/*---------------------------
| SEGURA VALENCIA FRANCISCO |
---------------------------*/

package com.compilador.codegen;

import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;

/**
 * Genera ensamblador real x86-64 (NASM) desde TAC optimizado.
 *
 * Convenciones:
 *   - Todas las variables en .bss como resq 1 (64-bit)
 *   - String literals en .data
 *   - Argumentos de función: rdi, rsi, rdx, rcx, r8, r9 (System V AMD64 ABI)
 *   - Retorno en rax
 *   - "getparam a" → mov [a], rdi  (primer parámetro formal)
 *   - "param x"    → se acumulan y se emiten como mov rdi,[x] / mov rsi,[y] antes del CALL
 *   - main → _start con exit syscall al final
 */
public class CodeGeneratorNASM {

    // ── Registro de parámetros ABI ─────────────────────────────────────────
    private static final String[] ARG_REGS = {"rdi", "rsi", "rdx", "rcx", "r8", "r9"};

    // ── Resultado ──────────────────────────────────────────────────────────
    public static class Resultado {
        public final String codigoNASM;
        public Resultado(String codigoNASM) { this.codigoNASM = codigoNASM; }
    }

    // ── Entry point ────────────────────────────────────────────────────────

    public static Resultado generar(List<String> tacOpt) {
        // Recolectar variables, string literals
        Set<String>         vars    = new LinkedHashSet<>();
        Map<String, String> strData = new LinkedHashMap<>();  // label → value
        int[]               strIdx  = {0};

        // Pre-scan pass 1: collect all TAC label names so we can exclude them from .bss
        Set<String> tacLabels = new HashSet<>();
        for (String raw : tacOpt) {
            String t = raw.trim();
            if (t.endsWith(":") && !t.contains(" "))
                tacLabels.add(t.substring(0, t.length() - 1));
        }

        // Pre-scan pass 2: collect all variables and string literals
        Set<String>         strVars = new HashSet<>();  // variables holding strings
        for (String raw : tacOpt) {
            String t = raw.trim();
            if (t.isEmpty() || t.endsWith(":") || t.startsWith("begin ")
                    || t.startsWith("end ") || t.startsWith("goto ")) continue;
            if (t.startsWith("if ") && t.contains("then")) continue;

            String lhs = getLHS(t);
            if (lhs != null) {
                String rhs = t.substring(t.indexOf('=') + 1).trim();
                if (rhs.startsWith("\"") || rhs.startsWith("'")) {
                    strVars.add(lhs);
                }
            }
        }

        // Pre-scan pass 3: collect vars/bss entries
        for (String raw : tacOpt) {
            String t = raw.trim();
            if (t.isEmpty() || t.endsWith(":") || t.startsWith("begin ")
                    || t.startsWith("end ") || t.startsWith("goto ")) continue;
            if (t.startsWith("if ") && t.contains("then")) continue;

            // getparam: formal param – needs bss entry
            if (t.startsWith("getparam ")) {
                String v = t.substring(9).trim();
                if (isVar(v) && !tacLabels.contains(v)) vars.add(v);
                continue;
            }
            // param: argument variable
            if (t.startsWith("param ")) {
                String v = t.substring(6).trim();
                if (isVar(v) && !tacLabels.contains(v)) vars.add(v);
                continue;
            }

            String lhs = getLHS(t);
            if (lhs != null && isVar(lhs) && !tacLabels.contains(lhs)) vars.add(lhs);

            // Scan RHS tokens – skip call/create (don't extract method names)
            String rhs = lhs != null ? t.substring(t.indexOf('=') + 1).trim() : t;
            if (rhs.startsWith("call ") || rhs.startsWith("create ")) continue;
            if (rhs.startsWith("\"") || rhs.startsWith("'")) {
                String content = rhs;
                if (!strData.containsValue(content))
                    strData.put("str" + (strIdx[0]++), content);
            } else {
                for (String tok : rhs.split("[^a-zA-Z0-9_]+")) {
                    if (isVar(tok) && !tacLabels.contains(tok)) vars.add(tok);
                }
            }
        }

        // ── Build output ──────────────────────────────────────────────────
        StringBuilder sb = new StringBuilder();
        sb.append("; ══════════════════════════════════════════════════════\n");
        sb.append("; Ensamblador x86-64 (NASM) — Compilador C++\n");
        sb.append("; SEGURA VALENCIA FRANCISCO\n");
        sb.append("; Traductores de Lenguajes — UDG CUTonalá\n");
        sb.append("; nasm -f elf64 out.asm && ld out.o -o out && ./out\n");
        sb.append("; ══════════════════════════════════════════════════════\n\n");

        // .data (newline, print buffer, string literals)
        sb.append("section .data\n");
        sb.append("    _nl: db 10\n");
        sb.append("    _print_buf: times 32 db 0\n");
        if (!strData.isEmpty()) {
            for (Map.Entry<String, String> e : strData.entrySet()) {
                String raw = e.getValue();
                String inner = raw.length() >= 2 ? raw.substring(1, raw.length() - 1) : raw;
                sb.append("    ").append(e.getKey()).append(": db `")
                  .append(inner.replace("`", "\\`")).append("`, 0\n");
            }
        }
        sb.append("\n");

        // .bss (all variables as 64-bit qwords)
        sb.append("section .bss\n");
        for (String v : vars) {
            sb.append("    ").append(nasm(v)).append(": resq 1\n");
        }
        sb.append("\n");

        // .text
        sb.append("section .text\n");
        sb.append("    global _start\n\n");

        // Translate instructions
        List<String> paramBuf   = new ArrayList<>();  // buffered param args before call
        int[]        getparamIdx = {0};              // index for getparam → register
        String[]     currentFunc = {null};
        boolean[]    inMain      = {false};
        boolean[]    lastWasRet  = {false};           // skip redundant pop+ret in end X

        for (String raw : tacOpt) {
            String t = raw.trim();
            if (t.isEmpty()) { sb.append("\n"); continue; }

            // Reset "last was return" flag unless we're about to emit return/end
            if (!t.startsWith("return") && !t.startsWith("end "))
                lastWasRet[0] = false;

            // ── begin/end ─────────────────────────────────────────────────
            if (t.startsWith("begin ")) {
                String fn = t.substring(6).trim();
                currentFunc[0] = fn;
                getparamIdx[0] = 0;
                inMain[0] = fn.equals("main");
                sb.append("; ── ").append(fn).append(" ───────────────────────────────────\n");
                if (inMain[0]) {
                    sb.append("_start:\n");
                } else {
                    sb.append("_").append(nasm(fn)).append(":\n");
                    sb.append("    push rbp\n");
                    sb.append("    mov  rbp, rsp\n");
                }
                continue;
            }
            if (t.startsWith("end ")) {
                String fn = t.substring(4).trim();
                if (!lastWasRet[0]) {
                    if (fn.equals("main")) {
                        sb.append("    xor  rdi, rdi\n");
                        sb.append("    mov  rax, 60\n");
                        sb.append("    syscall\n");
                    } else {
                        sb.append("    pop  rbp\n");
                        sb.append("    ret\n");
                    }
                }
                sb.append("\n");
                inMain[0] = false;
                currentFunc[0] = null;
                lastWasRet[0] = false;
                continue;
            }

            // ── getparam a ────────────────────────────────────────────────
            if (t.startsWith("getparam ")) {
                String v   = t.substring(9).trim();
                int    idx = getparamIdx[0]++;
                if (idx < ARG_REGS.length) {
                    sb.append("    mov  qword [").append(nasm(v)).append("], ")
                      .append(ARG_REGS[idx]).append("\n");
                } else {
                    // Stack param (offset from rbp+16 for idx 6,7,...)
                    int stackOff = 16 + (idx - ARG_REGS.length) * 8;
                    sb.append("    mov  rax, qword [rbp+").append(stackOff).append("]\n");
                    sb.append("    mov  qword [").append(nasm(v)).append("], rax\n");
                }
                continue;
            }

            // ── param x (acumular para próximo call) ──────────────────────
            if (t.startsWith("param ")) {
                paramBuf.add(t.substring(6).trim());
                continue;
            }

            // ── etiqueta ──────────────────────────────────────────────────
            if (t.endsWith(":") && !t.contains(" ")) {
                sb.append(t).append("\n");
                continue;
            }

            // ── goto L ────────────────────────────────────────────────────
            if (t.startsWith("goto ")) {
                sb.append("    jmp  ").append(t.substring(5).trim()).append("\n");
                continue;
            }

            // printnl
            if (t.equals("printnl")) {
                sb.append("    mov  rax, 1\n");
                sb.append("    mov  rdi, 1\n");
                sb.append("    lea  rsi, [rel _nl]\n");
                sb.append("    mov  rdx, 1\n");
                sb.append("    syscall\n");
                continue;
            }

            // print expr
            if (t.startsWith("print ")) {
                String val = t.substring(6).trim();
                if (strVars.contains(val)) {
                    // print string: val holds address of string in .data
                    sb.append("    mov  rax, qword [").append(nasm(val)).append("]\n");
                    sb.append("    mov  rsi, rax\n");
                    sb.append("    ; strlen\n");
                    sb.append("    mov  rcx, -1\n");
                    sb.append("    mov  rdi, rsi\n");
                    sb.append("    xor  al, al\n");
                    sb.append("    cld\n");
                    sb.append("    repne scasb\n");
                    sb.append("    not  rcx\n");
                    sb.append("    dec  rcx\n");
                    sb.append("    mov  rdx, rcx\n");
                    sb.append("    mov  rax, 1\n");
                    sb.append("    mov  rdi, 1\n");
                    sb.append("    syscall\n");
                } else {
                    sb.append("    ").append(loadToRAX(val)).append("\n");
                    sb.append("    call _print_int\n");
                }
                continue;
            }

            // ── ifFalse cond goto L ───────────────────────────────────────
            if (t.startsWith("ifFalse ")) {
                String[] pts = t.substring(8).split("\\s+goto\\s+", 2);
                if (pts.length == 2) {
                    sb.append("    cmp  qword [").append(nasm(pts[0].trim()))
                      .append("], 0\n");
                    sb.append("    je   ").append(pts[1].trim()).append("\n");
                }
                continue;
            }

            // ── if t != val goto L  (switch) ─────────────────────────────
            if (t.startsWith("if ") && t.contains(" != ") && t.contains(" goto ")) {
                String[] parts = t.substring(3).trim().split("\\s+");
                if (parts.length >= 5) {
                    String cond = parts[0], val = parts[2], lbl = parts[4];
                    sb.append("    mov  rax, qword [").append(nasm(cond)).append("]\n");
                    sb.append("    cmp  rax, ").append(literal(val)).append("\n");
                    sb.append("    jne  ").append(lbl).append("\n");
                }
                continue;
            }

            // ── if cond goto L  (do-while) ────────────────────────────────
            if (t.startsWith("if ") && t.contains(" goto ") && !t.contains(" != ")
                    && !t.contains("== 0") && !t.contains("then")) {
                String[] pts = t.substring(3).split("\\s+goto\\s+", 2);
                if (pts.length == 2) {
                    String cond = pts[0].trim().split("\\s+")[0];
                    sb.append("    cmp  qword [").append(nasm(cond)).append("], 0\n");
                    sb.append("    jne  ").append(pts[1].trim()).append("\n");
                }
                continue;
            }

            // ── if b == 0 then error ──────────────────────────────────────
            if (t.startsWith("if ") && t.contains("== 0") && t.contains("then")) {
                String[] parts = t.substring(3).trim().split("\\s+");
                sb.append("    cmp  qword [").append(nasm(parts[0])).append("], 0\n");
                sb.append("    je   _div_by_zero\n");
                continue;
            }

            // ── return val / return ───────────────────────────────────────
            if (t.startsWith("return ")) {
                String val = t.substring(7).trim();
                sb.append("    ").append(loadToRAX(val)).append("\n");
                if (!inMain[0]) {
                    sb.append("    pop  rbp\n");
                    sb.append("    ret\n");
                } else {
                    sb.append("    mov  rdi, rax\n");
                    sb.append("    mov  rax, 60\n");
                    sb.append("    syscall\n");
                }
                lastWasRet[0] = true;
                continue;
            }
            if (t.equals("return")) {
                if (!inMain[0]) {
                    sb.append("    pop  rbp\n");
                    sb.append("    ret\n");
                }
                lastWasRet[0] = true;
                continue;
            }

            // ── void call: "call obj.method, N" ──────────────────────────
            if (t.startsWith("call ") && getLHS(t) == null) {
                String callee = t.substring(5).trim();
                emitCall(sb, callee, paramBuf);
                paramBuf.clear();
                continue;
            }

            // ── assignments: lhs = rhs ────────────────────────────────────
            String lhs = getLHS(t);
            if (lhs != null) {
                String rhs = t.substring(t.indexOf('=') + 1).trim();

                // create ClassName → noop placeholder
                if (rhs.startsWith("create ")) {
                    sb.append("    mov  qword [").append(nasm(lhs)).append("], 1")
                      .append("  ; create ").append(rhs.substring(7).trim()).append("\n");
                    continue;
                }

                // call result: lhs = call obj.method, N
                if (rhs.startsWith("call ")) {
                    String callee = rhs.substring(5).trim();
                    emitCall(sb, callee, paramBuf);
                    paramBuf.clear();
                    sb.append("    mov  qword [").append(nasm(lhs)).append("], rax\n");
                    continue;
                }

                // String literal
                if (rhs.startsWith("\"") || rhs.startsWith("'")) {
                    // find matching label in strData
                    String inner = rhs.length() >= 2 ? rhs.substring(1, rhs.length()-1) : rhs;
                    String lbl   = strData.entrySet().stream()
                        .filter(e -> e.getValue().equals(rhs))
                        .map(Map.Entry::getKey).findFirst()
                        .orElse("str0");
                    sb.append("    lea  rax, [rel ").append(lbl).append("]\n");
                    sb.append("    mov  qword [").append(nasm(lhs)).append("], rax\n");
                    continue;
                }

                String[] tok = rhs.split("\\s+", 3);

                // Binary: lhs = a OP b
                if (tok.length == 3) {
                    String a = tok[0], op = tok[1], b = tok[2];
                    emitBinary(sb, lhs, a, op, b);
                    continue;
                }

                // Unary negation: -tN
                if (tok[0].startsWith("-") && !tok[0].matches("-?\\d+")) {
                    String a = tok[0].substring(1);
                    sb.append("    ").append(loadToRAX(a)).append("\n");
                    sb.append("    neg  rax\n");
                    sb.append("    mov  qword [").append(nasm(lhs)).append("], rax\n");
                    continue;
                }

                // Unary not: !tN
                if (tok[0].startsWith("!")) {
                    String a = tok[0].substring(1);
                    sb.append("    ").append(loadToRAX(a)).append("\n");
                    sb.append("    test rax, rax\n");
                    sb.append("    setz al\n");
                    sb.append("    movzx rax, al\n");
                    sb.append("    mov  qword [").append(nasm(lhs)).append("], rax\n");
                    continue;
                }

                // Simple: lhs = val (variable or literal)
                String val = tok.length == 2 ? tok[0] + " " + tok[1] : tok[0];
                sb.append("    ").append(loadToRAX(val.trim())).append("\n");
                sb.append("    mov  qword [").append(nasm(lhs)).append("], rax\n");
                continue;
            }

            // Fallback: emit as comment
            sb.append("    ; [sin mapa] ").append(t).append("\n");
            lastWasRet[0] = false;
        }

        // ── Error handler (reachable if division by zero) ─────────────────
        sb.append("\n; ── Print integer helper ─────────────────────────────\n");
        sb.append("_print_int:\n");
        sb.append("    push rax\n");
        sb.append("    push rbx\n");
        sb.append("    push rcx\n");
        sb.append("    push rdx\n");
        sb.append("    push rsi\n");
        sb.append("    push rdi\n");
        sb.append("    mov  rbx, 10\n");
        sb.append("    lea  rcx, [rel _print_buf + 31]\n");
        sb.append("    mov  byte [rcx], 10      ; newline at end\n");
        sb.append("    dec  rcx\n");
        sb.append("    mov  byte [rcx], 0       ; null terminator\n");
        sb.append("    dec  rcx\n");
        sb.append(".convert:\n");
        sb.append("    xor  rdx, rdx\n");
        sb.append("    div  rbx\n");
        sb.append("    add  dl, '0'\n");
        sb.append("    mov  byte [rcx], dl\n");
        sb.append("    dec  rcx\n");
        sb.append("    test rax, rax\n");
        sb.append("    jnz  .convert\n");
        sb.append("    inc  rcx                 ; point to first digit\n");
        sb.append("    mov  rsi, rcx\n");
        sb.append("    lea  rdi, [rel _print_buf + 30]\n");
        sb.append("    sub  rdi, rsi            ; length\n");
        sb.append("    mov  rdx, rdi\n");
        sb.append("    mov  rax, 1\n");
        sb.append("    mov  rdi, 1              ; stdout\n");
        sb.append("    syscall\n");
        sb.append("    pop  rdi\n");
        sb.append("    pop  rsi\n");
        sb.append("    pop  rdx\n");
        sb.append("    pop  rcx\n");
        sb.append("    pop  rbx\n");
        sb.append("    pop  rax\n");
        sb.append("    ret\n");
        sb.append("\n_div_by_zero:\n");
        sb.append("    ; División por cero — salir con código 1\n");
        sb.append("    mov  rdi, 1\n");
        sb.append("    mov  rax, 60\n");
        sb.append("    syscall\n");

        return new Resultado(sb.toString());
    }

