/*---------------------------
| SEGURA VALENCIA FRANCISCO |
---------------------------*/

package com.compilador.codegen;

import java.util.*;
import java.io.IOException;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;

public class CodeGenerator {

    // ── Public types ───────────────────────────────────────────────────────

    public static class InstruccionASM {
        public final int    direccion;
        public final String instruccion;
        public final String comentario;

        public InstruccionASM(int direccion, String instruccion, String comentario) {
            this.direccion   = direccion;
            this.instruccion = instruccion;
            this.comentario  = comentario;
        }

        public String formatear() {
            boolean esEtiqueta  = instruccion.endsWith(":");
            boolean esComentario = instruccion.startsWith(";");
            String hex = (esEtiqueta || esComentario)
                ? "        " : String.format("0x%04X", direccion);
            if (comentario != null && !comentario.isEmpty())
                return String.format("%-8s  %-38s ; %s", hex, instruccion, comentario);
            return String.format("%-8s  %s", hex, instruccion);
        }
    }

    public static class Resultado {
        public final List<InstruccionASM> instrucciones;
        public final Map<String, Integer> tablaSimbolos;
        public final String               codigoASM;
        public final byte[]               ejecutable;

        public Resultado(List<InstruccionASM> instrucciones,
                         Map<String, Integer>  tablaSimbolos,
                         String                codigoASM,
                         byte[]                ejecutable) {
            this.instrucciones = instrucciones;
            this.tablaSimbolos = tablaSimbolos;
            this.codigoASM     = codigoASM;
            this.ejecutable    = ejecutable;
        }
    }

    // ── Constants ──────────────────────────────────────────────────────────
    private static final int CODE_BASE  = 0x1000;
    private static final int DATA_BASE  = 0x2000;
    private static final int INSTR_SIZE = 4;
    private static final int VAR_SIZE   = 4;

    // ── Entry point ────────────────────────────────────────────────────────

    public static Resultado generar(List<String> tacOpt) {
        Map<String, Integer> tabla = new LinkedHashMap<>();
        asignarDirecciones(tacOpt, tabla);

        List<InstruccionASM> instrs = new ArrayList<>();
        int addr = CODE_BASE;
        for (String raw : tacOpt) {
            String t = raw.trim();
            if (t.isEmpty()) continue;
            List<InstruccionASM> emitidas = traducir(t, tabla, addr);
            instrs.addAll(emitidas);
            addr += emitidas.stream().filter(CodeGenerator::esReal).count() * INSTR_SIZE;
        }

        String asm = construirASM(instrs, tabla);
        byte[] bin = generarEjecutable(instrs, tabla);
        return new Resultado(instrs, tabla, asm, bin);
    }

    // ── File output ────────────────────────────────────────────────────────

    public static List<String> guardarArchivos(Resultado r, String base) throws IOException {
        List<String> archivos = new ArrayList<>();
        String asmPath = base + ".asm";
        String binPath = base + ".bin";
        Files.write(Paths.get(asmPath), r.codigoASM.getBytes(StandardCharsets.UTF_8));
        Files.write(Paths.get(binPath), r.ejecutable);
        archivos.add(asmPath);
        archivos.add(binPath);
        return archivos;
    }

    // ── ASM text builder ───────────────────────────────────────────────────

    private static String construirASM(List<InstruccionASM> instrs,
                                        Map<String, Integer> tabla) {
        StringBuilder sb = new StringBuilder();
        sb.append("; ══════════════════════════════════════════════════════\n");
        sb.append("; Código Ensamblador — Compilador C++\n");
        sb.append("; SEGURA VALENCIA FRANCISCO\n");
        sb.append("; Traductores de Lenguajes — UDG CUTonalá\n");
        sb.append("; ══════════════════════════════════════════════════════\n\n");

        sb.append("section .data\n");
        for (Map.Entry<String, Integer> e : tabla.entrySet())
            sb.append(String.format("    %-14s dd 0        ; addr 0x%04X%n",
                e.getKey() + ":", e.getValue()));

        sb.append("\nsection .text\n    global _start\n_start:\n");
        for (InstruccionASM a : instrs) {
            if (a.instruccion.startsWith(";")) {
                sb.append("\n    ").append(a.instruccion).append("\n");
            } else {
                sb.append("    ").append(a.formatear()).append("\n");
            }
        }
        sb.append("\n    HALT                    ; fin del programa\n");
        return sb.toString();
    }

