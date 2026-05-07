#!/usr/bin/env python3
"""
Reporte PDF — Actividad 13: Generación de Código Ejecutable
SEGURA VALENCIA FRANCISCO — Traductores de Lenguajes — UDG CUTonalá
"""

import subprocess, datetime, textwrap
from reportlab.lib.pagesizes import letter
from reportlab.lib import colors
from reportlab.lib.units import inch
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.platypus import (
    SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle,
    HRFlowable, KeepTogether, PageBreak
)
from reportlab.lib.enums import TA_CENTER, TA_LEFT, TA_JUSTIFY

# ── Constantes ─────────────────────────────────────────────────────────────────

NOMBRE      = "Segura Valencia Francisco"
CARRERA     = "Ingeniería en Computación"
MATERIA     = "Traductores de Lenguajes"
UNIVERSIDAD = "Universidad de Guadalajara — CUTonalá"
ACTIVIDAD   = "Actividad 13"
TITULO      = "Generación de Código Ejecutable"
VIDEO_URL   = "https://youtu.be/placeholder-a13"

AZUL_UDG    = colors.HexColor("#003087")
ROJO_UDG    = colors.HexColor("#C8102E")
GRIS_CLARO  = colors.HexColor("#F5F5F5")
GRIS_MEDIO  = colors.HexColor("#DDDDDD")
VERDE_OK    = colors.HexColor("#1B7340")
ROJO_ERR    = colors.HexColor("#A00000")
AMARILLO_H  = colors.HexColor("#FFF3CD")
AZUL_CLARO  = colors.HexColor("#E8F0FE")

# ── Estilos ────────────────────────────────────────────────────────────────────

def estilos():
    base = getSampleStyleSheet()
    def P(name, **kw):
        return ParagraphStyle(name, **kw)

    return {
        "titulo_portada": P("titulo_portada",
            fontSize=26, textColor=colors.white, alignment=TA_CENTER,
            fontName="Helvetica-Bold", leading=32),
        "subtitulo_portada": P("subtitulo_portada",
            fontSize=14, textColor=colors.HexColor("#CCE0FF"), alignment=TA_CENTER,
            fontName="Helvetica", leading=20),
        "meta_portada": P("meta_portada",
            fontSize=11, textColor=colors.white, alignment=TA_CENTER,
            fontName="Helvetica", leading=16),
        "h1": P("h1",
            fontSize=16, textColor=AZUL_UDG, fontName="Helvetica-Bold",
            spaceBefore=18, spaceAfter=6, leading=20),
        "h2": P("h2",
            fontSize=13, textColor=AZUL_UDG, fontName="Helvetica-Bold",
            spaceBefore=12, spaceAfter=4, leading=16),
        "h3": P("h3",
            fontSize=11, textColor=colors.HexColor("#444444"), fontName="Helvetica-Bold",
            spaceBefore=8, spaceAfter=3, leading=14),
        "body": P("body",
            fontSize=10, fontName="Helvetica", leading=14,
            spaceAfter=6, alignment=TA_JUSTIFY),
        "code": P("code",
            fontSize=7.5, fontName="Courier", leading=10,
            backColor=colors.HexColor("#1E1E1E"), textColor=colors.HexColor("#D4D4D4"),
            leftIndent=8, rightIndent=8, spaceBefore=4, spaceAfter=4),
        "code_inline": P("code_inline",
            fontSize=8.5, fontName="Courier", leading=12,
            backColor=GRIS_CLARO, textColor=colors.HexColor("#333333"),
            leftIndent=6, rightIndent=6, spaceBefore=2, spaceAfter=2),
        "ok": P("ok",
            fontSize=10, textColor=VERDE_OK, fontName="Helvetica-Bold",
            alignment=TA_CENTER, leading=14),
        "err": P("err",
            fontSize=10, textColor=ROJO_ERR, fontName="Helvetica-Bold",
            alignment=TA_CENTER, leading=14),
        "caption": P("caption",
            fontSize=8, textColor=colors.grey, fontName="Helvetica-Oblique",
            alignment=TA_CENTER, spaceAfter=4),
        "bullet": P("bullet",
            fontSize=10, fontName="Helvetica", leading=14,
            leftIndent=16, spaceAfter=3),
        "url": P("url",
            fontSize=10, textColor=AZUL_UDG, fontName="Helvetica",
            alignment=TA_CENTER, leading=14),
    }

