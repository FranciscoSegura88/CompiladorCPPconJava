/*---------------------------
| SEGURA VALENCIA FRANCISCO |
---------------------------*/

package com.compilador.tac;

import java.util.*;
import java.util.regex.*;

/**
 * Optimizador de código de tres direcciones.
 * Cuatro pasos en orden:
 *   1. Eliminación de código inalcanzable (tras goto incondicional)
 *   2. Eliminación de subexpresiones comunes (CSE)
 *   3. Eliminación de asignaciones redundantes (sobreescritura sin lectura)
 *   4. Eliminación de variables no usadas (asignadas pero nunca leídas)
 */
public class TACOptimizer {

    // ── Tipos públicos ─────────────────────────────────────────────────────

    public static class Cambio {
        public final String tipo;
        public final int    lineaOriginal;
        public final String instruccion;
        public final String razon;

        public Cambio(String tipo, int lineaOriginal, String instruccion, String razon) {
            this.tipo          = tipo;
            this.lineaOriginal = lineaOriginal;
            this.instruccion   = instruccion;
            this.razon         = razon;
        }
    }

    public static class Resultado {
        public final List<String> instrucciones;
        public final List<Cambio> cambios;

        public Resultado(List<String> instrucciones, List<Cambio> cambios) {
            this.instrucciones = instrucciones;
            this.cambios       = cambios;
        }
    }

    // ── Punto de entrada ───────────────────────────────────────────────────

    public static Resultado optimizar(List<String> original) {
        List<Cambio> cambios = new ArrayList<>();
        List<String> current = new ArrayList<>(original);

        current = paso1Inalcanzables(current, cambios);
        current = paso2SubexpresionesComuneS(current, cambios);
        current = paso3AsignacionesRedundantes(current, cambios);
        current = paso4VariablesNoUsadas(current, cambios);

        return new Resultado(current, cambios);
    }

    // ──────────────────────────────────────────────────────────────────────
    // PASO 1: Código inalcanzable tras salto incondicional goto
    // ──────────────────────────────────────────────────────────────────────
    private static List<String> paso1Inalcanzables(List<String> instrs, List<Cambio> cambios) {
        List<String> result    = new ArrayList<>();
        boolean      alcanzable = true;

        for (int i = 0; i < instrs.size(); i++) {
            String inst = instrs.get(i);
            String t    = inst.trim();

            if (t.isEmpty()) { result.add(inst); continue; }

            if (t.endsWith(":")) {
                // Etiqueta → restaura alcanzabilidad
                alcanzable = true;
                result.add(inst);
            } else if (!alcanzable) {
                cambios.add(new Cambio(
                    "Código inalcanzable", i + 1, t,
                    "Instrucción posterior a 'goto' incondicional nunca se ejecuta"));
            } else {
                result.add(inst);
                if (t.startsWith("goto ")) alcanzable = false;
            }
        }
        return result;
    }

    // ──────────────────────────────────────────────────────────────────────
    // PASO 2: Eliminación de subexpresiones comunes (CSE)
    // ──────────────────────────────────────────────────────────────────────
    // Patrón binario: tN = a OP b
    private static final Pattern BIN_PAT = Pattern.compile(
        "^(\\S+)\\s*=\\s*(\\S+)\\s*(==|!=|<=|>=|&&|\\|\\||[+\\-*/!<>])\\s*(\\S+)$"
    );

