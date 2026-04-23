#include <iostream>
using namespace std;

class Persona {

    public:
        int edad;
        float salario = 5000.0;
        const double PI = 3.1416;

    void registrar(int e, float s) {
        int contador = 0;
        double total;
        total = salario + 100.0;
        contador++;
        cout << contador;
    }

    int calcular(int a, int b) {
        int resultado = a + b;
        return resultado;
    }

};