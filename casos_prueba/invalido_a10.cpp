/*---------------------------
| SEGURA VALENCIA FRANCISCO |
---------------------------*/

#include <iostream>
using namespace std;

class Calculadora {
public:
    int sumar(int a, int b) {
        int resultado = a + b;
        return resultado;
    }

    bool verificar(int x) {
        bool ok = x > 0;
        return ok;
    }
};

class Programa {
public:
    int main() {
        cout << "Segura Valencia Francisco";

        Calculadora calc = new Calculadora();

        /* Error 1: variable no declarada en for */
        for (i = 0; i < 5; i++) {
            int r = 1;
        }

        /* Error 2: tipo incompatible en asignacion dentro de for */
        bool flag = true;
        for (flag = 0; flag < 3; flag++) {
            int z = 1;
        }

        /* Error 3: asignar resultado bool a int sin compatibilidad */
        int x = 10;
        int y = 20;
        int suma = x + y;
        bool cond = x > y;
        int err = cond + suma;

        /* Error 4: variable no declarada en switch */
        switch (noExiste) {
            case 1:
                int a = 1;
                break;
            default:
                int b = 2;
        }

        /* Error 5: redeclaracion dentro de switch */
        int val = 5;
        switch (val) {
            case 1:
                int val = 99;
                break;
        }

        cout << "Segura Valencia Francisco";
        return 0;
    }
};
