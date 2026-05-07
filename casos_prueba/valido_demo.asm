; ══════════════════════════════════════════════════════
; Código Ensamblador — Compilador C++
; SEGURA VALENCIA FRANCISCO
; Traductores de Lenguajes — UDG CUTonalá
; ══════════════════════════════════════════════════════

section .data
    t1:            dd 0        ; addr 0x2000
    a:             dd 0        ; addr 0x2004
    b:             dd 0        ; addr 0x2008
    resultado:     dd 0        ; addr 0x200C
    t2:            dd 0        ; addr 0x2010
    t3:            dd 0        ; addr 0x2014
    t4:            dd 0        ; addr 0x2018
    calc:          dd 0        ; addr 0x201C
    t5:            dd 0        ; addr 0x2020
    t6:            dd 0        ; addr 0x2024
    t7:            dd 0        ; addr 0x2028
    t8:            dd 0        ; addr 0x202C
    t9:            dd 0        ; addr 0x2030
    t10:           dd 0        ; addr 0x2034
    t11:           dd 0        ; addr 0x2038
    i:             dd 0        ; addr 0x203C
    t12:           dd 0        ; addr 0x2040
    total:         dd 0        ; addr 0x2044
    t13:           dd 0        ; addr 0x2048
    t14:           dd 0        ; addr 0x204C
    t15:           dd 0        ; addr 0x2050
    t16:           dd 0        ; addr 0x2054
    t17:           dd 0        ; addr 0x2058
    t18:           dd 0        ; addr 0x205C

section .text
    global _start
