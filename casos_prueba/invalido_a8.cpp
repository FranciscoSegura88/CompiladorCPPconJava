/*---------------------------
| SEGURA VALENCIA FRANCISCO |
---------------------------*/

// Caso de prueba invalido — Practica A8: Errores de tipo en expresiones

#include <iostream>
using namespace std;

class EjemplosInvalidos {
public:

    // ERROR 1: suma de string con entero
    int errorSumaStringEntero(int n) {
        string texto = "hola";
        int r = n + texto;
        return r;
    }

    // ERROR 2: resta de bool con entero
    int errorRestaBoolEntero(bool b, int n) {
        int r = b - n;
        return r;
    }

    // ERROR 3: multiplicacion de string con string
    int errorMultString(string a, string b) {
        int r = a * b;
        return r;
    }

    // ERROR 4: division de bool entre entero
    int errorDivBool(bool b) {
        int r = b / 2;
        return r;
    }

    // ERROR 5: AND logico con operando entero (izquierdo)
    bool errorAndConEntero(int n, bool b) {
        bool r = n && b;
        return r;
    }

    // ERROR 6: OR logico con operando string (derecho)
    bool errorOrConString(bool b, string s) {
        bool r = b || s;
        return r;
    }

    // ERROR 7: NOT sobre entero
    bool errorNotEntero(int n) {
        bool r = !n;
        return r;
    }

    // ERROR 8: comparacion entre string y entero
    bool errorComparacionStringEntero(string s, int n) {
        bool r = s == n;
        return r;
    }

    // ERROR 9: comparacion ordenada sobre bool
    bool errorMenorBool(bool a, bool b) {
        bool r = a < b;
        return r;
    }

    // ERROR 10: comparacion ordenada sobre string
    bool errorMayorString(string a, string b) {
        bool r = a > b;
        return r;
    }

    // ERROR 11: negacion unaria sobre string
    int errorNegacionString(string s) {
        int r = -s;
        return r;
    }

    // ERROR 12: += con string
    void errorPlusAsigString(int n) {
        string texto = "mundo";
        n += texto;
    }

    // ERROR 13: -= con bool
    void errorMinusAsigBool(int n) {
        bool b = true;
        n -= b;
    }
};
