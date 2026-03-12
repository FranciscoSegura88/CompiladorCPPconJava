package com.compilador;

import com.compilador.lexico.ScannerLexicoCpp;

import java.io.FileReader;

public class Main {
    public static void main(String[] args) {
        String testFile = "test.txt";
        
        try {

            FileReader reader = new FileReader(testFile);

            ScannerLexicoCpp lexer = new ScannerLexicoCpp(reader);

            lexer.yylex();

            System.out.println("Compilacion finalizada sin errores.");

            System.out.println(" -----Errores----- ");
            if (lexer.getErrors().isEmpty()) {
                System.out.println("No se encontraron errores.");
            } else {
                for (CompilerError error : lexer.getErrors()) {
                    System.out.println(error);
                }
            }

        } catch (Exception e) {
            System.err.println("Error al compilar: " + e.getMessage());
        }
    }
}