    private static List<String> paso2SubexpresionesComuneS(List<String> instrs, List<Cambio> cambios) {
        // "a OP b" → primer temporal que lo computó
        Map<String, String> exprATemp  = new LinkedHashMap<>();
        // viejo_temp → temp_reemplazante
        Map<String, String> reemplazos = new LinkedHashMap<>();
        // variables de usuario que fueron modificadas (invalidan el cache)
        Set<String>         modificadas = new HashSet<>();

        List<String> result = new ArrayList<>();

        for (int i = 0; i < instrs.size(); i++) {
            String inst = instrs.get(i);
            String t    = inst.trim();

            // Aplicar reemplazos acumulados
            String mod = aplicarReemplazos(t, reemplazos);

            // Límite de control: limpiar cache CSE
            if (t.endsWith(":") || t.startsWith("begin ") || t.startsWith("end ")) {
                exprATemp.clear();
                modificadas.clear();
                result.add(mod.equals(t) ? inst : "    " + mod);
                continue;
            }

            Matcher m = BIN_PAT.matcher(mod);
            if (m.matches()) {
                String lhs  = m.group(1);
                String arg1 = m.group(2);
                String op   = m.group(3);
                String arg2 = m.group(4);

                String key = arg1 + " " + op + " " + arg2;

                // Si algún operando fue modificado, invalidar esa entrada
                if (modificadas.contains(arg1) || modificadas.contains(arg2)) {
                    exprATemp.remove(key);
                }

                if (esOpAritmetico(op) && exprATemp.containsKey(key)) {
                    String anterior = exprATemp.get(key);
                    reemplazos.put(lhs, anterior);
                    cambios.add(new Cambio(
                        "Subexpresión común eliminada", i + 1, t,
                        "'" + lhs + " = " + key + "' duplica resultado de '" + anterior + "'"));
                    continue;   // eliminar esta instrucción
                } else if (esOpAritmetico(op)) {
                    exprATemp.put(key, lhs);
                }

                // Si LHS no es temporal, marcarlo como modificado
                if (!lhs.matches("t\\d+")) {
                    modificadas.add(lhs);
                    // Invalidar entradas del cache que usen esta variable
                    exprATemp.entrySet().removeIf(e -> {
                        for (String p : e.getKey().split("\\s+"))
                            if (p.equals(lhs)) return true;
                        return false;
                    });
                }
            } else {
                // Asignación simple u otra instrucción
                String lhs = getLHS(mod);
                if (lhs != null && !lhs.matches("t\\d+")) {
                    modificadas.add(lhs);
                    exprATemp.entrySet().removeIf(e -> {
                        for (String p : e.getKey().split("\\s+"))
                            if (p.equals(lhs)) return true;
                        return false;
                    });
                }
            }

            result.add(mod.equals(t) ? inst : "    " + mod);
        }
        return result;
    }

    private static boolean esOpAritmetico(String op) {
        switch (op) {
            case "+": case "-": case "*": case "/":
            case "==": case "!=": case "<": case ">": case "<=": case ">=":
            case "&&": case "||":
                return true;
            default:
                return false;
        }
    }

    private static String aplicarReemplazos(String inst, Map<String, String> reemplazos) {
        for (Map.Entry<String, String> e : reemplazos.entrySet()) {
            inst = inst.replaceAll(
                "(?<![a-zA-Z0-9_])" + Pattern.quote(e.getKey()) + "(?![a-zA-Z0-9_])",
                e.getValue());
        }
        return inst;
    }

    // ──────────────────────────────────────────────────────────────────────
    // PASO 3: Asignaciones redundantes (sobreescritura sin lectura intermedia)
    // ──────────────────────────────────────────────────────────────────────
    private static List<String> paso3AsignacionesRedundantes(List<String> instrs, List<Cambio> cambios) {
        boolean cambiado = true;
        List<String> current = new ArrayList<>(instrs);

        while (cambiado) {
            cambiado = false;
            Map<String, Integer> ultimaAsig = new LinkedHashMap<>();
            Set<Integer>         eliminar   = new HashSet<>();

            for (int i = 0; i < current.size(); i++) {
                String t = current.get(i).trim();
                if (t.isEmpty()) continue;

                // Límite de bloque: resetear seguimiento
                if (t.endsWith(":") || t.startsWith("begin ") || t.startsWith("end ")
                        || t.startsWith("ifFalse ") || t.startsWith("goto ")
                        || t.startsWith("if ") || t.startsWith("return")) {
                    // Leer vars de control flow
                    for (String v : getVarsRHS(t)) ultimaAsig.remove(v);
                    ultimaAsig.clear();
                    continue;
                }

                String      lhs = getLHS(t);
                Set<String> rhs = getVarsRHS(t);

                for (String v : rhs) ultimaAsig.remove(v);  // var leída → no es redundante

                if (lhs != null) {
                    if (ultimaAsig.containsKey(lhs)) {
                        int prevIdx = ultimaAsig.get(lhs);
                        eliminar.add(prevIdx);
                        cambios.add(new Cambio(
                            "Asignación redundante", prevIdx + 1,
                            current.get(prevIdx).trim(),
                            "'" + lhs + "' sobreescrito antes de ser leído"));
                        cambiado = true;
                    }
                    ultimaAsig.put(lhs, i);
                }
            }

            if (!eliminar.isEmpty()) {
                List<String> next = new ArrayList<>();
                for (int i = 0; i < current.size(); i++)
                    if (!eliminar.contains(i)) next.add(current.get(i));
                current = next;
            }
        }
        return current;
    }

