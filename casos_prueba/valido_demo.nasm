; ══════════════════════════════════════════════════════
; Ensamblador x86-64 (NASM) — Compilador C++
; SEGURA VALENCIA FRANCISCO
; Traductores de Lenguajes — UDG CUTonalá
; nasm -f elf64 out.asm && ld out.o -o out && ./out
; ══════════════════════════════════════════════════════

section .data
    _nl: db 10
    _print_buf: times 32 db 0
    str0: db `Segura Valencia Francisco`, 0
    str1: db `=== Operaciones Aritmeticas ===`, 0
    str2: db `Suma (10 + 3): `, 0
    str3: db `Resta (10 - 3): `, 0
    str4: db `Multiplicacion (10 * 3): `, 0
    str5: db `Division (10 / 3): `, 0
    str6: db `=== Bucle While (suma acumulativa) ===`, 0
    str7: db `Total (0+1+2+3+4): `, 0

section .bss
    a: resq 1
    b: resq 1
    t1: resq 1
    resultado: resq 1
    t2: resq 1
    t3: resq 1
    t4: resq 1
    t5: resq 1
    calc: resq 1
    t6: resq 1
    t7: resq 1
    t8: resq 1
    t9: resq 1
    suma: resq 1
    t10: resq 1
    t11: resq 1
    resta: resq 1
    t12: resq 1
    t13: resq 1
    producto: resq 1
    t14: resq 1
    t15: resq 1
    division: resq 1
    t16: resq 1
    t17: resq 1
    t18: resq 1
    i: resq 1
    t19: resq 1
    total: resq 1
    t20: resq 1
    t21: resq 1
    t22: resq 1
    t23: resq 1
    t24: resq 1
    t25: resq 1
    t26: resq 1
    t27: resq 1

section .text
    global _start

; ── sumar ───────────────────────────────────
_sumar:
    push rbp
    mov  rbp, rsp
    mov  qword [a], rdi
    mov  qword [b], rsi
    mov rax, qword [a]
    add  rax, qword [b]
    mov  qword [t1], rax
    mov rax, qword [t1]
    mov  qword [resultado], rax
    mov rax, qword [resultado]
    pop  rbp
    ret

; ── restar ───────────────────────────────────
_restar:
    push rbp
    mov  rbp, rsp
    mov  qword [a], rdi
    mov  qword [b], rsi
    mov rax, qword [a]
    sub  rax, qword [b]
    mov  qword [t2], rax
    mov rax, qword [t2]
    mov  qword [resultado], rax
    mov rax, qword [resultado]
    pop  rbp
    ret

; ── multiplicar ───────────────────────────────────
_multiplicar:
    push rbp
    mov  rbp, rsp
    mov  qword [a], rdi
    mov  qword [b], rsi
    mov rax, qword [a]
    mov rbx, qword [b]
    imul rax, rbx
    mov  qword [t3], rax
    mov rax, qword [t3]
    mov  qword [resultado], rax
    mov rax, qword [resultado]
    pop  rbp
    ret

; ── dividir ───────────────────────────────────
_dividir:
    push rbp
    mov  rbp, rsp
    mov  qword [a], rdi
    mov  qword [b], rsi
    cmp  qword [b], 0
    je   _div_by_zero
    mov rax, qword [a]
    mov rbx, qword [b]
    xor  rdx, rdx
    idiv rbx
    mov  qword [t4], rax
    mov rax, qword [t4]
    mov  qword [resultado], rax
    mov rax, qword [resultado]
    pop  rbp
    ret

