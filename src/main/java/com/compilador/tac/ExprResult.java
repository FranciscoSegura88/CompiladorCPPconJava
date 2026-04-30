/*---------------------------
| SEGURA VALENCIA FRANCISCO |
---------------------------*/

package com.compilador.tac;

/** Holds the semantic type and TAC place (temp or variable name) of an expression. */
public class ExprResult {
    public final String tipo;
    public final String lugar;

    public ExprResult(String tipo, String lugar) {
        this.tipo  = tipo;
        this.lugar = lugar;
    }
}