    // ── Emitir CALL con argumentos en registros ────────────────────────────

    private static void emitCall(StringBuilder sb, String callee,
                                   List<String> paramBuf) {
        // callee = "mat.multiplicar, 2" or "multiplicar, 2"
        String methodName = callee.replaceAll(",.*", "").trim();
        // Extract only the method name after the dot (if present)
        if (methodName.contains(".")) {
            methodName = methodName.substring(methodName.lastIndexOf(".") + 1);
        }
        String label = methodName.replace(".", "_");

        // Load buffered params into ABI registers
        for (int i = 0; i < paramBuf.size() && i < ARG_REGS.length; i++) {
            sb.append("    ").append(loadToReg(paramBuf.get(i), ARG_REGS[i])).append("\n");
        }
        sb.append("    call _").append(label).append("\n");
    }

    // ── Emitir operación binaria ───────────────────────────────────────────

    private static void emitBinary(StringBuilder sb,
                                    String lhs, String a, String op, String b) {
        switch (op) {
            case "+":
                sb.append("    ").append(loadToRAX(a)).append("\n");
                sb.append("    add  rax, ").append(operand(b)).append("\n");
                sb.append("    mov  qword [").append(nasm(lhs)).append("], rax\n");
                break;
            case "-":
                sb.append("    ").append(loadToRAX(a)).append("\n");
                sb.append("    sub  rax, ").append(operand(b)).append("\n");
                sb.append("    mov  qword [").append(nasm(lhs)).append("], rax\n");
                break;
            case "*":
                sb.append("    ").append(loadToRAX(a)).append("\n");
                sb.append("    ").append(loadToReg(b, "rbx")).append("\n");
                sb.append("    imul rax, rbx\n");
                sb.append("    mov  qword [").append(nasm(lhs)).append("], rax\n");
                break;
            case "/":
                sb.append("    ").append(loadToRAX(a)).append("\n");
                sb.append("    ").append(loadToReg(b, "rbx")).append("\n");
                sb.append("    xor  rdx, rdx\n");
                sb.append("    idiv rbx\n");
                sb.append("    mov  qword [").append(nasm(lhs)).append("], rax\n");
                break;
            case "==": case "!=": case "<": case ">": case "<=": case ">=":
                sb.append("    ").append(loadToRAX(a)).append("\n");
                sb.append("    cmp  rax, ").append(operand(b)).append("\n");
                sb.append("    ").append(setCC(op)).append(" al\n");
                sb.append("    movzx rax, al\n");
                sb.append("    mov  qword [").append(nasm(lhs)).append("], rax\n");
                break;
            case "&&":
                sb.append("    ").append(loadToRAX(a)).append("\n");
                sb.append("    test rax, rax\n");
                sb.append("    setnz cl\n");
                sb.append("    ").append(loadToReg(b, "rax")).append("\n");
                sb.append("    test rax, rax\n");
                sb.append("    setnz al\n");
                sb.append("    and  al, cl\n");
                sb.append("    movzx rax, al\n");
                sb.append("    mov  qword [").append(nasm(lhs)).append("], rax\n");
                break;
            case "||":
                sb.append("    ").append(loadToRAX(a)).append("\n");
                sb.append("    ").append(loadToReg(b, "rbx")).append("\n");
                sb.append("    or   rax, rbx\n");
                sb.append("    setnz al\n");
                sb.append("    movzx rax, al\n");
                sb.append("    mov  qword [").append(nasm(lhs)).append("], rax\n");
                break;
            default:
                sb.append("    ").append(loadToRAX(a)).append("\n");
                sb.append("    mov  qword [").append(nasm(lhs)).append("], rax\n");
        }
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    private static String nasm(String name) {
        // avoid NASM reserved words
        return name.equals("div") ? "var_div" : name;
    }

    private static String loadToRAX(String val) {
        if (val == null || val.isEmpty()) return "xor rax, rax";
        if (val.equals("true"))  return "mov rax, 1";
        if (val.equals("false")) return "xor rax, rax";
        if (val.matches("-?\\d+")) return "mov rax, " + val;
        if (val.matches("-?\\d+\\.\\d+")) {
            int intPart = (int) Double.parseDouble(val);
            return "mov rax, " + intPart;
        }
        return "mov rax, qword [" + nasm(val) + "]";
    }

    private static String loadToReg(String val, String reg) {
        if (val == null || val.isEmpty()) return "xor " + reg + ", " + reg;
        if (val.equals("true"))  return "mov " + reg + ", 1";
        if (val.equals("false")) return "xor " + reg + ", " + reg;
        if (val.matches("-?\\d+")) return "mov " + reg + ", " + val;
        if (val.matches("-?\\d+\\.\\d+")) {
            int intPart = (int) Double.parseDouble(val);
            return "mov " + reg + ", " + intPart;
        }
        return "mov " + reg + ", qword [" + nasm(val) + "]";
    }

    private static String operand(String val) {
        if (val == null || val.isEmpty()) return "0";
        if (val.equals("true"))  return "1";
        if (val.equals("false")) return "0";
        if (val.matches("-?\\d+")) return val;
        if (val.matches("-?\\d+\\.\\d+")) {
            return String.valueOf((int) Double.parseDouble(val));
        }
        return "qword [" + nasm(val) + "]";
    }

    private static String literal(String val) {
        if (val.matches("-?\\d+")) return val;
        return "qword [" + nasm(val) + "]";
    }

    private static String setCC(String op) {
        switch (op) {
            case "==": return "sete";
            case "!=": return "setne";
            case "<":  return "setl";
            case ">":  return "setg";
            case "<=": return "setle";
            case ">=": return "setge";
            default:   return "sete";
        }
    }

    private static String getLHS(String inst) {
        if (inst == null || inst.isEmpty() || inst.endsWith(":")) return null;
        int eq = inst.indexOf('=');
        if (eq <= 0) return null;
        String lhs = inst.substring(0, eq).trim();
        return (lhs.contains(" ") || lhs.isEmpty()) ? null : lhs;
    }

    private static boolean isVar(String s) {
        return s != null && s.matches("[a-zA-Z_][a-zA-Z0-9_]*")
            && !s.equals("true") && !s.equals("false")
            && !s.equals("call") && !s.equals("create")
            && !s.equals("then") && !s.equals("error")
            && !s.equals("goto") && !s.equals("ifFalse")
            && !s.equals("if")   && !s.equals("begin")
            && !s.equals("end")  && !s.equals("return")
            && !s.equals("read") && !s.equals("param")
            && !s.equals("getparam") && !s.equals("print") && !s.equals("printnl");
    }

    // ── File I/O ───────────────────────────────────────────────────────────

    public static String guardarNASM(Resultado r, String base) throws IOException {
        String path = base + ".nasm";
        Files.write(Paths.get(path), r.codigoNASM.getBytes(StandardCharsets.UTF_8));
        return path;
    }

    /**
     * Ensambla + enlaza + ejecuta. Requiere nasm y ld instalados.
     * Retorna [exitCode, stdout, stderr].
     */
    public static int[] compilarYEjecutar(String nasmPath, String base) throws IOException, InterruptedException {
        String objPath = base + ".o";
        String exePath = base + ".run";

        // nasm -f elf64 -o base.o base.nasm
        Process nasm = new ProcessBuilder("nasm", "-f", "elf64", "-o", objPath, nasmPath)
            .redirectErrorStream(true).start();
        String nasmOut = new String(nasm.getInputStream().readAllBytes());
        int nasmExit = nasm.waitFor();
        if (nasmExit != 0) {
            System.err.println("  nasm error:\n" + nasmOut);
            return new int[]{nasmExit};
        }

        // ld -o base.run base.o
        Process ld = new ProcessBuilder("ld", "-o", exePath, objPath)
            .redirectErrorStream(true).start();
        String ldOut = new String(ld.getInputStream().readAllBytes());
        int ldExit = ld.waitFor();
        if (ldExit != 0) {
            System.err.println("  ld error:\n" + ldOut);
            return new int[]{ldExit};
        }

        // ./base.run
        Process run = new ProcessBuilder(exePath)
            .redirectErrorStream(false).start();
        String stdout = new String(run.getInputStream().readAllBytes());
        int runExit = run.waitFor();

        if (!stdout.isEmpty()) System.out.println("  stdout: " + stdout);
        return new int[]{runExit};
    }
}
