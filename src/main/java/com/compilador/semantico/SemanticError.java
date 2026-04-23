/*---------------------------
| SEGURA VALENCIA FRANCISCO |
---------------------------*/

package com.compilador.semantico;

/**
 * Representa un error semántico detectado durante el análisis.
 * Se integra con la lista de errores del scanner para un reporte unificado.
 */
public class SemanticError {

    public enum TipoErrorSem {
        REDECLARACION,
        NO_DECLARADO,
        TIPO_INCOMPATIBLE,
        CONSTANTE_REASIGNADA,
        METODO_DUPLICADO,
        USO_ANTES_DE_DECLARAR
    }

    private final TipoErrorSem tipo;
    private final String mensaje;
    private final int linea;
    private final int columna;
    private final String token;

    public SemanticError(TipoErrorSem tipo, String mensaje,
                         int linea, int columna, String token) {
        this.tipo    = tipo;
        this.mensaje = mensaje;
        this.linea   = linea;
        this.columna = columna;
        this.token   = token;
    }

    public TipoErrorSem getTipo()   { return tipo;    }
    public String getMensaje()      { return mensaje; }
    public int    getLinea()        { return linea;   }
    public int    getColumna()      { return columna; }
    public String getToken()        { return token;   }

    /** Etiqueta corta para imprimir en la tabla de errores. */
    public String etiqueta() {
        return switch (tipo) {
            case REDECLARACION         -> "Semántico";
            case NO_DECLARADO          -> "Semántico";
            case TIPO_INCOMPATIBLE     -> "Semántico";
            case CONSTANTE_REASIGNADA  -> "Semántico";
            case METODO_DUPLICADO      -> "Semántico";
            case USO_ANTES_DE_DECLARAR -> "Semántico";
        };
    }

    @Override
    public String toString() {
        return String.format("  [ %-11s ]  Línea %3d, Col %3d  |  %s",
                etiqueta(), linea, columna, mensaje);
    }
}