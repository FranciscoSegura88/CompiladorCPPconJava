// Segura Valencia Francisco
#include <iostream>
using namespace std;

class Errores {
public:
    int sumar(int a, int b) {
        return a + b;
    }

    bool esPar(int n) {
        return n > 0;
    }

    void saludar(int x) {
        cout << x;
    }

    int calcular(int x) {
        // ERROR 1: condicion no booleana en if (tipo int, no bool)
        if (x) {
            cout << x;
        }

        // ERROR 2: condicion no booleana en while (tipo int)
        int i = 0;
        while (i) {
            i++;
        }

        // ERROR 3: return con tipo incompatible (se espera int, se retorna bool)
        bool flag = x > 0;
        return flag;
    }

    void operaciones() {
        // ERROR 4: llamada con demasiados argumentos (sumar requiere 2, se pasan 3)
        int r1 = sumar(1, 2, 3);

        // ERROR 5: llamada con pocos argumentos (sumar requiere 2, se pasa 1)
        int r2 = sumar(5);

        // ERROR 6: metodo void retorna valor
        return 42;
    }

    float flotantes(float a, float b) {
        // ERROR 7: condicion no booleana en do-while (tipo float)
        float suma = a + b;
        do {
            suma = suma + 1.0;
        } while (suma);

        return suma;
    }

    int main() {
        // ERROR 8: llamada a saludar con argumentos incorrectos (requiere 1, se pasan 0)
        saludar();
        return 0;
    }
};
