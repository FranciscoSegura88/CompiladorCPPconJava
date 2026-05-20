; ══════════════════════════════════════════════════════
; Código Ensamblador — Compilador C++
; SEGURA VALENCIA FRANCISCO
; Traductores de Lenguajes — UDG CUTonalá
; ══════════════════════════════════════════════════════

section .data
    t1:            dd 0        ; addr 0x2000
    t2:            dd 0        ; addr 0x2004
    a:             dd 0        ; addr 0x2008
    t3:            dd 0        ; addr 0x200C
    b:             dd 0        ; addr 0x2010
    t4:            dd 0        ; addr 0x2014
    c:             dd 0        ; addr 0x2018
    t5:            dd 0        ; addr 0x201C
    mayor:         dd 0        ; addr 0x2020
    t6:            dd 0        ; addr 0x2024
    medio:         dd 0        ; addr 0x2028
    t7:            dd 0        ; addr 0x202C
    menor:         dd 0        ; addr 0x2030
    t8:            dd 0        ; addr 0x2034
    t9:            dd 0        ; addr 0x2038
    t10:           dd 0        ; addr 0x203C
    t11:           dd 0        ; addr 0x2040
    t12:           dd 0        ; addr 0x2044
    t13:           dd 0        ; addr 0x2048
    t14:           dd 0        ; addr 0x204C
    t15:           dd 0        ; addr 0x2050
    t16:           dd 0        ; addr 0x2054
    t17:           dd 0        ; addr 0x2058

section .text
    global _start
