package com.compilador;

/**
 * Representa un error encontrado durante el análisis léxico o sintáctico.
 * Compartida entre el Scanner (JFlex) y el Parser (JCup).
 */
public class CompilerError {

    // ── Tipos de error como enum en lugar de String libre ──
    public enum TipoError {
        LEXICO     ("[ Léxico     ]"),
        SINTACTICO ("[ Sintáctico ]"),
        FATAL      ("[ Fatal      ]");

        private final String etiqueta;
        TipoError(String etiqueta) { this.etiqueta = etiqueta; }
        public String getEtiqueta() { return etiqueta; }
    }

    private final TipoError tipo;
    private final String    mensaje;
    private final int       linea;
    private final int       columna;
    private final String    token;

    // ── Constructor principal (usado internamente con enum) ──
    public CompilerError(TipoError tipo, String mensaje, int linea, int columna, String token) {
        this.tipo    = tipo;
        this.mensaje = mensaje;
        this.linea   = linea;
        this.columna = columna;
        this.token   = (token != null) ? token : "";
    }

    // ── Constructor de compatibilidad con String ──
    public CompilerError(String tipo, String mensaje, int linea, int columna, String token) {
        this(parseTipo(tipo), mensaje, linea, columna, token);
    }

    // ── Getters ──
    public TipoError getTipo()    { return tipo; }
    public String    getMensaje() { return mensaje; }
    public int       getLinea()   { return linea; }
    public int       getColumna() { return columna; }
    public String    getToken()   { return token; }

    // ── Alias para compatibilidad con código anterior ──
    public String getMessage() { return mensaje; }
    public int    getLine()    { return linea; }
    public int    getColumn()  { return columna; }
    public String getType()    { return tipo.getEtiqueta(); }

    // ── Representación en consola ──
    @Override
    public String toString() {
        String tokenPart = token.isEmpty() ? "" : "  →  Token: \"" + token + "\"";
        return String.format("%s  Línea %3d, Col %3d  |  %s%s",
                tipo.getEtiqueta(), linea, columna, mensaje, tokenPart);
    }

    // ── Convierte String al enum ──
    private static TipoError parseTipo(String tipo) {
        if (tipo == null) return TipoError.SINTACTICO;
        String t = tipo.toLowerCase();
        if (t.contains("léxico") || t.contains("lexico")) return TipoError.LEXICO;
        if (t.contains("fatal"))                           return TipoError.FATAL;
        return TipoError.SINTACTICO;
    }
}
