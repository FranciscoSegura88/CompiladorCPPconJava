/*---------------------------
| SEGURA VALENCIA FRANCISCO |
---------------------------*/

#include <iostream>
using namespace std;

/* Demostración de operaciones con salida visible */
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

class Programa {
public:
    int main() {
        Calculadora calc = new Calculadora();

        int a = 10;
        int b = 3;

        int resultado = calc.sumar(a, b);
        cout << resultado;
        cout << endl;

        resultado = calc.restar(a, b);
        cout << resultado;
        cout << endl;

        resultado = calc.multiplicar(a, b);
        cout << resultado;
        cout << endl;

        resultado = calc.dividir(a, b);
        cout << resultado;
        cout << endl;

        /* While demo: suma 0+1+2+3+4 = 10 */
        int i = 0;
        int total = 0;
        while (i < 5) {
            total = total + i;
            i = i + 1;
        }
        cout << total;
        cout << endl;

        return 0;
    }
};
