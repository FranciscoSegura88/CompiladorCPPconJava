/*
 * CASO VALIDO - Análisis Semántico
 * Prueba: tabla de símbolos, tipos, scope, uso correcto
 */
#include <iostream>
using namespace std;

class Calculadora {

    public:
        int resultado;
        double acumulado;
        const double PI = 3.1416;

    int sumar(int a, int b) {
        int total = a + b;
        return total;
    }

    double calcularArea(double radio) {
        double area = PI * radio * radio;
        return area;
    }

    void mostrar(int valor) {
        int copia = valor;
        cout << copia << endl;
    }

    void incrementar(int n) {
        int contador = 0;
        contador = n + 1;
        cout << contador;
    }

};
