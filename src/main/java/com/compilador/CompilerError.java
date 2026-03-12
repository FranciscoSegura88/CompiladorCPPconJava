package com.compilador;

public class CompilerError {
    private String type;
    private String message;
    private int line;
    private int column;
    private String token;

    public CompilerError(String type, String message, int line, int column, String token) {
        this.type = type;
        this.message = message;
        this.line = line;
        this.column = column;
        this.token = token;
    }

    public String getType() { return type;}
    public String getMessage() { return message; }
    public int getLine() { return line; }
    public String getToken() { return token; }
    public int getColumn() { return column; }

    @Override
    public String toString() {
        return String.format("%s Error en linea %d, Columna %d: %s (Token: %s)", type, line, column, message, token);
    }
}
