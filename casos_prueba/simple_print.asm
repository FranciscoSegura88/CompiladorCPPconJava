; ══════════════════════════════════════════════════════
; Código Ensamblador — Compilador C++
; SEGURA VALENCIA FRANCISCO
; Traductores de Lenguajes — UDG CUTonalá
; ══════════════════════════════════════════════════════

section .data
    t1:            dd 0        ; addr 0x2000
    a:             dd 0        ; addr 0x2004
    t2:            dd 0        ; addr 0x2008
    b:             dd 0        ; addr 0x200C
    t3:            dd 0        ; addr 0x2010
    suma:          dd 0        ; addr 0x2014
    t4:            dd 0        ; addr 0x2018

section .text
    global _start
_start:

    ; begin main
    0x1000    LOAD R1, #5                            ; cargar '5'
    0x1004    STORE [0x2000], R1                     ; asignar a 't1'
    0x1008    LOAD R1, [0x2000]                      ; cargar 't1'
    0x100C    STORE [0x2004], R1                     ; asignar a 'a'
    0x1010    LOAD R1, #3                            ; cargar '3'
    0x1014    STORE [0x2008], R1                     ; asignar a 't2'
    0x1018    LOAD R1, [0x2008]                      ; cargar 't2'
    0x101C    STORE [0x200C], R1                     ; asignar a 'b'
    0x1020    LOAD R1, [0x2004]                      ; cargar 'a'
    0x1024    LOAD R2, [0x200C]                      ; cargar 'b'
    0x1028    ADD  R1, R2                            ; R1 ← R1 + R2
    0x102C    STORE [0x2010], R1                     ; guardar en 't3'
    0x1030    LOAD R1, [0x2010]                      ; cargar 't3'
    0x1034    STORE [0x2014], R1                     ; asignar a 'suma'

    ; [sin mapa] print suma

    ; [sin mapa] printnl
    0x1038    LOAD R1, #0                            ; cargar '0'
    0x103C    STORE [0x2018], R1                     ; asignar a 't4'
    0x1040    LOAD R1, [0x2018]                      ; cargar valor de retorno 't4'
    0x1044    RET                                    ; retornar al llamador

    ; end main

    HALT                    ; fin del programa
