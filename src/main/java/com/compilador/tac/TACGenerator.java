/*---------------------------
| SEGURA VALENCIA FRANCISCO |
---------------------------*/

package com.compilador.tac;

import java.util.ArrayList;
import java.util.List;

public class TACGenerator {
    private int contador      = 0;
    private int labelContador = 0;
    private final List<String> instrucciones = new ArrayList<>();
    private boolean buffering = false;
    private final List<String> buffer = new ArrayList<>();

    public String nuevoTemp() {
        return "t" + (++contador);
    }

    public String nuevoLabel() {
        return "L" + (++labelContador);
    }

    public void emitir(String instruccion) {
        if (buffering) buffer.add(instruccion);
        else instrucciones.add(instruccion);
    }

    public void iniciarBuffer() {
        buffering = true;
        buffer.clear();
    }

    public List<String> obtenerBuffer() {
        buffering = false;
        List<String> result = new ArrayList<>(buffer);
        buffer.clear();
        return result;
    }

    public List<String> getInstrucciones() {
        return instrucciones;
    }
}
