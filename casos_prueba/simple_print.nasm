; ══════════════════════════════════════════════════════
; Ensamblador x86-64 (NASM) — Compilador C++
; SEGURA VALENCIA FRANCISCO
; Traductores de Lenguajes — UDG CUTonalá
; nasm -f elf64 out.asm && ld out.o -o out && ./out
; ══════════════════════════════════════════════════════

section .data
    _nl: db 10
    _print_buf: times 32 db 0

section .bss
    t1: resq 1
    a: resq 1
    t2: resq 1
    b: resq 1
    t3: resq 1
    suma: resq 1
    t4: resq 1

section .text
    global _start

; ── main ───────────────────────────────────
_start:
    mov rax, 5
    mov  qword [t1], rax
    mov rax, qword [t1]
    mov  qword [a], rax
    mov rax, 3
    mov  qword [t2], rax
    mov rax, qword [t2]
    mov  qword [b], rax
    mov rax, qword [a]
    add  rax, qword [b]
    mov  qword [t3], rax
    mov rax, qword [t3]
    mov  qword [suma], rax
    mov rax, qword [suma]
    call _print_int
    mov  rax, 1
    mov  rdi, 1
    lea  rsi, [rel _nl]
    mov  rdx, 1
    syscall
    mov rax, 0
    mov  qword [t4], rax
    mov rax, qword [t4]
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
