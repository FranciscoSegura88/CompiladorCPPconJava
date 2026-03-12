/*---------------------------
| SEGURA VALENCIA FRANCISCO |
---------------------------*/

/*-------------------
  Código de usuario
-------------------*/

package com.compilador.lexico;

import java.util.ArrayList;
import java.util.List;
import java_cup.runtime.*;
import com.compilador.CompilerError;
import com.compilador.CompilerError.TipoError;
import com.compilador.parser.sym;

/*-----------------------------
  Configuración y Declaraciones
-----------------------------*/

%%

%class ScannerLexicoCpp
%public
%unicode
%cup
%line
%column

%{
    /* ── Helpers para crear Symbols con línea/columna ── */
    private Symbol symbol(int type) {
        return new Symbol(type, yyline + 1, yycolumn + 1);
    }

    private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline + 1, yycolumn + 1, value);
    }

    /* ── Lista de errores compartida con el parser ── */
    private final List<CompilerError> errors = new ArrayList<>();

    public List<CompilerError> getErrors() {
        return errors;
    }

    /* ── Registrar error léxico (usa yytext() como token automáticamente) ── */
    private void addError(TipoError tipo, String msg) {
        errors.add(new CompilerError(tipo, msg, yyline + 1, yycolumn + 1, yytext()));
    }
%}

/*--------
  MACROS
--------*/

DIGITO  = [0-9]
LETRA   = [A-Za-z_]
ESPACIO = [ \t\r\n]+

/*--------------
  MACROS C++
--------------*/

/* Identificadores válidos */
ID_CPP = {LETRA}({LETRA}|{DIGITO})*

/* Números enteros y flotantes */
ENTERO  = {DIGITO}+
DECIMAL = {DIGITO}+\.{DIGITO}+

/*----------------------------------
  PATRONES PARA ERRORES (TRAMPAS)
  DEBEN ir ANTES de las reglas generales
----------------------------------*/

INVALID_ID      = {DIGITO}+{LETRA}+({LETRA}|{DIGITO})*
INVALID_NUM     = {DIGITO}+\.{DIGITO}+\.({DIGITO}|\.)*
UNCLOSED_STRING = \"[^\"\n]*

%%

/*-----------------
  REGLAS LÉXICAS
-----------------*/

/* Espacios — ignorar */
{ESPACIO}   { /* ignorar */ }

/* Comentarios — ignorar */
"//".*                                        { /* comentario de línea  */ }
"/*" [^*]* "*"+ ([^/*] [^*]* "*"+)* "/"      { /* comentario de bloque */ }

/* ══════════════════════════════════
   PALABRAS RESERVADAS
   (deben ir ANTES que la regla ID)
═══════════════════════════════════ */
"int"       { return symbol(sym.PR_INT);    }
"float"     { return symbol(sym.PR_FLOAT);  }
"bool"      { return symbol(sym.PR_BOOL);   }
"char"      { return symbol(sym.PR_CHAR);   }
"string"    { return symbol(sym.PR_STRING); }
"void"      { return symbol(sym.PR_VOID);   }
"main"      { return symbol(sym.PR_MAIN);   }
"if"        { return symbol(sym.PR_IF);     }
"else"      { return symbol(sym.PR_ELSE);   }
"while"     { return symbol(sym.PR_WHILE);  }
"do"        { return symbol(sym.PR_DO);     }
"return"    { return symbol(sym.PR_RETURN); }
"double"    { return symbol(sym.PR_DOUBLE); }

/* ══════════════════════════════════
   LIBRERÍAS Y DIRECTIVAS
═══════════════════════════════════ */
"include"   { return symbol(sym.PR_INCLUDE);   }
"iostream"  { return symbol(sym.LIB_IOSTREAM); }
"std"       { return symbol(sym.PR_STD);       }
"cout"      { return symbol(sym.PR_COUT);      }
"cin"       { return symbol(sym.PR_CIN);       }
"using"     { return symbol(sym.PR_USING);     }
"namespace" { return symbol(sym.PR_NAMESPACE); }
"endl"      { return symbol(sym.PR_ENDL);      }

/* ══════════════════════════════════
   OPERADORES
   IMPORTANTE: los de 2 caracteres van ANTES que los de 1
═══════════════════════════════════ */

/* Comparación */
"=="    { return symbol(sym.OP_IGUAL);        }
"!="    { return symbol(sym.OP_DIF);          }
"<="    { return symbol(sym.OP_MENOR_IGUAL);  }
">="    { return symbol(sym.OP_MAYOR_IGUAL);  }
"<"     { return symbol(sym.OP_MENOR);        }
">"     { return symbol(sym.OP_MAYOR);        }

/* Lógicos */
"&&"    { return symbol(sym.OP_AND); }
"||"    { return symbol(sym.OP_OR);  }
"!"     { return symbol(sym.OP_NOT); }

/* Flujo cout/cin */
"<<"    { return symbol(sym.OP_FLUJO_SAL); }
">>"    { return symbol(sym.OP_FLUJO_ENT); }

/* Aritméticos */
"+"     { return symbol(sym.OP_SUMA);   }
"-"     { return symbol(sym.OP_RESTA);  }
"*"     { return symbol(sym.OP_MULT);   }
"/"     { return symbol(sym.OP_DIV);    }

/* Asignación */
"="     { return symbol(sym.OP_ASIG); }

/* ══════════════════════════════════
   DELIMITADORES
═══════════════════════════════════ */
";"     { return symbol(sym.PUNTO_COMA); }
","     { return symbol(sym.COMA);       }
"("     { return symbol(sym.PAREN_IZQ); }
")"     { return symbol(sym.PAREN_DER); }
"{"     { return symbol(sym.LLAVE_IZQ); }
"}"     { return symbol(sym.LLAVE_DER); }
"#"     { return symbol(sym.HASH);      }
"::"    { return symbol(sym.DOBLE_DOS); }

/* ══════════════════════════════════
   TRAMPAS DE ERRORES
   Van ANTES que las reglas generales de literales
═══════════════════════════════════ */

{INVALID_ID} {
    addError(TipoError.LEXICO, "Identificador mal formado (inicia con número)");
}

{INVALID_NUM} {
    addError(TipoError.LEXICO, "Número mal formado (múltiples puntos decimales)");
}

{UNCLOSED_STRING} {
    addError(TipoError.LEXICO, "Cadena de texto no cerrada (falta comilla de cierre)");
}

/* ══════════════════════════════════
   LITERALES E IDENTIFICADORES
   Van AL FINAL para no pisar las palabras reservadas
═══════════════════════════════════ */

\" [^\"\n]* \"  { return symbol(sym.LIT_STRING,  yytext()); }
{DECIMAL}       { return symbol(sym.LIT_DECIMAL, Double.parseDouble(yytext())); }
{ENTERO}        { return symbol(sym.LIT_ENTERO,  Integer.parseInt(yytext())); }
{ID_CPP}        { return symbol(sym.ID, yytext()); }

/* ══════════════════════════════════
   CUALQUIER OTRO CARÁCTER
═══════════════════════════════════ */
. {
    addError(TipoError.FATAL, "Carácter no reconocido");
}