_start:

    ; begin sumar

    ; [sin mapa] getparam a

    ; [sin mapa] getparam b
    0x1000    LOAD R1, [0x2004]                      ; cargar 'a'
    0x1004    LOAD R2, [0x2008]                      ; cargar 'b'
    0x1008    ADD  R1, R2                            ; R1 ← R1 + R2
    0x100C    STORE [0x2000], R1                     ; guardar en 't1'
    0x1010    LOAD R1, [0x2000]                      ; cargar 't1'
    0x1014    STORE [0x200C], R1                     ; asignar a 'resultado'
    0x1018    LOAD R1, [0x200C]                      ; cargar valor de retorno 'resultado'
    0x101C    RET                                    ; retornar al llamador

    ; end sumar

    ; begin restar

    ; [sin mapa] getparam a

    ; [sin mapa] getparam b
    0x1020    LOAD R1, [0x2004]                      ; cargar 'a'
    0x1024    LOAD R2, [0x2008]                      ; cargar 'b'
    0x1028    SUB  R1, R2                            ; R1 ← R1 - R2
    0x102C    STORE [0x2010], R1                     ; guardar en 't2'
    0x1030    LOAD R1, [0x2010]                      ; cargar 't2'
    0x1034    STORE [0x200C], R1                     ; asignar a 'resultado'
    0x1038    LOAD R1, [0x200C]                      ; cargar valor de retorno 'resultado'
    0x103C    RET                                    ; retornar al llamador

    ; end restar

    ; begin multiplicar

    ; [sin mapa] getparam a

    ; [sin mapa] getparam b
    0x1040    LOAD R1, [0x2004]                      ; cargar 'a'
    0x1044    LOAD R2, [0x2008]                      ; cargar 'b'
    0x1048    MUL  R1, R2                            ; R1 ← R1 * R2
    0x104C    STORE [0x2014], R1                     ; guardar en 't3'
    0x1050    LOAD R1, [0x2014]                      ; cargar 't3'
    0x1054    STORE [0x200C], R1                     ; asignar a 'resultado'
    0x1058    LOAD R1, [0x200C]                      ; cargar valor de retorno 'resultado'
    0x105C    RET                                    ; retornar al llamador

    ; end multiplicar

    ; begin dividir

    ; [sin mapa] getparam a

    ; [sin mapa] getparam b
    0x1060    LOAD R1, [0x2008]                      ; verificar divisor 'b'
    0x1064    CMP  R1, #0                            ; comparar con cero
    0x1068    JEQ  _div_error                        ; saltar a error si divisor = 0
    0x106C    LOAD R1, [0x2004]                      ; cargar 'a'
    0x1070    LOAD R2, [0x2008]                      ; cargar 'b'
    0x1074    DIV  R1, R2                            ; R1 ← R1 / R2
    0x1078    STORE [0x2018], R1                     ; guardar en 't4'
    0x107C    LOAD R1, [0x2018]                      ; cargar 't4'
    0x1080    STORE [0x200C], R1                     ; asignar a 'resultado'
    0x1084    LOAD R1, [0x200C]                      ; cargar valor de retorno 'resultado'
    0x1088    RET                                    ; retornar al llamador

    ; end dividir

    ; begin main
    0x108C    CALL _new_Calculadora                  ; crear instancia de Calculadora
    0x1090    STORE [0x201C], R1                     ; guardar referencia en 'calc'
    0x1094    LOAD R1, #10                           ; cargar '10'
    0x1098    STORE [0x2020], R1                     ; asignar a 't5'
    0x109C    LOAD R1, [0x2020]                      ; cargar 't5'
    0x10A0    STORE [0x2004], R1                     ; asignar a 'a'
    0x10A4    LOAD R1, #3                            ; cargar '3'
    0x10A8    STORE [0x2024], R1                     ; asignar a 't6'
    0x10AC    LOAD R1, [0x2024]                      ; cargar 't6'
    0x10B0    STORE [0x2008], R1                     ; asignar a 'b'

    ; [sin mapa] param a

    ; [sin mapa] param b
    0x10B4    CALL _sumar                            ; llamar calc.sumar, 2
    0x10B8    STORE [0x2028], R1                     ; guardar retorno en 't7'
    0x10BC    LOAD R1, [0x2028]                      ; cargar 't7'
    0x10C0    STORE [0x200C], R1                     ; asignar a 'resultado'

    ; [sin mapa] print resultado

    ; [sin mapa] printnl

    ; [sin mapa] param a

    ; [sin mapa] param b
    0x10C4    CALL _restar                           ; llamar calc.restar, 2
    0x10C8    STORE [0x202C], R1                     ; guardar retorno en 't8'
    0x10CC    LOAD R1, [0x202C]                      ; cargar 't8'
    0x10D0    STORE [0x200C], R1                     ; asignar a 'resultado'

    ; [sin mapa] print resultado

    ; [sin mapa] printnl

    ; [sin mapa] param a

    ; [sin mapa] param b
    0x10D4    CALL _multiplicar                      ; llamar calc.multiplicar, 2
    0x10D8    STORE [0x2030], R1                     ; guardar retorno en 't9'
    0x10DC    LOAD R1, [0x2030]                      ; cargar 't9'
    0x10E0    STORE [0x200C], R1                     ; asignar a 'resultado'

    ; [sin mapa] print resultado

    ; [sin mapa] printnl

    ; [sin mapa] param a

    ; [sin mapa] param b
    0x10E4    CALL _dividir                          ; llamar calc.dividir, 2
    0x10E8    STORE [0x2034], R1                     ; guardar retorno en 't10'
    0x10EC    LOAD R1, [0x2034]                      ; cargar 't10'
    0x10F0    STORE [0x200C], R1                     ; asignar a 'resultado'

    ; [sin mapa] print resultado

    ; [sin mapa] printnl
    0x10F4    LOAD R1, #0                            ; cargar '0'
    0x10F8    STORE [0x2038], R1                     ; asignar a 't11'
    0x10FC    LOAD R1, [0x2038]                      ; cargar 't11'
    0x1100    STORE [0x203C], R1                     ; asignar a 'i'
    0x1104    LOAD R1, #0                            ; cargar '0'
    0x1108    STORE [0x2040], R1                     ; asignar a 't12'
    0x110C    LOAD R1, [0x2040]                      ; cargar 't12'
    0x1110    STORE [0x2044], R1                     ; asignar a 'total'
              L1:                                    ; etiqueta de salto
    0x1114    LOAD R1, #5                            ; cargar '5'
    0x1118    STORE [0x2048], R1                     ; asignar a 't13'
    0x111C    LOAD R1, [0x203C]                      ; cargar 'i'
    0x1120    LOAD R2, [0x2048]                      ; cargar 't13'
    0x1124    LT   R1, R2                            ; R1 ← R1 < R2
    0x1128    STORE [0x204C], R1                     ; guardar en 't14'
    0x112C    LOAD R1, [0x204C]                      ; cargar condición 't14'
    0x1130    CMP  R1, #0                            ; evaluar si es falso
    0x1134    JEQ  L2                                ; saltar a L2 si falso
    0x1138    LOAD R1, [0x2044]                      ; cargar 'total'
    0x113C    LOAD R2, [0x203C]                      ; cargar 'i'
    0x1140    ADD  R1, R2                            ; R1 ← R1 + R2
    0x1144    STORE [0x2050], R1                     ; guardar en 't15'
    0x1148    LOAD R1, [0x2050]                      ; cargar 't15'
    0x114C    STORE [0x2044], R1                     ; asignar a 'total'
    0x1150    LOAD R1, #1                            ; cargar '1'
    0x1154    STORE [0x2054], R1                     ; asignar a 't16'
    0x1158    LOAD R1, [0x203C]                      ; cargar 'i'
    0x115C    LOAD R2, [0x2054]                      ; cargar 't16'
    0x1160    ADD  R1, R2                            ; R1 ← R1 + R2
    0x1164    STORE [0x2058], R1                     ; guardar en 't17'
    0x1168    LOAD R1, [0x2058]                      ; cargar 't17'
    0x116C    STORE [0x203C], R1                     ; asignar a 'i'
    0x1170    JMP  L1                                ; salto incondicional → L1
              L2:                                    ; etiqueta de salto

    ; [sin mapa] print total

    ; [sin mapa] printnl
    0x1174    LOAD R1, #0                            ; cargar '0'
    0x1178    STORE [0x205C], R1                     ; asignar a 't18'
    0x117C    LOAD R1, [0x205C]                      ; cargar valor de retorno 't18'
    0x1180    RET                                    ; retornar al llamador

    ; end main

    HALT                    ; fin del programa
