/*---------------------------
| SEGURA VALENCIA FRANCISCO |
---------------------------*/

// Caso de prueba valido — Practica A8: Validacion de tipos en expresiones

#include <iostream>
using namespace std;

class Calculadora {
public:
    // Aritmetica numerica valida
    int sumar(int a, int b) {
        int resultado = a + b;
        return resultado;
    }

    double promediar(int a, int b) {
        double prom = (a + b) / 2;
        return prom;
    }

    // Logica booleana valida
    bool esPositivo(int n) {
        bool pos = n > 0;
        return pos;
    }

    bool ambosPositivos(int a, int b) {
        bool r = (a > 0) && (b > 0);
        return r;
    }

    bool algunoPositivo(int a, int b) {
        bool r = (a > 0) || (b > 0);
        return r;
    }

    bool noEsNegativo(int n) {
        bool esNeg = n < 0;
        bool r = !esNeg;
        return r;
    }

    // Comparaciones entre tipos compatibles
    bool compararEnteros(int a, int b) {
        bool igual   = a == b;
        bool distinto = a != b;
        bool menor   = a < b;
        bool mayor   = a > b;
        bool menorIg = a <= b;
        bool mayorIg = a >= b;
        return igual;
    }

    bool compararFlotantes(double x, double y) {
        bool r = x < y;
        return r;
    }

    // Expresiones complejas validas
    int exprCompleja(int a, int b, int c) {
        int r = (a + b) * (c - 1);
        return r;
    }

    bool condicionCompuesta(int x, int y, int z) {
        bool r = (x > 0) && (y > 0) && (z > 0);
        return r;
    }

    // Widening: asignacion int a double/float
    double anchoImplicito(int n) {
        double d = n;
        float  f = n;
        return d + f;
    }

    // Incremento/decremento aritmetico valido
    void conteo(int n) {
        n++;
        n--;
        n += 5;
        n -= 3;
    }
};
