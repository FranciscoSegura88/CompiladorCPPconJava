; ══════════════════════════════════════════════════════
; Código Ensamblador — Compilador C++
; SEGURA VALENCIA FRANCISCO
; Traductores de Lenguajes — UDG CUTonalá
; ══════════════════════════════════════════════════════

section .data
    t1:            dd 0        ; addr 0x2000
    t2:            dd 0        ; addr 0x2004
    n:             dd 0        ; addr 0x2008
    t3:            dd 0        ; addr 0x200C
    suma:          dd 0        ; addr 0x2010
    t5:            dd 0        ; addr 0x2014
    i:             dd 0        ; addr 0x2018
    t6:            dd 0        ; addr 0x201C
    t7:            dd 0        ; addr 0x2020
    t8:            dd 0        ; addr 0x2024

section .text
    global _start
_start:

    ; begin main
    0x1000    LOAD R1, "Segura Valencia…"            ; cargar literal de cadena
    0x1004    STORE [0x2000], R1                     ; asignar a 't1'

    ; [sin mapa] print t1
    0x1008    LOAD R1, #10                           ; cargar '10'
    0x100C    STORE [0x2004], R1                     ; asignar a 't2'
    0x1010    LOAD R1, [0x2004]                      ; cargar 't2'
    0x1014    STORE [0x2008], R1                     ; asignar a 'n'
    0x1018    LOAD R1, #0                            ; cargar '0'
    0x101C    STORE [0x200C], R1                     ; asignar a 't3'
    0x1020    LOAD R1, [0x200C]                      ; cargar 't3'
    0x1024    STORE [0x2010], R1                     ; asignar a 'suma'
    0x1028    LOAD R1, #1                            ; cargar '1'
    0x102C    STORE [0x2014], R1                     ; asignar a 't5'
    0x1030    LOAD R1, [0x2014]                      ; cargar 't5'
    0x1034    STORE [0x2018], R1                     ; asignar a 'i'
              L1:                                    ; etiqueta de salto
    0x1038    LOAD R1, [0x2018]                      ; cargar 'i'
    0x103C    LOAD R2, [0x2008]                      ; cargar 'n'
    0x1040    LE   R1, R2                            ; R1 ← R1 <= R2
    0x1044    STORE [0x201C], R1                     ; guardar en 't6'
    0x1048    LOAD R1, [0x201C]                      ; cargar condición 't6'
    0x104C    CMP  R1, #0                            ; evaluar si es falso
    0x1050    JEQ  L2                                ; saltar a L2 si falso
    0x1054    LOAD R1, [0x2010]                      ; cargar 'suma'
    0x1058    LOAD R2, [0x2018]                      ; cargar 'i'
    0x105C    ADD  R1, R2                            ; R1 ← R1 + R2
    0x1060    STORE [0x2020], R1                     ; guardar en 't7'
    0x1064    LOAD R1, [0x2020]                      ; cargar 't7'
    0x1068    STORE [0x2010], R1                     ; asignar a 'suma'
    0x106C    LOAD R1, [0x2018]                      ; cargar 'i'
    0x1070    LOAD R2, #1                            ; cargar '1'
    0x1074    ADD  R1, R2                            ; R1 ← R1 + R2
    0x1078    STORE [0x2018], R1                     ; guardar en 'i'
    0x107C    JMP  L1                                ; salto incondicional → L1
              L2:                                    ; etiqueta de salto

    ; [sin mapa] print suma

    ; [sin mapa] printnl
    0x1080    LOAD R1, #0                            ; cargar '0'
    0x1084    STORE [0x2024], R1                     ; asignar a 't8'
    0x1088    LOAD R1, [0x2024]                      ; cargar valor de retorno 't8'
    0x108C    RET                                    ; retornar al llamador

    ; end main

    HALT                    ; fin del programa
