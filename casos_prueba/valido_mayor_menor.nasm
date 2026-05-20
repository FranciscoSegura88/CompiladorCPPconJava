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

section .bss
    t1: resq 1
    t2: resq 1
    a: resq 1
    t3: resq 1
    b: resq 1
    t4: resq 1
    c: resq 1
    t5: resq 1
    mayor: resq 1
    t6: resq 1
    medio: resq 1
    t7: resq 1
    menor: resq 1
    t8: resq 1
    t9: resq 1
    t10: resq 1
    t11: resq 1
    t12: resq 1
    t13: resq 1
    t14: resq 1
    t15: resq 1
    t16: resq 1
    t17: resq 1

section .text
    global _start

; ── main ───────────────────────────────────
_start:
    lea  rax, [rel str0]
    mov  qword [t1], rax
    mov  rax, qword [t1]
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
    mov rax, 7
    mov  qword [t2], rax
    mov rax, qword [t2]
    mov  qword [a], rax
    mov rax, 2
    mov  qword [t3], rax
    mov rax, qword [t3]
    mov  qword [b], rax
    mov rax, 5
    mov  qword [t4], rax
    mov rax, qword [t4]
    mov  qword [c], rax
    mov rax, 0
    mov  qword [t5], rax
    mov rax, qword [t5]
    mov  qword [mayor], rax
    mov rax, 0
    mov  qword [t6], rax
    mov rax, qword [t6]
    mov  qword [medio], rax
    mov rax, 0
    mov  qword [t7], rax
    mov rax, qword [t7]
    mov  qword [menor], rax
    mov rax, qword [a]
    cmp  rax, qword [b]
    setge al
    movzx rax, al
    mov  qword [t8], rax
    mov rax, qword [a]
    cmp  rax, qword [c]
    setge al
    movzx rax, al
    mov  qword [t9], rax
    mov rax, qword [t8]
    test rax, rax
    setnz cl
    mov rax, qword [t9]
    test rax, rax
    setnz al
    and  al, cl
    movzx rax, al
    mov  qword [t10], rax
    cmp  qword [t10], 0
    je   L1
    mov rax, qword [a]
    mov  qword [mayor], rax
    mov rax, qword [b]
    cmp  rax, qword [c]
    setge al
    movzx rax, al
    mov  qword [t11], rax
    cmp  qword [t11], 0
    je   L3
    mov rax, qword [b]
    mov  qword [medio], rax
    mov rax, qword [c]
    mov  qword [menor], rax
    jmp  L4
L3:
    mov rax, qword [c]
    mov  qword [medio], rax
    mov rax, qword [b]
    mov  qword [menor], rax
L4:
    jmp  L2
L1:
    mov rax, qword [b]
    cmp  rax, qword [a]
    setge al
    movzx rax, al
    mov  qword [t12], rax
    mov rax, qword [b]
    cmp  rax, qword [c]
    setge al
    movzx rax, al
    mov  qword [t13], rax
    mov rax, qword [t12]
    test rax, rax
    setnz cl
    mov rax, qword [t13]
    test rax, rax
    setnz al
    and  al, cl
    movzx rax, al
    mov  qword [t14], rax
    cmp  qword [t14], 0
    je   L5
    mov rax, qword [b]
    mov  qword [mayor], rax
    mov rax, qword [a]
    cmp  rax, qword [c]
    setge al
    movzx rax, al
    mov  qword [t15], rax
    cmp  qword [t15], 0
    je   L7
    mov rax, qword [a]
    mov  qword [medio], rax
    mov rax, qword [c]
    mov  qword [menor], rax
    jmp  L8
L7:
    mov rax, qword [c]
    mov  qword [medio], rax
    mov rax, qword [a]
    mov  qword [menor], rax
L8:
    jmp  L6
L5:
    mov rax, qword [c]
    mov  qword [mayor], rax
    mov rax, qword [a]
    cmp  rax, qword [b]
    setge al
    movzx rax, al
    mov  qword [t16], rax
    cmp  qword [t16], 0
    je   L9
    mov rax, qword [a]
    mov  qword [medio], rax
    mov rax, qword [b]
    mov  qword [menor], rax
    jmp  L10
L9:
    mov rax, qword [b]
    mov  qword [medio], rax
    mov rax, qword [a]
    mov  qword [menor], rax
L10:
L6:
L2:
    mov rax, qword [mayor]
    call _print_int
    mov rax, qword [medio]
    call _print_int
    mov rax, qword [menor]
    call _print_int
    mov rax, 0
    mov  qword [t17], rax
    mov rax, qword [t17]
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
