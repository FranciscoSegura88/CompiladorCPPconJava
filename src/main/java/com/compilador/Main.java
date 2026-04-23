/*---------------------------
| SEGURA VALENCIA FRANCISCO |
---------------------------*/

package com.compilador;

import com.compilador.lexico.ScannerLexicoCpp;
import com.compilador.parser.parser;
import com.compilador.semantico.SemanticError;

import java.io.FileReader;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {

        // ── Primera instrucción de impresión ─────────────────────────
        System.out.println("Compilador C++ — Segura Valencia Francisco");

        if (args.length == 0) {
            System.err.println("Uso: java -jar compilador.jar <archivo.cpp>");
            System.exit(1);
        }

        String archivo = args[0];
        ScannerLexicoCpp scanner = new ScannerLexicoCpp(new FileReader(archivo));
        parser p = new parser(scanner);

        // Encabezado
        System.out.println("╔" + "═".repeat(40) + "╗");
        System.out.printf("  Analizando: %-28s%n", archivo);
        System.out.println("╚" + "═".repeat(40) + "╝");

        // Ejecutar análisis (léxico + sintáctico + semántico)
        p.parse();

        // ── Errores léxicos y sintácticos ────────────────────────────
        List<CompilerError> errSint = scanner.getErrors();

        // ── Errores semánticos ───────────────────────────────────────
        List<SemanticError> errSem = p.erroresSem;

        int totalErrores = errSint.size() + errSem.size();

        // ── Tabla de símbolos ────────────────────────────────────────
        p.tablaSimbolos.imprimir();

        // ── Reporte de errores ───────────────────────────────────────
        System.out.println();
        System.out.println("═".repeat(14) + " RESULTADO " + "═".repeat(14));

        if (totalErrores == 0) {
            System.out.println("✓  Sin errores. Compilación exitosa.");
        } else {
            System.out.printf("✗  Se encontraron %d error(es):%n", totalErrores);
            System.out.printf("  %-18s %-20s %s%n", "Tipo", "Ubicación", "Descripción");
            System.out.println("  " + "─".repeat(70));

            // Primero léxicos/sintácticos
            for (CompilerError e : errSint) {
                System.out.printf(
                    "  [ %-11s ]  Línea %3d, Col %3d  |  %s%n",
                    e.getType(), e.getLine(), e.getColumn(), e.getMessage());
            }
            // Luego semánticos
            for (SemanticError e : errSem) {
                System.out.println(e);
            }
        }

        System.out.println("═".repeat(39));

        // ── Última instrucción de impresión ──────────────────────────
        System.out.println("Compilador C++ — Segura Valencia Francisco");
    }
}