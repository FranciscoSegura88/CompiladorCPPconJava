#include <iostream>
using namespace std;

class Calculadora {
public:
    int sumar(int a, int b) {
        int resultado = a + b;
        return resultado;
    }

    float dividir(float x, float y) {
        return x / y;
    }

    bool esMayor(int a, int b) {
        return a > b;
    }

    void imprimirMensaje(int n) {
        if (n > 0) {
            int positivo = n;
            cout << positivo;
        } else {
            int negativo = n;
            cout << negativo;
        }
    }

    int contarHasta(int limite) {
        int contador = 0;
        while (contador < limite) {
            contador++;
        }
        return contador;
    }

    int contarDoWhile(int limite) {
        int i = 0;
        do {
            i++;
        } while (i < limite);
        return i;
    }

    bool verificar(int x) {
        bool resultado = x > 0;
        if (resultado) {
            cout << x;
        }
        return resultado;
    }

    int main() {
        int a = 5;
        int b = 3;
        bool condicion = a > b;
        if (condicion) {
            cout << a;
        }
        int total = sumar(a, b);
        cout << total;
        return 0;
    }
};
