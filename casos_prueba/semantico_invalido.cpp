/*
 * CASO INVALIDO - Análisis Semántico
 * Prueba: errores de tipo, redeclaración, no declarado, const reasignada
 *
 * Errores esperados:
 *   Sem: x redeclarado en clase
 *   Sem: tipo incompatible — int ← float   (z = y)
 *   Sem: tipo incompatible — float ← int   (b = a)
 *   Sem: constante MAX reasignada
 *   Sem: 'noExiste' no declarado
 *   Sem: variable 'local' usada antes de inicializar
 */
#include <iostream>
using namespace std;

class Prueba {

    public:
        int x;
        int x;
        const double MAX = 100.0;

    void tipos(int a, float b) {
        int z;
        float y = 3.5;
        z = y;
        b = a;
        MAX = 200.0;
    }

    void noDeclarado() {
        int local;
        cout << local;
        noExiste();
    }

};