; ── main ───────────────────────────────────
_start:
    lea  rax, [rel str0]
    mov  qword [t5], rax
    mov  rax, qword [t5]
    mov  rsi, rax
    ; strlen
    mov  rcx, -1
    mov  rdi, rsi
    xor  al, al
    cld
    repne scasb
    not  rcx
    dec  rcx
    mov  rdx, rcx
    mov  rax, 1
    mov  rdi, 1
    syscall
    mov  qword [calc], 1  ; create Calculadora
    mov rax, 10
    mov  qword [t6], rax
    mov rax, qword [t6]
    mov  qword [a], rax
    mov rax, 3
    mov  qword [t7], rax
    mov rax, qword [t7]
    mov  qword [b], rax
    mov  rax, 1
    mov  rdi, 1
    lea  rsi, [rel _nl]
    mov  rdx, 1
    syscall
    lea  rax, [rel str1]
    mov  qword [t8], rax
    mov  rax, qword [t8]
    mov  rsi, rax
    ; strlen
    mov  rcx, -1
    mov  rdi, rsi
    xor  al, al
    cld
    repne scasb
    not  rcx
    dec  rcx
    mov  rdx, rcx
    mov  rax, 1
    mov  rdi, 1
    syscall
    mov  rax, 1
    mov  rdi, 1
    lea  rsi, [rel _nl]
    mov  rdx, 1
    syscall
    mov rdi, qword [a]
    mov rsi, qword [b]
    call _sumar
    mov  qword [t9], rax
    mov rax, qword [t9]
    mov  qword [suma], rax
    lea  rax, [rel str2]
    mov  qword [t10], rax
    mov  rax, qword [t10]
    mov  rsi, rax
    ; strlen
    mov  rcx, -1
    mov  rdi, rsi
    xor  al, al
    cld
    repne scasb
    not  rcx
    dec  rcx
    mov  rdx, rcx
    mov  rax, 1
    mov  rdi, 1
    syscall
    mov rax, qword [suma]
    call _print_int
    mov  rax, 1
    mov  rdi, 1
    lea  rsi, [rel _nl]
    mov  rdx, 1
    syscall
    mov rdi, qword [a]
    mov rsi, qword [b]
    call _restar
    mov  qword [t11], rax
    mov rax, qword [t11]
    mov  qword [resta], rax
    lea  rax, [rel str3]
    mov  qword [t12], rax
    mov  rax, qword [t12]
    mov  rsi, rax
    ; strlen
    mov  rcx, -1
    mov  rdi, rsi
    xor  al, al
    cld
    repne scasb
    not  rcx
    dec  rcx
    mov  rdx, rcx
    mov  rax, 1
    mov  rdi, 1
    syscall
    mov rax, qword [resta]
    call _print_int
    mov  rax, 1
    mov  rdi, 1
    lea  rsi, [rel _nl]
    mov  rdx, 1
    syscall
    mov rdi, qword [a]
    mov rsi, qword [b]
    call _multiplicar
    mov  qword [t13], rax
    mov rax, qword [t13]
    mov  qword [producto], rax
    lea  rax, [rel str4]
    mov  qword [t14], rax
    mov  rax, qword [t14]
    mov  rsi, rax
    ; strlen
    mov  rcx, -1
    mov  rdi, rsi
    xor  al, al
    cld
    repne scasb
    not  rcx
    dec  rcx
    mov  rdx, rcx
    mov  rax, 1
    mov  rdi, 1
    syscall
    mov rax, qword [producto]
    call _print_int
    mov  rax, 1
    mov  rdi, 1
    lea  rsi, [rel _nl]
    mov  rdx, 1
    syscall
    mov rdi, qword [a]
    mov rsi, qword [b]
    call _dividir
    mov  qword [t15], rax
    mov rax, qword [t15]
    mov  qword [division], rax
    lea  rax, [rel str5]
    mov  qword [t16], rax
    mov  rax, qword [t16]
    mov  rsi, rax
    ; strlen
    mov  rcx, -1
    mov  rdi, rsi
    xor  al, al
    cld
    repne scasb
    not  rcx
    dec  rcx
    mov  rdx, rcx
    mov  rax, 1
    mov  rdi, 1
    syscall
    mov rax, qword [division]
    call _print_int
    mov  rax, 1
    mov  rdi, 1
    lea  rsi, [rel _nl]
    mov  rdx, 1
    syscall
    lea  rax, [rel str6]
    mov  qword [t17], rax
    mov  rax, qword [t17]
    mov  rsi, rax
    ; strlen
    mov  rcx, -1
    mov  rdi, rsi
    xor  al, al
    cld
    repne scasb
    not  rcx
    dec  rcx
    mov  rdx, rcx
    mov  rax, 1
    mov  rdi, 1
    syscall
    mov  rax, 1
    mov  rdi, 1
    lea  rsi, [rel _nl]
    mov  rdx, 1
    syscall
    mov rax, 0
    mov  qword [t18], rax
    mov rax, qword [t18]
    mov  qword [i], rax
    mov rax, 0
    mov  qword [t19], rax
    mov rax, qword [t19]
    mov  qword [total], rax
L1:
    mov rax, 5
    mov  qword [t20], rax
    mov rax, qword [i]
    cmp  rax, qword [t20]
    setl al
    movzx rax, al
    mov  qword [t21], rax
    cmp  qword [t21], 0
    je   L2
    mov rax, qword [total]
    add  rax, qword [i]
    mov  qword [t22], rax
    mov rax, qword [t22]
    mov  qword [total], rax
    mov rax, 1
    mov  qword [t23], rax
    mov rax, qword [i]
    add  rax, qword [t23]
    mov  qword [t24], rax
    mov rax, qword [t24]
    mov  qword [i], rax
    jmp  L1
L2:
    lea  rax, [rel str7]
    mov  qword [t25], rax
    mov  rax, qword [t25]
    mov  rsi, rax
    ; strlen
    mov  rcx, -1
    mov  rdi, rsi
    xor  al, al
    cld
    repne scasb
    not  rcx
    dec  rcx
    mov  rdx, rcx
    mov  rax, 1
    mov  rdi, 1
    syscall
    mov rax, qword [total]
    call _print_int
    mov  rax, 1
    mov  rdi, 1
    lea  rsi, [rel _nl]
    mov  rdx, 1
    syscall
    lea  rax, [rel str0]
    mov  qword [t26], rax
    mov  rax, qword [t26]
    mov  rsi, rax
    ; strlen
    mov  rcx, -1
    mov  rdi, rsi
    xor  al, al
    cld
    repne scasb
    not  rcx
    dec  rcx
    mov  rdx, rcx
    mov  rax, 1
    mov  rdi, 1
    syscall
    mov rax, 0
    mov  qword [t27], rax
    mov rax, qword [t27]
    mov  rdi, rax
    mov  rax, 60
    syscall


; ── Print integer helper ─────────────────────────────
_print_int:
    push rax
    push rbx
    push rcx
    push rdx
    push rsi
    push rdi
    mov  rbx, 10
    lea  rcx, [rel _print_buf + 31]
    mov  byte [rcx], 10      ; newline at end
    dec  rcx
    mov  byte [rcx], 0       ; null terminator
    dec  rcx
.convert:
    xor  rdx, rdx
    div  rbx
    add  dl, '0'
    mov  byte [rcx], dl
    dec  rcx
    test rax, rax
    jnz  .convert
    inc  rcx                 ; point to first digit
    mov  rsi, rcx
    lea  rdi, [rel _print_buf + 30]
    sub  rdi, rsi            ; length
    mov  rdx, rdi
    mov  rax, 1
    mov  rdi, 1              ; stdout
    syscall
    pop  rdi
    pop  rsi
    pop  rdx
    pop  rcx
    pop  rbx
    pop  rax
    ret

_div_by_zero:
    ; División por cero — salir con código 1
    mov  rdi, 1
    mov  rax, 60
    syscall