    // ── Executable generation ──────────────────────────────────────────────

    private static byte[] generarEjecutable(List<InstruccionASM> instrs,
                                             Map<String, Integer>  tabla) {
        List<InstruccionASM> code = new ArrayList<>();
        for (InstruccionASM a : instrs) if (esReal(a)) code.add(a);

        int codeSize = code.size() * 8;
        int dataSize = tabla.size() * VAR_SIZE;
        byte[] bin   = new byte[18 + codeSize + dataSize];
        int    off   = 0;

        // Header (18 bytes): magic(4) + version(2) + entry(4) + codeSize(4) + dataSize(4)
        bin[off++]='E'; bin[off++]='X'; bin[off++]='E'; bin[off++]='C';
        bin[off++]=0x01; bin[off++]=0x00;
        putInt(bin, off, CODE_BASE); off += 4;
        putInt(bin, off, codeSize);  off += 4;
        putInt(bin, off, dataSize);  off += 4;

        // Code section: each instr → 8 bytes (opcode<<24|addr, 0)
        Map<String, Integer> opcodes = buildOpcodes();
        for (InstruccionASM a : code) {
            String mnem = a.instruccion.split("\\s+")[0].toUpperCase();
            int op = opcodes.getOrDefault(mnem, 0xFF);
            putInt(bin, off, (op << 24) | (a.direccion & 0x00FFFFFF)); off += 4;
            putInt(bin, off, 0);                                        off += 4;
        }
        // Data section: zeroed by new byte[]
        return bin;
    }

    private static Map<String, Integer> buildOpcodes() {
        Map<String, Integer> m = new LinkedHashMap<>();
        m.put("LOAD",    0x01); m.put("STORE",   0x02); m.put("MOV",     0x03);
        m.put("ADD",     0x10); m.put("SUB",     0x11); m.put("MUL",     0x12);
        m.put("DIV",     0x13); m.put("AND",     0x14); m.put("OR",      0x15);
        m.put("NOT",     0x16); m.put("NEG",     0x17); m.put("EQ",      0x18);
        m.put("NEQ",     0x19); m.put("LT",      0x1A); m.put("GT",      0x1B);
        m.put("LE",      0x1C); m.put("GE",      0x1D); m.put("CMP",     0x20);
        m.put("JMP",     0x30); m.put("JEQ",     0x31); m.put("JNE",     0x32);
        m.put("JGE",     0x33); m.put("JLE",     0x34); m.put("CALL",    0x40);
        m.put("RET",     0x41); m.put("PUSH",    0x50); m.put("POP",     0x51);
        m.put("SYSCALL", 0x60); m.put("HALT",    0xFF);
        return m;
    }

    // ── Memory address assignment ──────────────────────────────────────────

    private static void asignarDirecciones(List<String> tac,
                                            Map<String, Integer> tabla) {
        // Collect label names to avoid treating them as variables
        Set<String> labels = new HashSet<>();
        for (String raw : tac) {
            String t = raw.trim();
            if (t.endsWith(":") && !t.contains(" "))
                labels.add(t.substring(0, t.length() - 1));
        }

        int addr = DATA_BASE;
        for (String raw : tac) {
            String t = raw.trim();
            if (t.isEmpty() || t.endsWith(":")) continue;
            if (t.startsWith("begin ") || t.startsWith("end ")) continue;
            if (t.startsWith("goto ")) continue;

            String lhs = getLHS(t);

            if (lhs != null) {
                // Register LHS variable
                if (isVar(lhs) && !labels.contains(lhs) && !tabla.containsKey(lhs)) {
                    tabla.put(lhs, addr);
                    addr += VAR_SIZE;
                }
                // Register variables from RHS (only for assignments, not call/create rhs)
                String rhs = t.substring(t.indexOf('=') + 1).trim();
                if (!rhs.startsWith("create ") && !rhs.startsWith("call ")
                        && !rhs.startsWith("\"") && !rhs.startsWith("'")) {
                    for (String tok : rhs.split("[^a-zA-Z0-9_]+")) {
                        tok = tok.trim();
                        if (isVar(tok) && !labels.contains(tok) && !tabla.containsKey(tok)) {
                            tabla.put(tok, addr);
                            addr += VAR_SIZE;
                        }
                    }
                }
            } else {
                // Non-assignment: extract condition variable only (first identifier after keyword)
                String condVar = null;
                if (t.startsWith("ifFalse ")) {
                    condVar = t.substring(8).trim().split("\\s+")[0];
                } else if (t.startsWith("if ")) {
                    condVar = t.substring(3).trim().split("\\s+")[0];
                } else if (t.startsWith("return ")) {
                    condVar = t.substring(7).trim();
                }
                // For switch case: "if t10 != 1 goto L2" → also register the literal val (skip — literal)
                if (condVar != null && isVar(condVar)
                        && !labels.contains(condVar) && !tabla.containsKey(condVar)) {
                    tabla.put(condVar, addr);
                    addr += VAR_SIZE;
                }
            }
        }
    }

