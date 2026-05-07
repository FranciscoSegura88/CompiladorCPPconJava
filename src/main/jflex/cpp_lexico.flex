/*---------------------------
| SEGURA VALENCIA FRANCISCO |
---------------------------*/

package com.compilador.lexico;

import java.util.ArrayList;
import java.util.List;
import java_cup.runtime.*;
import com.compilador.CompilerError;
import com.compilador.CompilerError.TipoError;
import com.compilador.parser.sym;

%%

%class ScannerLexicoCpp
%public
%unicode
%cup
%line
%column

%{
    private Symbol symbol(int type) {
        return new Symbol(type, yyline + 1, yycolumn + 1);
    }

    private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline + 1, yycolumn + 1, value);
    }

    private final List<CompilerError> errors = new ArrayList<>();

    public List<CompilerError> getErrors() { return errors; }

    private void addError(TipoError tipo, String msg) {
        errors.add(new CompilerError(tipo, msg, yyline + 1, yycolumn + 1, yytext()));
    }
%}

DIGITO  = [0-9]
LETRA   = [A-Za-z_]
ESPACIO = [ \t\r\n]+

ID_CPP  = {LETRA}({LETRA}|{DIGITO})*
ENTERO  = {DIGITO}+
DECIMAL = {DIGITO}+\.{DIGITO}+

INVALID_ID      = {DIGITO}+{LETRA}+({LETRA}|{DIGITO})*
INVALID_NUM     = {DIGITO}+\.{DIGITO}+\.({DIGITO}|\.)*
UNCLOSED_STRING = \"[^\"\n]*

%%

{ESPACIO}   { /* ignorar */ }

"//".*                                        { /* comentario de línea  */ }
"/*" [^*]* "*"+ ([^/*] [^*]* "*"+)* "/"      { /* comentario de bloque */ }

/* ══ Palabras reservadas — NUEVAS (A4/A7) ══ */
"class"     { return symbol(sym.PR_CLASS);     }
"const"     { return symbol(sym.PR_CONST);     }
"public"    { return symbol(sym.PR_PUBLIC);    }
"private"   { return symbol(sym.PR_PRIVATE);   }
"protected" { return symbol(sym.PR_PROTECTED); }
"true"      { return symbol(sym.LIT_BOOL, Boolean.TRUE);  }
"false"     { return symbol(sym.LIT_BOOL, Boolean.FALSE); }

/* ══ Palabras reservadas — existentes ══ */
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
"do"        { return symbol(sym.PR_DO);      }
"for"       { return symbol(sym.PR_FOR);     }
"switch"    { return symbol(sym.PR_SWITCH);  }
"case"      { return symbol(sym.PR_CASE);    }
"break"     { return symbol(sym.PR_BREAK);   }
"default"   { return symbol(sym.PR_DEFAULT); }
"new"       { return symbol(sym.PR_NEW);     }
"return"    { return symbol(sym.PR_RETURN); }
"double"    { return symbol(sym.PR_DOUBLE); }

/* ══ Librerías / directivas ══ */
"include"   { return symbol(sym.PR_INCLUDE);   }
"iostream"  { return symbol(sym.LIB_IOSTREAM); }
"std"       { return symbol(sym.PR_STD);       }
"cout"      { return symbol(sym.PR_COUT);      }
"cin"       { return symbol(sym.PR_CIN);       }
"using"     { return symbol(sym.PR_USING);     }
"namespace" { return symbol(sym.PR_NAMESPACE); }
"endl"      { return symbol(sym.PR_ENDL);      }

/* ══ Operadores — NUEVOS (A7): ++, --, +=, -= antes de + - ══ */
"++"    { return symbol(sym.OP_INC);        }
"--"    { return symbol(sym.OP_DEC);        }
"+="    { return symbol(sym.OP_SUMA_ASIG);  }
"-="    { return symbol(sym.OP_RESTA_ASIG); }

/* ══ Operadores — existentes (2 chars antes de 1) ══ */
"=="    { return symbol(sym.OP_IGUAL);        }
"!="    { return symbol(sym.OP_DIF);          }
"<="    { return symbol(sym.OP_MENOR_IGUAL);  }
">="    { return symbol(sym.OP_MAYOR_IGUAL);  }
"<"     { return symbol(sym.OP_MENOR);        }
">"     { return symbol(sym.OP_MAYOR);        }
"&&"    { return symbol(sym.OP_AND);          }
"||"    { return symbol(sym.OP_OR);           }
"!"     { return symbol(sym.OP_NOT);          }
"<<"    { return symbol(sym.OP_FLUJO_SAL);    }
">>"    { return symbol(sym.OP_FLUJO_ENT);    }
"+"     { return symbol(sym.OP_SUMA);         }
"-"     { return symbol(sym.OP_RESTA);        }
"*"     { return symbol(sym.OP_MULT);         }
"/"     { return symbol(sym.OP_DIV);          }
"="     { return symbol(sym.OP_ASIG);         }

/* ══ Delimitadores ══ */
";"     { return symbol(sym.PUNTO_COMA); }
","     { return symbol(sym.COMA);       }
"("     { return symbol(sym.PAREN_IZQ); }
")"     { return symbol(sym.PAREN_DER); }
"{"     { return symbol(sym.LLAVE_IZQ); }
"}"     { return symbol(sym.LLAVE_DER); }
"#"     { return symbol(sym.HASH);      }
"::"    { return symbol(sym.DOBLE_DOS);  }
":"     { return symbol(sym.DOS_PUNTOS); }
"."     { return symbol(sym.PUNTO);      }

/* ══ Trampas de errores ══ */
{INVALID_ID} {
    addError(TipoError.LEXICO, "Identificador mal formado (inicia con número)");
}
{INVALID_NUM} {
    addError(TipoError.LEXICO, "Número mal formado (múltiples puntos decimales)");
}
{UNCLOSED_STRING} {
    addError(TipoError.LEXICO, "Cadena de texto no cerrada (falta comilla de cierre)");
}

/* ══ Literales e identificadores ══ */
\" [^\"\n]* \"        { return symbol(sym.LIT_STRING,  yytext()); }
\' [^\'\n] \'         { return symbol(sym.LIT_CHAR,    yytext()); }
{DECIMAL}             { return symbol(sym.LIT_DECIMAL, Double.parseDouble(yytext())); }
{ENTERO}              { return symbol(sym.LIT_ENTERO,  Integer.parseInt(yytext())); }
{ID_CPP}              { return symbol(sym.ID,          yytext()); }

/* ══ Cualquier otro carácter ══ */
. {
    addError(TipoError.FATAL, "Carácter no reconocido");
}
