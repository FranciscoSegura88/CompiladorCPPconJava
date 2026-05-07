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

    bool esMayor(int x, int y) {
        bool cmp = x > y;
        return cmp;
    }
};

class Programa {
public:
    int main() {
        cout << "Segura Valencia Francisco";

        Calculadora calc = new Calculadora();

        /* Error 1: variable no declarada usada en expresion */
        int r = noDeclarada + 5;

        /* Error 2: tipo incompatible — bool asignado a int */
        int x = 10;
        int y = 20;
        bool condicion = x > y;
        int err = condicion + x;   /* bool + int: tipo incompatible */

        /* Error 3: reasignacion de constante */
        const int MAX = 100;
        MAX = 200;

        /* Error 4: variable no declarada en switch */
        switch (fantasma) {
            case 1:
                int a = 1;
                break;
            default:
                int b = 2;
        }

        /* Error 5: redeclaracion dentro del mismo ambito */
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