    // ── Instruction translation ────────────────────────────────────────────

    private static List<InstruccionASM> traducir(String t,
                                                   Map<String, Integer> tabla,
                                                   int base) {
        List<InstruccionASM> out = new ArrayList<>();

        // Label: "L1:"
        if (t.endsWith(":") && !t.contains(" ")) {
            out.add(new InstruccionASM(base, t, "etiqueta de salto"));
            return out;
        }
        // Scope markers: "begin X" / "end X"
        if (t.startsWith("begin ") || t.startsWith("end ")) {
            out.add(new InstruccionASM(base, "; " + t, null));
            return out;
        }
        // goto L
        if (t.startsWith("goto ")) {
            String lbl = t.substring(5).trim();
            out.add(asm(base, "JMP  " + lbl, "salto incondicional → " + lbl));
            return out;
        }
        // ifFalse cond goto L
        if (t.startsWith("ifFalse ")) {
            String rest = t.substring(8).trim();
            String[] pts = rest.split("\\s+goto\\s+", 2);
            if (pts.length == 2) {
                String cond = pts[0].trim();
                String lbl  = pts[1].trim();
                out.add(asm(base,                "LOAD R1, " + res(cond, tabla), "cargar condición '" + cond + "'"));
                out.add(asm(base +   INSTR_SIZE, "CMP  R1, #0",                  "evaluar si es falso"));
                out.add(asm(base + 2*INSTR_SIZE, "JEQ  " + lbl,                  "saltar a " + lbl + " si falso"));
            }
            return out;
        }
        // if cond goto L  (do-while back-jump)
        if (t.startsWith("if ") && t.contains(" goto ")
                && !t.contains(" != ") && !t.contains(" == ") && !t.contains("then")) {
            String rest = t.substring(3).trim();
            String[] pts = rest.split("\\s+goto\\s+", 2);
            if (pts.length == 2) {
                String cond = pts[0].trim().split("\\s+")[0];
                String lbl  = pts[1].trim();
                out.add(asm(base,                "LOAD R1, " + res(cond, tabla), "cargar condición '" + cond + "'"));
                out.add(asm(base +   INSTR_SIZE, "CMP  R1, #0",                  "evaluar si es verdadero"));
                out.add(asm(base + 2*INSTR_SIZE, "JNE  " + lbl,                  "saltar a " + lbl + " si verdadero"));
            }
            return out;
        }
        // if t != val goto L  (switch case check)
        if (t.startsWith("if ") && t.contains(" != ") && t.contains(" goto ")) {
            String[] parts = t.substring(3).trim().split("\\s+");
            if (parts.length >= 5) {
                String cond = parts[0];
                String val  = parts[2];
                String lbl  = parts[4];
                out.add(asm(base,                "LOAD R1, " + res(cond, tabla), "cargar selector switch"));
                out.add(asm(base +   INSTR_SIZE, "CMP  R1, " + res(val, tabla),  "comparar con caso " + val));
                out.add(asm(base + 2*INSTR_SIZE, "JNE  " + lbl,                  "saltar si no coincide"));
            }
            return out;
        }
        // if b == 0 then error "..."  (división por cero)
        if (t.startsWith("if ") && t.contains("== 0") && t.contains("then")) {
            String[] parts = t.substring(3).trim().split("\\s+");
            String divisor = parts[0];
            out.add(asm(base,                "LOAD R1, " + res(divisor, tabla), "verificar divisor '" + divisor + "'"));
            out.add(asm(base +   INSTR_SIZE, "CMP  R1, #0",                     "comparar con cero"));
            out.add(asm(base + 2*INSTR_SIZE, "JEQ  _div_error",                 "saltar a error si divisor = 0"));
            return out;
        }
        // return val
        if (t.startsWith("return ")) {
            String val = t.substring(7).trim();
            out.add(asm(base,              "LOAD R1, " + res(val, tabla), "cargar valor de retorno '" + val + "'"));
            out.add(asm(base + INSTR_SIZE, "RET",                          "retornar al llamador"));
            return out;
        }
        // return (void)
        if (t.equals("return")) {
            out.add(asm(base, "RET", "retornar (void)"));
            return out;
        }
        // read var (cin)
        if (t.startsWith("read ")) {
            String var = t.substring(5).trim();
            out.add(asm(base,              "SYSCALL #1",                         "leer desde entrada estándar"));
            out.add(asm(base + INSTR_SIZE, "STORE " + res(var, tabla) + ", R1",  "guardar entrada en '" + var + "'"));
            return out;
        }
        // Void call: "call obj.method, n"
        if (t.startsWith("call ") && getLHS(t) == null) {
            String callee = t.substring(5).trim();
            String methodName = callee.split(",")[0].trim();
            if (methodName.contains(".")) {
                methodName = methodName.substring(methodName.lastIndexOf(".") + 1);
            }
            out.add(asm(base, "CALL _" + methodName, "llamar " + callee));
            return out;
        }

        // ── Assignments: lhs = rhs ────────────────────────────────────────
        String lhs = getLHS(t);
        if (lhs != null) {
            String rhs = t.substring(t.indexOf('=') + 1).trim();

            // create ClassName
            if (rhs.startsWith("create ")) {
                String cn = rhs.substring(7).trim();
                out.add(asm(base,              "CALL _new_" + cn,                     "crear instancia de " + cn));
                out.add(asm(base + INSTR_SIZE, "STORE " + res(lhs, tabla) + ", R1",   "guardar referencia en '" + lhs + "'"));
                return out;
            }
            // call result
            if (rhs.startsWith("call ")) {
                String callee = rhs.substring(5).trim();
                String methodName = callee.split(",")[0].trim();
                if (methodName.contains(".")) {
                    methodName = methodName.substring(methodName.lastIndexOf(".") + 1);
                }
                out.add(asm(base,              "CALL _" + methodName,                       "llamar " + callee));
                out.add(asm(base + INSTR_SIZE, "STORE " + res(lhs, tabla) + ", R1",   "guardar retorno en '" + lhs + "'"));
                return out;
            }
            // String/char literal
            if (rhs.startsWith("\"") || rhs.startsWith("'")) {
                String display = rhs.length() > 18 ? rhs.substring(0, 16) + "…\"" : rhs;
                out.add(asm(base,              "LOAD R1, " + display,                "cargar literal de cadena"));
                out.add(asm(base + INSTR_SIZE, "STORE " + res(lhs, tabla) + ", R1",  "asignar a '" + lhs + "'"));
                return out;
            }

            String[] tok = rhs.split("\\s+", 3);

            // Binary: lhs = a OP b
            if (tok.length == 3) {
                String a  = tok[0];
                String op = tok[1];
                String b  = tok[2];
                String asmOp = opToASM(op);
                out.add(asm(base,                "LOAD R1, " + res(a, tabla),          "cargar '" + a + "'"));
                out.add(asm(base +   INSTR_SIZE, "LOAD R2, " + res(b, tabla),          "cargar '" + b + "'"));
                out.add(asm(base + 2*INSTR_SIZE, asmOp + "R1, R2",                    "R1 ← R1 " + op + " R2"));
                out.add(asm(base + 3*INSTR_SIZE, "STORE " + res(lhs, tabla) + ", R1", "guardar en '" + lhs + "'"));
                return out;
            }
            // Scalar / unary (length 1 or 2 joined without space)
            if (tok.length <= 2) {
                String val = tok.length == 2 ? tok[0] + " " + tok[1] : tok[0];
                // Unary negation without space: "-tN"
                if (val.startsWith("-") && !val.matches("-?\\d+(\\.\\d+)?")) {
                    String a = val.substring(1);
                    out.add(asm(base,                "LOAD R1, " + res(a, tabla),          "cargar '" + a + "'"));
                    out.add(asm(base +   INSTR_SIZE, "NEG  R1",                             "negar aritméticamente"));
                    out.add(asm(base + 2*INSTR_SIZE, "STORE " + res(lhs, tabla) + ", R1",  "guardar en '" + lhs + "'"));
                    return out;
                }
                // Unary not without space: "!tN"
                if (val.startsWith("!")) {
                    String a = val.substring(1);
                    out.add(asm(base,                "LOAD R1, " + res(a, tabla),          "cargar '" + a + "'"));
                    out.add(asm(base +   INSTR_SIZE, "NOT  R1",                             "negar lógicamente"));
                    out.add(asm(base + 2*INSTR_SIZE, "STORE " + res(lhs, tabla) + ", R1",  "guardar en '" + lhs + "'"));
                    return out;
                }
                // Simple assignment: lhs = value
                out.add(asm(base,              "LOAD R1, " + res(val.trim(), tabla),  "cargar '" + val.trim() + "'"));
                out.add(asm(base + INSTR_SIZE, "STORE " + res(lhs, tabla) + ", R1",   "asignar a '" + lhs + "'"));
                return out;
            }
        }

        // Fallback
        out.add(new InstruccionASM(base, "; [sin mapa] " + t, null));
        return out;
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    private static String res(String var, Map<String, Integer> tabla) {
        if (var == null || var.isEmpty()) return "#0";
        if (var.startsWith("\"") || var.startsWith("'")) return var;
        if (var.equals("true"))  return "#1";
        if (var.equals("false")) return "#0";
        if (var.matches("-?\\d+(\\.\\d+)?")) return "#" + var;
        if (tabla.containsKey(var)) return String.format("[0x%04X]", tabla.get(var));
        return var;
    }

    private static String opToASM(String op) {
        switch (op) {
            case "+":  return "ADD  ";
            case "-":  return "SUB  ";
            case "*":  return "MUL  ";
            case "/":  return "DIV  ";
            case "==": return "EQ   ";
            case "!=": return "NEQ  ";
            case "<":  return "LT   ";
            case ">":  return "GT   ";
            case "<=": return "LE   ";
            case ">=": return "GE   ";
            case "&&": return "AND  ";
            case "||": return "OR   ";
            default:   return "MOV  ";
        }
    }

    private static InstruccionASM asm(int addr, String instr, String comment) {
        return new InstruccionASM(addr, instr, comment);
    }

    private static boolean esReal(InstruccionASM a) {
        String s = a.instruccion;
        return !s.startsWith(";") && !s.endsWith(":");
    }

    private static boolean isVar(String s) {
        return s != null
            && s.matches("[a-zA-Z_][a-zA-Z0-9_]*")
            && !s.equals("true") && !s.equals("false")
            && !s.equals("call") && !s.equals("create")
            && !s.equals("then") && !s.equals("error")
            && !s.equals("goto") && !s.equals("ifFalse")
            && !s.equals("if") && !s.equals("begin")
            && !s.equals("end") && !s.equals("return")
            && !s.equals("read");
    }

    static String getLHS(String inst) {
        if (inst == null || inst.isEmpty() || inst.endsWith(":")) return null;
        int eq = inst.indexOf('=');
        if (eq <= 0) return null;
        String lhs = inst.substring(0, eq).trim();
        return (lhs.contains(" ") || lhs.isEmpty()) ? null : lhs;
    }

    static Set<String> getRHSVars(String inst) {
        Set<String> vars = new LinkedHashSet<>();
        if (inst == null || inst.isEmpty() || inst.endsWith(":")) return vars;
        int eq = inst.indexOf('=');
        String scan = eq > 0 ? inst.substring(eq + 1) : inst;
        for (String tok : scan.split("[^a-zA-Z0-9_]+")) {
            tok = tok.trim();
            if (isVar(tok)) vars.add(tok);
        }
        return vars;
    }

    private static void putInt(byte[] buf, int off, int val) {
        buf[off]   = (byte)((val >> 24) & 0xFF);
        buf[off+1] = (byte)((val >> 16) & 0xFF);
        buf[off+2] = (byte)((val >>  8) & 0xFF);
        buf[off+3] = (byte)( val        & 0xFF);
    }
}
