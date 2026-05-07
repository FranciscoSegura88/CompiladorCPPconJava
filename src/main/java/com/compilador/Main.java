/*---------------------------
| SEGURA VALENCIA FRANCISCO |
---------------------------*/

package com.compilador;

import com.compilador.lexico.ScannerLexicoCpp;
import com.compilador.parser.parser;
import com.compilador.semantico.SemanticError;
import com.compilador.tac.TACOptimizer;
import com.compilador.tac.TACOptimizer.Resultado;
import com.compilador.tac.TACOptimizer.Cambio;
import com.compilador.codegen.CodeGenerator;
import com.compilador.codegen.CodeGeneratorNASM;

import java.io.FileReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

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

        // ── Código de tres direcciones (original) ───────────────────
        List<String> tac = p.tac.getInstrucciones();
        System.out.println();
        System.out.println("═".repeat(8) + " CÓDIGO INTERMEDIO (ORIGINAL) " + "═".repeat(8));
        imprimirTAC(tac);
        System.out.println("═".repeat(46));

        // ── Optimización ────────────────────────────────────────────
        System.out.println();
        System.out.println("═".repeat(11) + " OPTIMIZACIÓN DE CÓDIGO " + "═".repeat(11));
        Resultado opt = TACOptimizer.optimizar(tac);

        if (opt.cambios.isEmpty()) {
            System.out.println("  (sin optimizaciones aplicables)");
        } else {
            // Tabla de cambios
            System.out.println();
            System.out.printf("  %-5s  %-34s  %-30s  %s%n",
                "Línea", "Tipo de Optimización", "Instrucción Eliminada", "Razón");
            System.out.println("  " + "─".repeat(110));
            for (Cambio c : opt.cambios) {
                String instrTrunc = c.instruccion.length() > 29
                    ? c.instruccion.substring(0, 27) + ".." : c.instruccion;
                String razonTrunc = c.razon.length() > 55
                    ? c.razon.substring(0, 53) + ".." : c.razon;
                System.out.printf("  %-5d  %-34s  %-30s  %s%n",
                    c.lineaOriginal, c.tipo, instrTrunc, razonTrunc);
            }
            System.out.printf("%n  Total de optimizaciones aplicadas: %d%n", opt.cambios.size());
        }

        // ── Código optimizado ────────────────────────────────────────
        System.out.println();
        System.out.println("═".repeat(8) + " CÓDIGO INTERMEDIO (OPTIMIZADO) " + "═".repeat(6));
        if (opt.instrucciones.isEmpty()) {
            System.out.println("  (sin instrucciones)");
        } else {
            imprimirTAC(opt.instrucciones);
        }
        System.out.println("═".repeat(46));

        // ── Generación de código ejecutable ─────────────────────────
        System.out.println();
        System.out.println("═".repeat(7) + " GENERACIÓN DE CÓDIGO EJECUTABLE " + "═".repeat(6));
        CodeGenerator.Resultado cg = CodeGenerator.generar(opt.instrucciones);

        // Tabla de símbolos con direcciones
        System.out.println();
        System.out.printf("  %-20s  %-14s  %s%n", "Símbolo", "Dirección Mem.", "Tamaño");
        System.out.println("  " + "─".repeat(48));
        for (Map.Entry<String, Integer> e : cg.tablaSimbolos.entrySet())
            System.out.printf("  %-20s  0x%04X          4 bytes%n",
                e.getKey(), e.getValue());

        // Código ensamblador con direcciones
        System.out.println();
        System.out.println("  ─── Código Ensamblador (con direcciones) ─────────────────");
        for (CodeGenerator.InstruccionASM a : cg.instrucciones)
            System.out.println("    " + a.formatear());

        // Cabecera del ejecutable (hex dump, 32 bytes)
        System.out.println();
        System.out.println("  ─── Cabecera del Ejecutable (hex dump) ───────────────────");
        byte[] binData = cg.ejecutable;
        for (int i = 0; i < Math.min(32, binData.length); i++) {
            if (i % 8 == 0) System.out.printf("  %04X:  ", i);
            System.out.printf("%02X ", binData[i]);
            if (i % 8 == 7) System.out.println();
        }
        System.out.println();

        // Guardar .asm y .bin
        try {
            String base = archivo.replaceAll("\\.cpp$", "");
            List<String> escritos = CodeGenerator.guardarArchivos(cg, base);
            for (String f : escritos)
                System.out.println("  ✓ Generado: " + f);
        } catch (Exception ex) {
            System.err.println("  ✗ Error al guardar archivos: " + ex.getMessage());
        }
        System.out.println("═".repeat(46));

        // ── Código ensamblador real x86-64 (NASM) ───────────────────
        System.out.println();
        System.out.println("═".repeat(10) + " GENERACIÓN NASM x86-64 " + "═".repeat(10));
        try {
            CodeGeneratorNASM.Resultado nasm = CodeGeneratorNASM.generar(opt.instrucciones);
            String base     = archivo.replaceAll("\\.cpp$", "");
            String nasmPath = CodeGeneratorNASM.guardarNASM(nasm, base);
            System.out.println("  ✓ Generado: " + nasmPath);

            // Mostrar extracto del NASM
            System.out.println();
            System.out.println("  ─── Extracto NASM ───────────────────────────────────────");
            String[] lines = nasm.codigoNASM.split("\n");
            int shown = 0;
            for (String l : lines) {
                System.out.println("  " + l);
                if (++shown >= 60) { System.out.println("  ..."); break; }
            }

            // Ensamblar + ejecutar si no hay errores
            if (totalErrores == 0) {
                System.out.println();
                System.out.println("  ─── Ensamblando y ejecutando ────────────────────────────");
                int[] result = CodeGeneratorNASM.compilarYEjecutar(nasmPath, base);
                System.out.printf("  Código de salida: %d%n", result[0]);
                if (result[0] == 0) System.out.println("  ✓ Ejecución exitosa");
                else                System.out.println("  ✗ Error en ejecución");
            }
        } catch (Exception ex) {
            System.err.println("  ✗ Error NASM: " + ex.getMessage());
        }
        System.out.println("═".repeat(44));

        // ── Última instrucción de impresión ──────────────────────────
        System.out.println("Compilador C++ — Segura Valencia Francisco");
    }

    private static void imprimirTAC(List<String> instrs) {
        for (String inst : instrs) {
            String t = inst.trim();
            if (t.endsWith(":")) System.out.printf("%n  %s%n", t);
            else                 System.out.printf("    %s%n", t);
        }
    }
}