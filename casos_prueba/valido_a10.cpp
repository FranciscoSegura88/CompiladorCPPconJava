/*---------------------------
| SEGURA VALENCIA FRANCISCO |
---------------------------*/

#include <iostream>
using namespace std;

class Geometria {
public:
    float calcularArea(float base, float altura) {
        float area = base * altura;
        return area;
    }

    int calcularFactorial(int n) {
        int resultado = 1;
        int i = 1;
        for (i = 1; i <= n; i++) {
            resultado = resultado * i;
        }
        return resultado;
    }

    int clasificarNumero(int x) {
        int tipo = 0;
        switch (x) {
            case 1:
                tipo = 10;
                break;
            case 2:
                tipo = 20;
                break;
            default:
                tipo = 99;
        }
        return tipo;
    }
};

class Programa {
public:
    int main() {
        cout << "Segura Valencia Francisco";

        Geometria g = new Geometria();

        float a = 5.0;
        float b = 3.0;
        float area = g.calcularArea(a, b);

        int n = 5;
        int fact = g.calcularFactorial(n);

        int codigo = 2;
        int clase = g.clasificarNumero(codigo);

        int suma = 0;
        int i = 0;
        for (i = 0; i < 10; i++) {
            suma = suma + i;
        }

        int x = 3;
        int resultado = 0;
        switch (x) {
            case 1:
                resultado = 100;
                break;
            case 3:
                resultado = 300;
                break;
            default:
                resultado = 0;
        }

        cout << "Segura Valencia Francisco";
        return 0;
    }
};