_start:

    ; begin main
    0x1000    LOAD R1, "Segura Valencia…"            ; cargar literal de cadena
    0x1004    STORE [0x2000], R1                     ; asignar a 't1'

    ; [sin mapa] print t1
    0x1008    LOAD R1, #7                            ; cargar '7'
    0x100C    STORE [0x2004], R1                     ; asignar a 't2'
    0x1010    LOAD R1, [0x2004]                      ; cargar 't2'
    0x1014    STORE [0x2008], R1                     ; asignar a 'a'
    0x1018    LOAD R1, #2                            ; cargar '2'
    0x101C    STORE [0x200C], R1                     ; asignar a 't3'
    0x1020    LOAD R1, [0x200C]                      ; cargar 't3'
    0x1024    STORE [0x2010], R1                     ; asignar a 'b'
    0x1028    LOAD R1, #5                            ; cargar '5'
    0x102C    STORE [0x2014], R1                     ; asignar a 't4'
    0x1030    LOAD R1, [0x2014]                      ; cargar 't4'
    0x1034    STORE [0x2018], R1                     ; asignar a 'c'
    0x1038    LOAD R1, #0                            ; cargar '0'
    0x103C    STORE [0x201C], R1                     ; asignar a 't5'
    0x1040    LOAD R1, [0x201C]                      ; cargar 't5'
    0x1044    STORE [0x2020], R1                     ; asignar a 'mayor'
    0x1048    LOAD R1, #0                            ; cargar '0'
    0x104C    STORE [0x2024], R1                     ; asignar a 't6'
    0x1050    LOAD R1, [0x2024]                      ; cargar 't6'
    0x1054    STORE [0x2028], R1                     ; asignar a 'medio'
    0x1058    LOAD R1, #0                            ; cargar '0'
    0x105C    STORE [0x202C], R1                     ; asignar a 't7'
    0x1060    LOAD R1, [0x202C]                      ; cargar 't7'
    0x1064    STORE [0x2030], R1                     ; asignar a 'menor'
    0x1068    LOAD R1, [0x2008]                      ; cargar 'a'
    0x106C    LOAD R2, [0x2010]                      ; cargar 'b'
    0x1070    GE   R1, R2                            ; R1 ← R1 >= R2
    0x1074    STORE [0x2034], R1                     ; guardar en 't8'
    0x1078    LOAD R1, [0x2008]                      ; cargar 'a'
    0x107C    LOAD R2, [0x2018]                      ; cargar 'c'
    0x1080    GE   R1, R2                            ; R1 ← R1 >= R2
    0x1084    STORE [0x2038], R1                     ; guardar en 't9'
    0x1088    LOAD R1, [0x2034]                      ; cargar 't8'
    0x108C    LOAD R2, [0x2038]                      ; cargar 't9'
    0x1090    AND  R1, R2                            ; R1 ← R1 && R2
    0x1094    STORE [0x203C], R1                     ; guardar en 't10'
    0x1098    LOAD R1, [0x203C]                      ; cargar condición 't10'
    0x109C    CMP  R1, #0                            ; evaluar si es falso
    0x10A0    JEQ  L1                                ; saltar a L1 si falso
    0x10A4    LOAD R1, [0x2008]                      ; cargar 'a'
    0x10A8    STORE [0x2020], R1                     ; asignar a 'mayor'
    0x10AC    LOAD R1, [0x2010]                      ; cargar 'b'
    0x10B0    LOAD R2, [0x2018]                      ; cargar 'c'
    0x10B4    GE   R1, R2                            ; R1 ← R1 >= R2
    0x10B8    STORE [0x2040], R1                     ; guardar en 't11'
    0x10BC    LOAD R1, [0x2040]                      ; cargar condición 't11'
    0x10C0    CMP  R1, #0                            ; evaluar si es falso
    0x10C4    JEQ  L3                                ; saltar a L3 si falso
    0x10C8    LOAD R1, [0x2010]                      ; cargar 'b'
    0x10CC    STORE [0x2028], R1                     ; asignar a 'medio'
    0x10D0    LOAD R1, [0x2018]                      ; cargar 'c'
    0x10D4    STORE [0x2030], R1                     ; asignar a 'menor'
    0x10D8    JMP  L4                                ; salto incondicional → L4
              L3:                                    ; etiqueta de salto
    0x10DC    LOAD R1, [0x2018]                      ; cargar 'c'
    0x10E0    STORE [0x2028], R1                     ; asignar a 'medio'
    0x10E4    LOAD R1, [0x2010]                      ; cargar 'b'
    0x10E8    STORE [0x2030], R1                     ; asignar a 'menor'
              L4:                                    ; etiqueta de salto
    0x10EC    JMP  L2                                ; salto incondicional → L2
              L1:                                    ; etiqueta de salto
    0x10F0    LOAD R1, [0x2010]                      ; cargar 'b'
    0x10F4    LOAD R2, [0x2008]                      ; cargar 'a'
    0x10F8    GE   R1, R2                            ; R1 ← R1 >= R2
    0x10FC    STORE [0x2044], R1                     ; guardar en 't12'
    0x1100    LOAD R1, [0x2010]                      ; cargar 'b'
    0x1104    LOAD R2, [0x2018]                      ; cargar 'c'
    0x1108    GE   R1, R2                            ; R1 ← R1 >= R2
    0x110C    STORE [0x2048], R1                     ; guardar en 't13'
    0x1110    LOAD R1, [0x2044]                      ; cargar 't12'
    0x1114    LOAD R2, [0x2048]                      ; cargar 't13'
    0x1118    AND  R1, R2                            ; R1 ← R1 && R2
    0x111C    STORE [0x204C], R1                     ; guardar en 't14'
    0x1120    LOAD R1, [0x204C]                      ; cargar condición 't14'
    0x1124    CMP  R1, #0                            ; evaluar si es falso
    0x1128    JEQ  L5                                ; saltar a L5 si falso
    0x112C    LOAD R1, [0x2010]                      ; cargar 'b'
    0x1130    STORE [0x2020], R1                     ; asignar a 'mayor'
    0x1134    LOAD R1, [0x2008]                      ; cargar 'a'
    0x1138    LOAD R2, [0x2018]                      ; cargar 'c'
    0x113C    GE   R1, R2                            ; R1 ← R1 >= R2
    0x1140    STORE [0x2050], R1                     ; guardar en 't15'
    0x1144    LOAD R1, [0x2050]                      ; cargar condición 't15'
    0x1148    CMP  R1, #0                            ; evaluar si es falso
    0x114C    JEQ  L7                                ; saltar a L7 si falso
    0x1150    LOAD R1, [0x2008]                      ; cargar 'a'
    0x1154    STORE [0x2028], R1                     ; asignar a 'medio'
    0x1158    LOAD R1, [0x2018]                      ; cargar 'c'
    0x115C    STORE [0x2030], R1                     ; asignar a 'menor'
    0x1160    JMP  L8                                ; salto incondicional → L8
              L7:                                    ; etiqueta de salto
    0x1164    LOAD R1, [0x2018]                      ; cargar 'c'
    0x1168    STORE [0x2028], R1                     ; asignar a 'medio'
    0x116C    LOAD R1, [0x2008]                      ; cargar 'a'
    0x1170    STORE [0x2030], R1                     ; asignar a 'menor'
              L8:                                    ; etiqueta de salto
    0x1174    JMP  L6                                ; salto incondicional → L6
              L5:                                    ; etiqueta de salto
    0x1178    LOAD R1, [0x2018]                      ; cargar 'c'
    0x117C    STORE [0x2020], R1                     ; asignar a 'mayor'
    0x1180    LOAD R1, [0x2008]                      ; cargar 'a'
    0x1184    LOAD R2, [0x2010]                      ; cargar 'b'
    0x1188    GE   R1, R2                            ; R1 ← R1 >= R2
    0x118C    STORE [0x2054], R1                     ; guardar en 't16'
    0x1190    LOAD R1, [0x2054]                      ; cargar condición 't16'
    0x1194    CMP  R1, #0                            ; evaluar si es falso
    0x1198    JEQ  L9                                ; saltar a L9 si falso
    0x119C    LOAD R1, [0x2008]                      ; cargar 'a'
    0x11A0    STORE [0x2028], R1                     ; asignar a 'medio'
    0x11A4    LOAD R1, [0x2010]                      ; cargar 'b'
    0x11A8    STORE [0x2030], R1                     ; asignar a 'menor'
    0x11AC    JMP  L10                               ; salto incondicional → L10
              L9:                                    ; etiqueta de salto
    0x11B0    LOAD R1, [0x2010]                      ; cargar 'b'
    0x11B4    STORE [0x2028], R1                     ; asignar a 'medio'
    0x11B8    LOAD R1, [0x2008]                      ; cargar 'a'
    0x11BC    STORE [0x2030], R1                     ; asignar a 'menor'
              L10:                                   ; etiqueta de salto
              L6:                                    ; etiqueta de salto
              L2:                                    ; etiqueta de salto

    ; [sin mapa] print mayor

    ; [sin mapa] print medio

    ; [sin mapa] print menor
    0x11C0    LOAD R1, #0                            ; cargar '0'
    0x11C4    STORE [0x2058], R1                     ; asignar a 't17'
    0x11C8    LOAD R1, [0x2058]                      ; cargar valor de retorno 't17'
    0x11CC    RET                                    ; retornar al llamador

    ; end main

    HALT                    ; fin del programa
