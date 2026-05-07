/*---------------------------
| SEGURA VALENCIA FRANCISCO |
---------------------------*/

#include <iostream>
using namespace std;

/* Segura Valencia Francisco — Clase Calculadora */
class Calculadora {
public:
    int sumar(int a, int b) {
        int resultado = a + b;
        return resultado;
    }

    int restar(int a, int b) {
        int resultado = a - b;
        return resultado;
    }

    int multiplicar(int a, int b) {
        int resultado = a * b;
        return resultado;
    }

    int dividir(int a, int b) {
        int resultado = a / b;
        return resultado;
    }
};

/* Segura Valencia Francisco — Clase Programa (punto de entrada) */
class Programa {
public:
    int main() {
        cout << "Segura Valencia Francisco";

        Calculadora calc = new Calculadora();

        int a = 10;
        int b = 3;

        cout << endl;
        cout << "=== Operaciones Aritmeticas ===";
        cout << endl;

        int suma = calc.sumar(a, b);
        cout << "Suma (10 + 3): ";
        cout << suma;
        cout << endl;

        int resta = calc.restar(a, b);
        cout << "Resta (10 - 3): ";
        cout << resta;
        cout << endl;

        int producto = calc.multiplicar(a, b);
        cout << "Multiplicacion (10 * 3): ";
        cout << producto;
        cout << endl;

        int division = calc.dividir(a, b);
        cout << "Division (10 / 3): ";
        cout << division;
        cout << endl;

        cout << "=== Bucle While (suma acumulativa) ===";
        cout << endl;

        int i = 0;
        int total = 0;
        while (i < 5) {
            total = total + i;
            i = i + 1;
        }
        cout << "Total (0+1+2+3+4): ";
        cout << total;
        cout << endl;

        cout << "Segura Valencia Francisco";

        return 0;
    }
};
