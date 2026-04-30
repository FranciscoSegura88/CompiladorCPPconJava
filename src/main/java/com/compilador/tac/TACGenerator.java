/*---------------------------
| SEGURA VALENCIA FRANCISCO |
---------------------------*/

package com.compilador.tac;

import java.util.ArrayList;
import java.util.List;

/** Generates three-address code instructions, managing a global temp counter. */
public class TACGenerator {
    private int contador    = 0;
    private int labelContador = 0;
    private final List<String> instrucciones = new ArrayList<>();

    public String nuevoTemp() {
        return "t" + (++contador);
    }

    public String nuevoLabel() {
        return "L" + (++labelContador);
    }

    public void emitir(String instruccion) {
        instrucciones.add(instruccion);
    }

    public List<String> getInstrucciones() {
        return instrucciones;
    }
}