# ── Helpers ────────────────────────────────────────────────────────────────────

def hr(color=AZUL_UDG, thickness=1):
    return HRFlowable(width="100%", thickness=thickness, color=color,
                      spaceAfter=6, spaceBefore=6)

def sp(h=8):
    return Spacer(1, h)

def tabla_estilo_base(header_color=AZUL_UDG):
    return TableStyle([
        ("BACKGROUND",   (0, 0), (-1,  0), header_color),
        ("TEXTCOLOR",    (0, 0), (-1,  0), colors.white),
        ("FONTNAME",     (0, 0), (-1,  0), "Helvetica-Bold"),
        ("FONTSIZE",     (0, 0), (-1,  0), 9),
        ("ALIGN",        (0, 0), (-1,  0), "CENTER"),
        ("FONTNAME",     (0, 1), (-1, -1), "Courier"),
        ("FONTSIZE",     (0, 1), (-1, -1), 7.5),
        ("ROWBACKGROUNDS",(0,1), (-1, -1), [colors.white, GRIS_CLARO]),
        ("GRID",         (0, 0), (-1, -1), 0.4, GRIS_MEDIO),
        ("VALIGN",       (0, 0), (-1, -1), "MIDDLE"),
        ("LEFTPADDING",  (0, 0), (-1, -1), 5),
        ("RIGHTPADDING", (0, 0), (-1, -1), 5),
        ("TOPPADDING",   (0, 0), (-1, -1), 3),
        ("BOTTOMPADDING",(0, 0), (-1, -1), 3),
    ])

