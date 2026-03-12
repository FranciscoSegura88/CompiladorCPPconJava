package com.compilador;

import com.compilador.lexico.ScannerLexicoCpp;
import com.compilador.parser.parser;

import java.io.FileReader;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        // Permite pasar el archivo como argumento, o usa test.cpp por defecto
        String archivo = (args.length > 0) ? args[0] : "test.cpp";

        try {
            FileReader reader = new FileReader(archivo);

            // 1. Crear el scanner
            ScannerLexicoCpp scanner = new ScannerLexicoCpp(reader);

            // 2. Crear el parser pasándole el scanner
            //    El parser usará la misma lista de errores del scanner
            parser p = new parser(scanner);

            System.out.println("╔══════════════════════════════════════╗");
            System.out.println("  Analizando: " + archivo);
            System.out.println("╚══════════════════════════════════════╝\n");

            // 3. Ejecutar el análisis sintáctico
            //    (que internamente también ejecuta el léxico)
            p.parse();

            // 4. Reporte final de errores
            List<CompilerError> errores = scanner.getErrors();

            System.out.println();
            System.out.println("══════════════ RESULTADO ══════════════");

            if (errores.isEmpty()) {
                System.out.println("✓  Sin errores. Compilación exitosa.");
            } else {
                System.out.println("✗  Se encontraron " + errores.size() + " error(es):\n");
                System.out.printf("  %-18s  %-18s  %s%n",
                        "Tipo", "Ubicación", "Descripción");
                System.out.println("  " + "─".repeat(70));
                for (CompilerError e : errores) {
                    System.out.println("  " + e);
                }
            }

            System.out.println("═══════════════════════════════════════");

        } catch (java.io.FileNotFoundException e) {
            System.err.println("Error: No se encontró el archivo '" + archivo + "'");
        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
        }
    }
}
