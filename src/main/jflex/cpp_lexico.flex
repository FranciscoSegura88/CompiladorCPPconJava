/*---------------------------
| SEGURA VALENCIA FRANCISCO |
---------------------------*/

/*-------------------
Codigo de usuario
-------------------*/

package com.compilador.lexico;

import java.util.ArrayList;
import java.util.List;
import java_cup.runtime.*;
import com.compilador.parser.sym;
import com.compilador.CompileError;

/*-----------------------------
Configuracion y Declaraciones
-----------------------------*/

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

    // Lista de errores para el reporte final
    private List<CompilerError> errors = new ArrayList<>();

    public List<CompilerError> getErrors() {
        return errors;
    }

    private void addError(String type, String msg) {
        errors.add(new CompilerError(type, msg, yyline + 1, yycolumn + 1));
    }
%}

/*--------
| MACROS |
--------*/

DIGITO = [0-9]
LETRA  = [A-Za-z_]
ESPACIO = [ \t\r\n]+

/*------------
| MACROS CPP |
------------*/
/*Identificadores validos.*/

ID_CPP = {LETRA}({LETRA}|{DIGITO})*

/*Numeros (Enteros y flotantes)*/
ENTERO  = {DIGITO}+
DECIMAL = {DIGITO}+\.{DIGITO}+

/*---------------------------------
| PATRONES PARA ERRORES (TRAMPAS) |
---------------------------------*/ 

INVALID_ID = {DIGITO}+ {LETRA}+ ({LETRA}|{DIGITO})*

INVALID_NUM = {DIGITO}+ \. {DIGITO}+ \. ({DIGITO}|\.)*

UNCLOSED_STRING = \" [^\"\n]* {ESPACIO}?

%%

/*----------------
| REGLAS LEXICAS |
----------------*/

{ESPACIO}

"//".* 
"/*" [^*]* "*"+ ([^/*] [^*]* "*"+)* "/"

/* PALABRAS RESERVADAS */
"int"    { return symbol(sym.PR_INT); }
"float"  { return symbol(sym.PR_FLOAT); }
"bool"   { return symbol(sym.PR_BOOL); }
"char"   { return symbol(sym.PR_CHAR); }
"string" { return symbol(sym.PR_STRING); }
"if"     { return symbol(sym.PR_IF); }
"else"   { return symbol(sym.PR_ELSE); }
"while"  { return symbol(sym.PR_WHILE); }
"do"     { return symbol(sym.PR_DO); }
"return" { return symbol(sym.PR_RETURN); }
"void"   { return symbol(sym.PR_VOID); }
"main"   { return symbol(sym.PR_MAIN); }

/* LIBRERIAS Y DIRECTIVAS */

"include"   { return symbol(sym.PR_INCLUDE); }
"iostream"  { return symbol(sym.LIB_IOSTREAM); }
"std"       { return symbol(sym.PR_STD); }
"cout"      { return symbol(sym.PR_COUT); }
"cin"       { return symbol(sym.PR_CIN); }

/* OPERADORES Y SIGNOS */

"+"         { return symbol(sym.OP_SUMA); }
"-"         { return symbol(sym.OP_RESTA); }
"*"         { return symbol(sym.OP_MULT); }
"/"         { return symbol(sym.OP_DIV); }
"="         { return symbol(sym.OP_ASIG); }
"=="        { return symbol(sym.OP_IGUAL); }
"!="        { return symbol(sym.OP_DIF); }
"<"         { return symbol(sym.OP_MENOR); }
">"         { return symbol(sym.OP_MAYOR); }
"<="        { return symbol(sym.OP_MENOR_IGUAL); }
">="        { return symbol(sym.OP_MAYOR_IGUAL); }
"&&"        { return symbol(sym.OP_AND); }
"||"        { return symbol(sym.OP_OR); }
"!"         { return symbol(sym.OP_NOT); }
";"         { return symbol(sym.PUNTO_COMA); }
","         { return symbol(sym.COMA); }
"("         { return symbol(sym.PAREN_IZQ); }
")"         { return symbol(sym.PAREN_DER); }
"{"         { return symbol(sym.LLAVE_IZQ); }
"}"         { return symbol(sym.LLAVE_DER); }
"#"         { return symbol(sym.HASH); }

/* TRAMPAS DE ERRORES */

{INVALID_ID} { 
    addError("Error Sintáctico", "Identificador mal formado (inicia con número): " + yytext()); 
}

{INVALID_NUM} { 
    addError("Error Sintáctico", "Número mal formado (múltiples puntos): " + yytext()); 
}

{UNCLOSED_STRING} {
    addError("Error Sintáctico", "Cadena de texto no cerrada (falta comillas)");
}

/* REGLAS GENERALES */

// Cadenas: Comillas, cualquier cosa que NO sea comillas o salto de linea, Comillas.
\" [^\"\n]* \" { return symbol(sym. LIT_STRING -> " + yytext()); }

{DECIMAL}    { return symbol(sym. LIT_DECIMAL -> " + yytext()); }
{ENTERO}    { return symbol(sym. LIT_ENTERO -> " + yytext()); }
{ID_CPP} { return symbol(sym. ID -> " + yytext()); }

/* CUALQUIER OTRA COSA */

. { 
    addError("Error Fatal", "Caracter no reconocido: " + yytext()); 
}