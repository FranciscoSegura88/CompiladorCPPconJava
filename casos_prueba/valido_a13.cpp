/*---------------------------
| SEGURA VALENCIA FRANCISCO |
---------------------------*/

#include <iostream>
using namespace std;

/* Clase con operaciones aritméticas para demostrar generación de código */
class Matematicas {
public:
    /* Multiplica dos enteros */
    int multiplicar(int a, int b) {
        int producto = a * b;
        return producto;
    }

    /* Divide dos enteros */
    int dividir(int a, int b) {
        int cociente = a / b;
        return cociente;
    }
};

class Programa {
public:
    int main() {
        cout << "Segura Valencia Francisco";

        Matematicas mat = new Matematicas();

        int x = 6;
        int y = 3;

        int prod = mat.multiplicar(x, y);
        int coc  = mat.dividir(x, y);
        int suma = prod + coc;

        /* Switch que demuestra saltos condicionales en el código generado */
        int op = 1;
        switch (op) {
            case 1:
                suma = suma * 2;
                break;
            case 2:
                suma = suma + 10;
                break;
            default:
                suma = 0;
        }

        cout << "Segura Valencia Francisco";
        return 0;
    }
};