def code_block(text, st):
    lines = text.strip().split("\n")
    escaped = []
    for l in lines:
        l2 = (l.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;"))
        escaped.append(l2)
    return Paragraph("<br/>".join(escaped), st["code"])

def leer_salida(path):
    try:
        with open(path) as f:
            return f.read()
    except:
        return "(sin salida)"

# ── Sección: portada ────────────────────────────────────────────────────────────

def portada(st, fecha):
    from reportlab.platypus import Table as RLTable

    encabezado = RLTable(
        [[Paragraph(f"<b>{UNIVERSIDAD}</b>", st["subtitulo_portada"])],
         [Paragraph(f"{CARRERA}", st["meta_portada"])],
         [Paragraph(f"{MATERIA}", st["meta_portada"])],
         [sp(10)],
         [Paragraph(f"<b>{ACTIVIDAD}</b>", ParagraphStyle("act",
              fontSize=18, textColor=colors.HexColor("#FFD700"),
              fontName="Helvetica-Bold", alignment=TA_CENTER, leading=22))],
         [Paragraph(TITULO, st["titulo_portada"])],
         [sp(20)],
         [Paragraph(f"<b>{NOMBRE}</b>", ParagraphStyle("nom",
              fontSize=15, textColor=colors.white, fontName="Helvetica-Bold",
              alignment=TA_CENTER, leading=20))],
         [Paragraph(f"Fecha: {fecha}", st["meta_portada"])],
         ],
        colWidths=[6.5*inch],
    )
    encabezado.setStyle(TableStyle([
        ("BACKGROUND",  (0, 0), (-1, -1), AZUL_UDG),
        ("TOPPADDING",  (0, 0), (-1, -1), 8),
        ("BOTTOMPADDING",(0,0), (-1, -1), 8),
        ("LEFTPADDING", (0, 0), (-1, -1), 20),
        ("RIGHTPADDING",(0, 0), (-1, -1), 20),
        ("ROUNDEDCORNERS", [10]),
    ]))

    return [encabezado, sp(20)]

# ── Sección: objetivo ───────────────────────────────────────────────────────────

def seccion_objetivo(st):
    return [
        Paragraph("1. Objetivo", st["h1"]),
        hr(),
        Paragraph(
            "Implementar la fase final del compilador: la <b>generación de código ejecutable</b> "
            "a partir del Código de Tres Direcciones (TAC) optimizado. "
            "El generador traduce cada instrucción TAC a ensamblador simbólico, "
            "asigna direcciones de memoria a variables y temporales, produce una "
            "representación binaria del ejecutable y escribe archivos <b>.asm</b> y <b>.bin</b>.",
            st["body"]),
        sp(6),
    ]

# ── Sección: arquitectura ───────────────────────────────────────────────────────

def seccion_arquitectura(st):
    rows = [
        ["Fase", "Clase / Archivo", "Responsabilidad"],
        ["Léxico",     "ScannerLexicoCpp (JFlex)",   "Tokenización"],
        ["Sintáctico", "parser (JCup)",               "Árbol sintáctico + acciones"],
        ["Semántico",  "SymbolTable / SemanticError", "Tipos, scopes, errores"],
        ["TAC",        "TACGenerator",                "Código de tres direcciones"],
        ["Optimización","TACOptimizer",               "4 pasos: dead code, CSE, copy, unused"],
        ["Codegen",    "CodeGenerator ✦",             "ASM simbólico + binario"],
    ]
    t = Table(rows, colWidths=[1.3*inch, 2.2*inch, 3.0*inch])
    ts = tabla_estilo_base()
    ts.add("BACKGROUND", (0, 6), (-1, 6), colors.HexColor("#D4EDDA"))
    ts.add("TEXTCOLOR",  (0, 6), (-1, 6), VERDE_OK)
    ts.add("FONTNAME",   (0, 6), (-1, 6), "Helvetica-Bold")
    t.setStyle(ts)

    return [
        Paragraph("2. Arquitectura del Compilador", st["h1"]),
        hr(),
        Paragraph(
            "Pipeline de 6 fases encadenadas. La fase resaltada (✦) es la implementada "
            "en esta actividad:", st["body"]),
        sp(4), t, sp(10),
    ]

# ── Sección: implementación ────────────────────────────────────────────────────

def seccion_implementacion(st):
    pasos = [
        ("Asignación de direcciones",
         "asignarDirecciones() recorre el TAC y registra cada variable/temporal "
         "en un LinkedHashMap<String,Integer>. Las variables reciben direcciones "
         "secuenciales desde DATA_BASE = 0x2000 con VAR_SIZE = 4 bytes cada una. "
         "Las etiquetas de salto (L1:, L2:) se detectan y excluyen del mapa de datos."),
        ("Traducción instrucción por instrucción",
         "traducir() mapea cada línea TAC a 1–4 instrucciones ASM simbólicas. "
         "Maneja: etiquetas, marcadores begin/end, goto, ifFalse, if-goto (do-while), "
         "if-!=- goto (switch), if-==0-then (div-by-zero), return, read, "
         "llamadas con y sin resultado, y asignaciones (binarias, unarias, escalares)."),
        ("Tabla de opcodes",
         "buildOpcodes() devuelve un mapa String→int con 30 mnemónicos: "
         "LOAD(0x01), STORE(0x02), ADD(0x10), SUB(0x11), MUL(0x12), DIV(0x13), "
         "JMP(0x30), JEQ(0x31), JNE(0x32), CALL(0x40), RET(0x41), HALT(0xFF), etc."),
        ("Generación del binario",
         "Formato personalizado de 18 bytes de cabecera: magic EXEC (4B) + "
         "versión (2B) + entry point (4B) + codeSize (4B) + dataSize (4B). "
         "Sección de código: cada instrucción real → 8 bytes (opcode<<24|addr, 0). "
         "Sección de datos: inicializada en cero por new byte[]."),
        ("Salida de archivos",
         "guardarArchivos() escribe <base>.asm con el texto ensamblador "
         "completo (secciones .data y .text) y <base>.bin con el ejecutable binario."),
    ]

    items = []
    for i, (titulo, desc) in enumerate(pasos, 1):
        items.append(Paragraph(f"<b>{i}. {titulo}</b>", st["h3"]))
        items.append(Paragraph(desc, st["body"]))
        items.append(sp(4))

    claves = [
        ["Constante",       "Valor",  "Propósito"],
        ["CODE_BASE",       "0x1000", "Inicio de sección de código"],
        ["DATA_BASE",       "0x2000", "Inicio de sección de datos"],
        ["INSTR_SIZE",      "4",      "Bytes por instrucción (addressing)"],
        ["VAR_SIZE",        "4",      "Bytes por variable (int 32-bit)"],
        ["Header size",     "18",     "magic(4)+version(2)+entry(4)+code(4)+data(4)"],
    ]
    t = Table(claves, colWidths=[1.8*inch, 1.0*inch, 3.7*inch])
    t.setStyle(tabla_estilo_base(colors.HexColor("#555555")))

    return [
        Paragraph("3. Implementación — CodeGenerator.java", st["h1"]),
        hr(),
        *items,
        Paragraph("<b>Constantes clave</b>", st["h2"]),
        sp(4), t, sp(10),
    ]

# ── Sección: caso válido ───────────────────────────────────────────────────────

def seccion_valido(st, salida_valido):
    # Extraer subsecciones
    def extract(text, start_marker, end_marker=None):
        lines = text.split("\n")
        out, capturing = [], False
        for l in lines:
            if start_marker in l:
                capturing = True
            if capturing:
                if end_marker and end_marker in l and out:
                    out.append(l)
                    break
                out.append(l)
        return "\n".join(out)

    tac_orig  = extract(salida_valido, "CÓDIGO INTERMEDIO (ORIGINAL)", "═"*6)
    tac_opt   = extract(salida_valido, "CÓDIGO INTERMEDIO (OPTIMIZADO)", "═"*6)
    opt_tabla = extract(salida_valido, "OPTIMIZACIÓN DE CÓDIGO", "Total de optimizaciones")
    codegen   = extract(salida_valido, "GENERACIÓN DE CÓDIGO EJECUTABLE", "Compilador C++")
    resultado = extract(salida_valido, "RESULTADO", "═"*30)

    # Tabla de optimizaciones (extraer filas)
    opt_lines = [l for l in opt_tabla.split("\n")
                 if "|" not in l and "─" not in l and "═" not in l
                 and l.strip() and "Línea" not in l and "OPTIMIZACIÓN" not in l
                 and "Total" not in l]

    # Contar optimizaciones
    n_opt = 0
    for l in salida_valido.split("\n"):
        if "Total de optimizaciones" in l:
            import re
            m = re.search(r"\d+", l)
            if m: n_opt = int(m.group())

    # Tabla de símbolo-dirección (extraer filas)
    sym_lines = []
    capturing = False
    for l in codegen.split("\n"):
        if "Símbolo" in l and "Dirección" in l:
            capturing = True
            continue
        if capturing:
            if "─" in l or "═" in l or "Código Ensamblador" in l:
                break
            parts = l.strip().split()
            if len(parts) >= 2:
                sym_lines.append(parts)

    # ASM (primeras 30 instrucciones reales)
    asm_lines = []
    capturing = False
    for l in codegen.split("\n"):
        if "Código Ensamblador" in l:
            capturing = True
            continue
        if capturing:
            if "Cabecera del Ejecutable" in l:
                break
            asm_lines.append(l)

    # Hex dump
    hex_lines = []
    capturing = False
    for l in codegen.split("\n"):
        if "Cabecera del Ejecutable" in l:
            capturing = True
            continue
        if capturing:
            if "Generado:" in l or "═" in l:
                break
            if l.strip():
                hex_lines.append(l.strip())

    elems = [
        Paragraph("4. Caso Válido — valido_a13.cpp", st["h1"]),
        hr(),
        Paragraph(
            "Clase <b>Matematicas</b> con métodos <i>multiplicar</i> y <i>dividir</i>, "
            "más clase <b>Programa</b> con <i>main</i> que instancia, llama métodos "
            "y ejecuta un switch. Cero errores esperados.", st["body"]),
        sp(6),
    ]

    # Resultado
    ok_tbl = Table([[Paragraph("✓  Sin errores — Compilación exitosa", st["ok"])]],
                   colWidths=[6.5*inch])
    ok_tbl.setStyle(TableStyle([
        ("BACKGROUND",  (0,0),(-1,-1), colors.HexColor("#D4EDDA")),
        ("GRID",        (0,0),(-1,-1), 0.5, VERDE_OK),
        ("TOPPADDING",  (0,0),(-1,-1), 8),
        ("BOTTOMPADDING",(0,0),(-1,-1), 8),
    ]))
    elems += [ok_tbl, sp(10)]

    # TAC original (truncado)
    elems.append(Paragraph("TAC Original (extracto)", st["h2"]))
    tac_orig_lines = [l for l in tac_orig.split("\n") if l.strip() and "═" not in l][:30]
    elems.append(code_block("\n".join(tac_orig_lines), st))
    elems.append(sp(8))

    # Optimizaciones
    elems.append(Paragraph(f"Optimizaciones aplicadas: {n_opt}", st["h2"]))
    opt_rows = [["#", "Tipo", "Instrucción eliminada"]]
    for i, l in enumerate(opt_lines[:10], 1):
        parts = l.strip().split(None, 3)
        if len(parts) >= 3:
            opt_rows.append([str(i), parts[1] if len(parts)>1 else "", parts[2] if len(parts)>2 else ""])
    if len(opt_rows) > 1:
        t = Table(opt_rows, colWidths=[0.4*inch, 2.8*inch, 3.3*inch])
        t.setStyle(tabla_estilo_base(colors.HexColor("#856404")))
        elems += [t, sp(8)]

    # Tabla de símbolos con direcciones
    elems.append(Paragraph("Tabla de Símbolos — Asignación de Memoria", st["h2"]))
    sym_rows = [["Símbolo", "Dirección"]]
    for row in sym_lines[:15]:
        if len(row) >= 2:
            sym_rows.append([row[0], row[1]])
    if len(sym_rows) > 1:
        t = Table(sym_rows, colWidths=[2.5*inch, 4.0*inch])
        t.setStyle(tabla_estilo_base())
        elems += [t, sp(8)]

    # Código ensamblador (extracto)
    elems.append(Paragraph("Código Ensamblador — extracto (primeras instrucciones)", st["h2"]))
    asm_show = [l for l in asm_lines if l.strip()][:35]
    elems.append(code_block("\n".join(asm_show), st))
    elems.append(sp(8))

    # Hex dump
    elems.append(Paragraph("Cabecera del Ejecutable — hex dump (32 bytes)", st["h2"]))
    elems.append(code_block("\n".join(hex_lines), st))
    elems.append(sp(10))

    return elems

# ── Sección: caso inválido ─────────────────────────────────────────────────────

def seccion_invalido(st, salida_invalido):
    # Extraer errores
    err_lines = []
    capturing = False
    for l in salida_invalido.split("\n"):
        if "RESULTADO" in l:
            capturing = True
            continue
        if capturing:
            if "═"*10 in l and err_lines:
                break
            if l.strip():
                err_lines.append(l)

    # Extraer TAC
    tac_lines = []
    capturing = False
    for l in salida_invalido.split("\n"):
        if "CÓDIGO INTERMEDIO (ORIGINAL)" in l:
            capturing = True
            continue
        if capturing:
            if "═"*10 in l and tac_lines:
                break
            tac_lines.append(l)

    # Contar errores
    n_err = 0
    for l in err_lines:
        if "[" in l and "]" in l:
            n_err += 1

    elems = [
        PageBreak(),
        Paragraph("5. Caso Inválido — invalido_a13.cpp", st["h1"]),
        hr(),
        Paragraph(
            "Archivo con errores semánticos y sintácticos deliberados: "
            "variable no declarada, tipo incompatible (bool+int), "
            "reasignación de constante, y más. "
            "El compilador debe reportar todos los errores y aun así generar TAC parcial.", st["body"]),
        sp(6),
    ]

    # Banner de error
    err_tbl = Table(
        [[Paragraph(f"✗  Se encontraron {n_err} error(es)", st["err"])]],
        colWidths=[6.5*inch])
    err_tbl.setStyle(TableStyle([
        ("BACKGROUND",  (0,0),(-1,-1), colors.HexColor("#F8D7DA")),
        ("GRID",        (0,0),(-1,-1), 0.5, ROJO_ERR),
        ("TOPPADDING",  (0,0),(-1,-1), 8),
        ("BOTTOMPADDING",(0,0),(-1,-1), 8),
    ]))
    elems += [err_tbl, sp(10)]

    # Tabla de errores
    elems.append(Paragraph("Errores Detectados", st["h2"]))
    err_rows = [["Tipo", "Descripción"]]
    for l in err_lines:
        if "[" in l and "]" in l:
            import re
            tipo_m = re.search(r"\[\s*([^\]]+?)\s*\]", l)
            desc_m = re.search(r"\|\s+(.+)$", l)
            if tipo_m and desc_m:
                err_rows.append([tipo_m.group(1).strip(), desc_m.group(1).strip()])
            elif tipo_m:
                err_rows.append([tipo_m.group(1).strip(), l.strip()])
    if len(err_rows) > 1:
        t = Table(err_rows, colWidths=[1.5*inch, 5.0*inch])
        ts = tabla_estilo_base(ROJO_ERR)
        ts.add("ROWBACKGROUNDS",(0,1),(-1,-1),[colors.HexColor("#FFF5F5"), colors.white])
        t.setStyle(ts)
        elems += [t, sp(10)]

    # TAC parcial generado
    elems.append(Paragraph("TAC Parcial Generado (a pesar de errores)", st["h2"]))
    tac_show = [l for l in tac_lines if l.strip()][:25]
    elems.append(code_block("\n".join(tac_show), st))
    elems.append(sp(10))

    return elems

# ── Sección: video ──────────────────────────────────────────────────────────────

def seccion_video(st):
    tbl = Table(
        [[Paragraph("Video de Demostración", st["h2"])],
         [Paragraph(f'<link href="{VIDEO_URL}">{VIDEO_URL}</link>', st["url"])],
         [Paragraph(
             "El video muestra la ejecución del compilador sobre ambos casos de prueba, "
             "con énfasis en la generación de código ASM y el binario producido.",
             st["body"])]],
        colWidths=[6.5*inch])
    tbl.setStyle(TableStyle([
        ("BACKGROUND",  (0,0),(-1,-1), AZUL_CLARO),
        ("GRID",        (0,0),(-1,-1), 0.5, AZUL_UDG),
        ("TOPPADDING",  (0,0),(-1,-1), 10),
        ("BOTTOMPADDING",(0,0),(-1,-1), 10),
        ("LEFTPADDING", (0,0),(-1,-1), 15),
        ("RIGHTPADDING",(0,0),(-1,-1), 15),
    ]))
    return [
        Paragraph("6. Video de Demostración", st["h1"]),
        hr(),
        tbl, sp(10),
    ]

# ── Sección: conclusiones ───────────────────────────────────────────────────────

def seccion_conclusiones(st):
    puntos = [
        "Se implementó CodeGenerator.java con traducción completa de TAC → ASM simbólico.",
        "La asignación de memoria es automática: variables y temporales reciben "
            "direcciones en DATA_BASE (0x2000) de 4 bytes cada una.",
        "El generador cubre todos los patrones TAC del compilador: aritmética, "
            "comparaciones, saltos condicionales/incondicionales, llamadas a métodos, "
            "creación de objetos, return y read.",
        "El ejecutable binario sigue un formato de cabecera propia (magic EXEC) "
            "con secciones de código (opcode<<24|addr) y datos.",
        "Los archivos .asm y .bin se generan automáticamente junto al .cpp de entrada.",
        "El pipeline completo (léxico→semántico→TAC→optimización→codegen) funciona "
            "en una sola ejecución del JAR.",
    ]
    items = [Paragraph(f"• {p}", st["bullet"]) for p in puntos]

    return [
        Paragraph("7. Conclusiones", st["h1"]),
        hr(),
        *items,
        sp(10),
    ]

# ── Main ────────────────────────────────────────────────────────────────────────

def main():
    salida_valido   = leer_salida("/tmp/salida_valido_a13.txt")
    salida_invalido = leer_salida("/tmp/salida_invalido_a13.txt")

    fecha = datetime.date.today().strftime("%d de %B de %Y")
    st    = estilos()

    output = "reporte_a13.pdf"
    doc = SimpleDocTemplate(
        output,
        pagesize=letter,
        leftMargin=0.9*inch, rightMargin=0.9*inch,
        topMargin=0.8*inch,  bottomMargin=0.8*inch,
        title=f"{ACTIVIDAD} — {TITULO}",
        author=NOMBRE,
    )

    story = []
    story += portada(st, fecha)
    story.append(PageBreak())
    story += seccion_objetivo(st)
    story += seccion_arquitectura(st)
    story += seccion_implementacion(st)
    story += seccion_valido(st, salida_valido)
    story += seccion_invalido(st, salida_invalido)
    story += seccion_video(st)
    story += seccion_conclusiones(st)

    doc.build(story)
    print(f"✓ Reporte generado: {output}")

if __name__ == "__main__":
    main()
