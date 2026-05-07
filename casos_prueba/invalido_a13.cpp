/*---------------------------
| SEGURA VALENCIA FRANCISCO |
---------------------------*/

#include <iostream>
using namespace std;

class Calculadora {
public:
    int dividir(int a, int b) {
        int resultado = a / b;
        return resultado;
    }
};

class Programa {
public:
    int main() {
        cout << "Segura Valencia Francisco";

        /* Error 1: variable no declarada usada en expresion */
        int r = sinDeclarar + 5;

        /* Error 2: tipo incompatible — bool + int */
        bool flag = true;
        int x = flag + 10;

        /* Error 3: reasignacion de constante */
        const int LIMITE = 50;
        LIMITE = 100;

        /* Error 4: variable no declarada en condicion */
        int y = 20;
        if (noExiste > y) {
            y = 0;
        }

        cout << "Segura Valencia Francisco";
        return 0;
    }
};
