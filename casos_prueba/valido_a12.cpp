/*---------------------------
| SEGURA VALENCIA FRANCISCO |
---------------------------*/

#include <iostream>
using namespace std;

/* Clase que demuestra los cuatro patrones de optimizacion */
class Optimizacion {
public:
    /* Metodo con subexpresion comun, variable no usada y asignacion redundante */
    int calcular(int a, int b) {

        /* PATRON 1 — Subexpresion comun: a + b se calcula dos veces */
        int x = a + b;
        int y = a + b;   /* mismo calculo: t_y = a + b duplica t_x */

        /* PATRON 2 — Variable no usada: noUsada asignada, nunca leida */
        int noUsada = 50;

        /* PATRON 3 — Asignacion redundante: resultado = 0 sobreescrito */
        int resultado = 0;
        resultado = x + y;

        return resultado;
    }
};

class Programa {
public:
    int main() {
        cout << "Segura Valencia Francisco";

        Optimizacion op = new Optimizacion();

        int a = 3;
        int b = 4;
        int res = op.calcular(a, b);

        /* PATRON 4 — Codigo inalcanzable: instruccion tras break */
        int val = 1;
        switch (val) {
            case 1:
                val = 100;
                break;
                val = 999;   /* codigo muerto — nunca se ejecuta */
            case 2:
                val = 200;
                break;
            default:
                val = 0;
        }

        cout << "Segura Valencia Francisco";
        return 0;
    }
};
