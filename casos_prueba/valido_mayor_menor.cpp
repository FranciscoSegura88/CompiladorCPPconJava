/*---------------------------
| SEGURA VALENCIA FRANCISCO |
---------------------------*/

#include <iostream>
using namespace std;

class OrdenTres {
public:
    int main() {
        cout << "Segura Valencia Francisco";

        int a = 7;
        int b = 2;
        int c = 5;

        int mayor = 0;
        int medio = 0;
        int menor = 0;

        if (a >= b && a >= c) {
            mayor = a;
            if (b >= c) {
                medio = b;
                menor = c;
            } else {
                medio = c;
                menor = b;
            }
        } else {
            if (b >= a && b >= c) {
                mayor = b;
                if (a >= c) {
                    medio = a;
                    menor = c;
                } else {
                    medio = c;
                    menor = a;
                }
            } else {
                mayor = c;
                if (a >= b) {
                    medio = a;
                    menor = b;
                } else {
                    medio = b;
                    menor = a;
                }
            }
        }

        cout << mayor;
        cout << medio;
        cout << menor;

        return 0;
    }
};