    // ──────────────────────────────────────────────────────────────────────
    // PASO 4: Variables no usadas (asignadas pero nunca leídas)
    // ──────────────────────────────────────────────────────────────────────
    private static List<String> paso4VariablesNoUsadas(List<String> instrs, List<Cambio> cambios) {
        boolean cambiado = true;
        List<String> current = new ArrayList<>(instrs);

        while (cambiado) {
            cambiado = false;

            // Todas las variables que aparecen en algún RHS
            Set<String> leidas = new HashSet<>();
            for (String inst : current) leidas.addAll(getVarsRHS(inst.trim()));

            List<String> next = new ArrayList<>();
            for (int i = 0; i < current.size(); i++) {
                String inst = current.get(i);
                String t    = inst.trim();
                String lhs  = getLHS(t);

                // Eliminar temporal (tN) nunca leído
                if (lhs != null && lhs.matches("t\\d+")
                        && !leidas.contains(lhs) && !tieneLlamada(t) && !tieneParam(t)) {
                    cambios.add(new Cambio(
                        "Variable temporal no usada", i + 1, t,
                        "'" + lhs + "' asignado pero nunca utilizado en instrucciones posteriores"));
                    cambiado = true;
                    continue;
                }

                // Eliminar variable de usuario nunca leída (sin efectos laterales)
                if (lhs != null && !lhs.matches("t\\d+")
                        && !leidas.contains(lhs)
                        && !tieneLlamada(t) && !t.contains("create ") && !tieneParam(t)) {
                    cambios.add(new Cambio(
                        "Variable no usada", i + 1, t,
                        "'" + lhs + "' asignado pero nunca utilizado en cálculos posteriores"));
                    cambiado = true;
                    continue;
                }

                next.add(inst);
            }
            current = next;
        }
        return current;
    }

    private static boolean tieneLlamada(String inst) {
        return inst.contains("call ") || inst.contains("create ");
    }

    private static boolean tieneParam(String inst) {
        return inst.startsWith("param ") || inst.startsWith("getparam ")
            || inst.startsWith("print ") || inst.startsWith("printnl");
    }

    // ── Utilidades ─────────────────────────────────────────────────────────

    static String getLHS(String inst) {
        if (inst.isEmpty() || inst.endsWith(":")) return null;
        int eq = inst.indexOf('=');
        if (eq <= 0) return null;
        String lhs = inst.substring(0, eq).trim();
        if (lhs.contains(" ") || lhs.isEmpty()) return null;
        return lhs;
    }

    static Set<String> getVarsRHS(String inst) {
        Set<String> vars = new HashSet<>();
        if (inst.isEmpty() || inst.endsWith(":")) return vars;

        if (inst.startsWith("ifFalse ")) {
            String rest = inst.substring("ifFalse ".length());
            addToken(vars, rest.split("\\s+")[0]);

        } else if (inst.startsWith("if ")) {
            String[] tok = inst.substring("if ".length()).split("\\s+");
            if (tok.length > 0) addToken(vars, tok[0]);
            if (tok.length > 2) addToken(vars, tok[2]);

        } else if (inst.startsWith("return ")) {
            addToken(vars, inst.substring("return ".length()).trim());

        } else if (inst.startsWith("param ")) {
            addToken(vars, inst.substring("param ".length()).trim());

        } else if (inst.startsWith("print ")) {
            addToken(vars, inst.substring("print ".length()).trim());

        } else {
            int eq = inst.indexOf('=');
            if (eq > 0) {
                String rhs = inst.substring(eq + 1).trim();
                if (!rhs.startsWith("call ") && !rhs.startsWith("create ")) {
                    for (String tok : rhs.split("[^a-zA-Z0-9_]+"))
                        addToken(vars, tok);
                }
            }
        }
        return vars;
    }

    private static void addToken(Set<String> set, String tok) {
        tok = tok.trim();
        if (!tok.isEmpty() && tok.matches("[a-zA-Z_][a-zA-Z0-9_]*"))
            set.add(tok);
    }
}
