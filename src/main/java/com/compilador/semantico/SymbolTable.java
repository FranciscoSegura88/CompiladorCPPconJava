/*---------------------------
| SEGURA VALENCIA FRANCISCO |
---------------------------*/

package com.compilador.semantico;

import java.util.*;

/**
 * Tabla de símbolos con gestión de ámbitos anidados.
 * Usa un stack de scopes: cada scope es un Map<nombre, Simbolo>.
 * enterScope() se llama ANTES de procesar el cuerpo.
 * exitScope()  se llama DESPUÉS de procesar el cuerpo.
 */
public class SymbolTable {

    public static class Simbolo {
        public final String nombre;
        public final String tipo;
        public final String ambito;
        public final boolean esConstante;
        public String valor;
        public final int linea;
        public final int columna;
        public boolean inicializado;

        public Simbolo(String nombre, String tipo, String ambito,
                       boolean esConstante, String valor, int linea, int columna) {
            this.nombre      = nombre;
            this.tipo        = tipo;
            this.ambito      = ambito;
            this.esConstante = esConstante;
            this.valor       = valor;
            this.linea       = linea;
            this.columna     = columna;
            this.inicializado = (valor != null && !valor.isEmpty());
        }

        @Override
        public String toString() {
            return String.format("  %-18s %-10s %-14s %-8s %s",
                nombre, tipo, ambito,
                esConstante ? "const" : "var",
                inicializado ? (valor != null ? valor : "(init)") : "(sin valor)");
        }
    }

    private final Deque<Map<String, Simbolo>> scopeStack = new ArrayDeque<>();
    private final List<Simbolo> allSymbols = new ArrayList<>();
    private final Deque<String> scopeNames = new ArrayDeque<>();

    public SymbolTable() {
        // ámbito global siempre activo
        scopeStack.push(new LinkedHashMap<>());
        scopeNames.push("global");
    }

    public void enterScope(String name) {
        scopeStack.push(new LinkedHashMap<>());
        scopeNames.push(name);
    }

    public void exitScope() {
        if (scopeStack.size() > 1) {
            scopeStack.pop();
            scopeNames.pop();
        }
    }

    public String currentScope() {
        return scopeNames.isEmpty() ? "global" : scopeNames.peek();
    }

    public boolean insertar(String nombre, String tipo, boolean esConst,
                            String valor, int linea, int columna) {
        Map<String, Simbolo> scope = scopeStack.peek();
        if (scope.containsKey(nombre)) return false;
        Simbolo s = new Simbolo(nombre, tipo, currentScope(),
                                esConst, valor, linea, columna);
        scope.put(nombre, s);
        allSymbols.add(s);
        return true;
    }

    public Simbolo buscar(String nombre) {
        for (Map<String, Simbolo> scope : scopeStack) {
            if (scope.containsKey(nombre)) return scope.get(nombre);
        }
        return null;
    }

    public Simbolo buscarEnAmbitoActual(String nombre) {
        return scopeStack.isEmpty() ? null : scopeStack.peek().get(nombre);
    }

    public void actualizarValor(String nombre, String nuevoValor) {
        for (Map<String, Simbolo> scope : scopeStack) {
            if (scope.containsKey(nombre)) {
                scope.get(nombre).valor = nuevoValor;
                scope.get(nombre).inicializado = true;
                return;
            }
        }
    }

    /** Fuerza la inserción aunque haya error de tipo (para no generar falsos "no declarado") */
    public void insertarForzado(String nombre, String tipo, boolean esConst,
                                String valor, int linea, int columna) {
        Map<String, Simbolo> scope = scopeStack.peek();
        Simbolo s = new Simbolo(nombre, tipo, currentScope(),
                                esConst, valor, linea, columna);
        scope.put(nombre, s);  // sobreescribe si existe
        // no agregar a allSymbols de nuevo si ya está
        boolean yaEsta = allSymbols.stream().anyMatch(x -> x.nombre.equals(nombre)
                         && x.ambito.equals(currentScope()));
        if (!yaEsta) allSymbols.add(s);
    }

    public List<Simbolo> getTodos() { return Collections.unmodifiableList(allSymbols); }

    public void imprimir() {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("                      TABLA DE SÍMBOLOS                        ");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.printf("  %-18s %-10s %-14s %-8s %s%n",
                          "Identificador", "Tipo", "Ámbito", "Clase", "Valor");
        System.out.println("  " + "─".repeat(70));

        if (allSymbols.isEmpty()) {
            System.out.println("  (vacía)");
        } else {
            String ultimo = null;
            for (Simbolo s : allSymbols) {
                if (s.nombre.startsWith("__m__")) continue; // ocultar marcadores internos
                if (!s.ambito.equals(ultimo)) {
                    System.out.println("  [" + s.ambito + "]");
                    ultimo = s.ambito;
                }
                System.out.println(s);
            }
        }
        System.out.println("  " + "─".repeat(70));
        long visible = allSymbols.stream().filter(s -> !s.nombre.startsWith("__m__")).count();
        System.out.printf("  Total: %d símbolo(s)%n", visible);
    }

    public static boolean sonCompatibles(String dest, String orig) {
        if (dest == null || orig == null) return true;
        if (dest.equals(orig)) return true;
        if (dest.equals("double") && (orig.equals("float") || orig.equals("int"))) return true;
        return false;
    }

    public static String tipoResultanteAritmetico(String t1, String t2) {
        if (t1 == null || t2 == null) return "unknown";
        Set<String> num = Set.of("int","float","double");
        if (!num.contains(t1) || !num.contains(t2)) return null;
        if (t1.equals("double") || t2.equals("double")) return "double";
        if (t1.equals("float")  || t2.equals("float"))  return "float";
        return "int";
    }
}
