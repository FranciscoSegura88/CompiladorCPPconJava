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
    t5:            dd 0        ; addr 0x201C
    calc:          dd 0        ; addr 0x2020
    t6:            dd 0        ; addr 0x2024
    t7:            dd 0        ; addr 0x2028
    t8:            dd 0        ; addr 0x202C
    t9:            dd 0        ; addr 0x2030
    suma:          dd 0        ; addr 0x2034
    t10:           dd 0        ; addr 0x2038
    t11:           dd 0        ; addr 0x203C
    resta:         dd 0        ; addr 0x2040
    t12:           dd 0        ; addr 0x2044
    t13:           dd 0        ; addr 0x2048
    producto:      dd 0        ; addr 0x204C
    t14:           dd 0        ; addr 0x2050
    t15:           dd 0        ; addr 0x2054
    division:      dd 0        ; addr 0x2058
    t16:           dd 0        ; addr 0x205C
    t17:           dd 0        ; addr 0x2060
    t18:           dd 0        ; addr 0x2064
    i:             dd 0        ; addr 0x2068
    t19:           dd 0        ; addr 0x206C
    total:         dd 0        ; addr 0x2070
    t20:           dd 0        ; addr 0x2074
    t21:           dd 0        ; addr 0x2078
    t22:           dd 0        ; addr 0x207C
    t23:           dd 0        ; addr 0x2080
    t24:           dd 0        ; addr 0x2084
    t25:           dd 0        ; addr 0x2088
    t26:           dd 0        ; addr 0x208C
    t27:           dd 0        ; addr 0x2090

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
    0x108C    LOAD R1, "Segura Valencia…"            ; cargar literal de cadena
    0x1090    STORE [0x201C], R1                     ; asignar a 't5'

    ; [sin mapa] print t5
    0x1094    CALL _new_Calculadora                  ; crear instancia de Calculadora
    0x1098    STORE [0x2020], R1                     ; guardar referencia en 'calc'
    0x109C    LOAD R1, #10                           ; cargar '10'
    0x10A0    STORE [0x2024], R1                     ; asignar a 't6'
    0x10A4    LOAD R1, [0x2024]                      ; cargar 't6'
    0x10A8    STORE [0x2004], R1                     ; asignar a 'a'
    0x10AC    LOAD R1, #3                            ; cargar '3'
    0x10B0    STORE [0x2028], R1                     ; asignar a 't7'
    0x10B4    LOAD R1, [0x2028]                      ; cargar 't7'
    0x10B8    STORE [0x2008], R1                     ; asignar a 'b'

    ; [sin mapa] printnl
    0x10BC    LOAD R1, "=== Operaciones…"            ; cargar literal de cadena
    0x10C0    STORE [0x202C], R1                     ; asignar a 't8'

    ; [sin mapa] print t8

    ; [sin mapa] printnl

    ; [sin mapa] param a

    ; [sin mapa] param b
    0x10C4    CALL _sumar                            ; llamar calc.sumar, 2
    0x10C8    STORE [0x2030], R1                     ; guardar retorno en 't9'
    0x10CC    LOAD R1, [0x2030]                      ; cargar 't9'
    0x10D0    STORE [0x2034], R1                     ; asignar a 'suma'
    0x10D4    LOAD R1, "Suma (10 + 3): "             ; cargar literal de cadena
    0x10D8    STORE [0x2038], R1                     ; asignar a 't10'

    ; [sin mapa] print t10

    ; [sin mapa] print suma

    ; [sin mapa] printnl

    ; [sin mapa] param a

    ; [sin mapa] param b
    0x10DC    CALL _restar                           ; llamar calc.restar, 2
    0x10E0    STORE [0x203C], R1                     ; guardar retorno en 't11'
    0x10E4    LOAD R1, [0x203C]                      ; cargar 't11'
    0x10E8    STORE [0x2040], R1                     ; asignar a 'resta'
    0x10EC    LOAD R1, "Resta (10 - 3): "            ; cargar literal de cadena
    0x10F0    STORE [0x2044], R1                     ; asignar a 't12'

    ; [sin mapa] print t12

    ; [sin mapa] print resta

    ; [sin mapa] printnl

    ; [sin mapa] param a

    ; [sin mapa] param b
    0x10F4    CALL _multiplicar                      ; llamar calc.multiplicar, 2
    0x10F8    STORE [0x2048], R1                     ; guardar retorno en 't13'
    0x10FC    LOAD R1, [0x2048]                      ; cargar 't13'
    0x1100    STORE [0x204C], R1                     ; asignar a 'producto'
    0x1104    LOAD R1, "Multiplicacion …"            ; cargar literal de cadena
    0x1108    STORE [0x2050], R1                     ; asignar a 't14'

    ; [sin mapa] print t14

    ; [sin mapa] print producto

    ; [sin mapa] printnl

    ; [sin mapa] param a

    ; [sin mapa] param b
    0x110C    CALL _dividir                          ; llamar calc.dividir, 2
    0x1110    STORE [0x2054], R1                     ; guardar retorno en 't15'
    0x1114    LOAD R1, [0x2054]                      ; cargar 't15'
    0x1118    STORE [0x2058], R1                     ; asignar a 'division'
    0x111C    LOAD R1, "Division (10 / …"            ; cargar literal de cadena
    0x1120    STORE [0x205C], R1                     ; asignar a 't16'

    ; [sin mapa] print t16

    ; [sin mapa] print division

    ; [sin mapa] printnl
    0x1124    LOAD R1, "=== Bucle While…"            ; cargar literal de cadena
    0x1128    STORE [0x2060], R1                     ; asignar a 't17'

    ; [sin mapa] print t17

    ; [sin mapa] printnl
    0x112C    LOAD R1, #0                            ; cargar '0'
    0x1130    STORE [0x2064], R1                     ; asignar a 't18'
    0x1134    LOAD R1, [0x2064]                      ; cargar 't18'
    0x1138    STORE [0x2068], R1                     ; asignar a 'i'
    0x113C    LOAD R1, #0                            ; cargar '0'
    0x1140    STORE [0x206C], R1                     ; asignar a 't19'
    0x1144    LOAD R1, [0x206C]                      ; cargar 't19'
    0x1148    STORE [0x2070], R1                     ; asignar a 'total'
              L1:                                    ; etiqueta de salto
    0x114C    LOAD R1, #5                            ; cargar '5'
    0x1150    STORE [0x2074], R1                     ; asignar a 't20'
    0x1154    LOAD R1, [0x2068]                      ; cargar 'i'
    0x1158    LOAD R2, [0x2074]                      ; cargar 't20'
    0x115C    LT   R1, R2                            ; R1 ← R1 < R2
    0x1160    STORE [0x2078], R1                     ; guardar en 't21'
    0x1164    LOAD R1, [0x2078]                      ; cargar condición 't21'
    0x1168    CMP  R1, #0                            ; evaluar si es falso
    0x116C    JEQ  L2                                ; saltar a L2 si falso
    0x1170    LOAD R1, [0x2070]                      ; cargar 'total'
    0x1174    LOAD R2, [0x2068]                      ; cargar 'i'
    0x1178    ADD  R1, R2                            ; R1 ← R1 + R2
    0x117C    STORE [0x207C], R1                     ; guardar en 't22'
    0x1180    LOAD R1, [0x207C]                      ; cargar 't22'
    0x1184    STORE [0x2070], R1                     ; asignar a 'total'
    0x1188    LOAD R1, #1                            ; cargar '1'
    0x118C    STORE [0x2080], R1                     ; asignar a 't23'
    0x1190    LOAD R1, [0x2068]                      ; cargar 'i'
    0x1194    LOAD R2, [0x2080]                      ; cargar 't23'
    0x1198    ADD  R1, R2                            ; R1 ← R1 + R2
    0x119C    STORE [0x2084], R1                     ; guardar en 't24'
    0x11A0    LOAD R1, [0x2084]                      ; cargar 't24'
    0x11A4    STORE [0x2068], R1                     ; asignar a 'i'
    0x11A8    JMP  L1                                ; salto incondicional → L1
              L2:                                    ; etiqueta de salto
    0x11AC    LOAD R1, "Total (0+1+2+3+…"            ; cargar literal de cadena
    0x11B0    STORE [0x2088], R1                     ; asignar a 't25'

    ; [sin mapa] print t25

    ; [sin mapa] print total

    ; [sin mapa] printnl
    0x11B4    LOAD R1, "Segura Valencia…"            ; cargar literal de cadena
    0x11B8    STORE [0x208C], R1                     ; asignar a 't26'

    ; [sin mapa] print t26
    0x11BC    LOAD R1, #0                            ; cargar '0'
    0x11C0    STORE [0x2090], R1                     ; asignar a 't27'
    0x11C4    LOAD R1, [0x2090]                      ; cargar valor de retorno 't27'
    0x11C8    RET                                    ; retornar al llamador

    ; end main

    HALT                    ; fin del programa
