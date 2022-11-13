package org.example;

public class Main {

    public static void main(String[] args) {
        ClasseA A = new ClasseA("classeA");
        A.methodeDansAQuiAppelleToutesLesAutresClasses();
        A.methodeDansAQuiAppelleToutesLesAutresClasses();
        A.methodeDansAQuiAppelleToutesLesAutresClasses();
        ClasseB B = new ClasseB("classeB");
        B.methodeDansB();
        ClasseC C = new ClasseC("classeC");
        C.methodeDansC();
        ClasseD D = new ClasseD("classeD");
        D.methodeDansD();
        ClasseE E = new ClasseE("classeE");
        E.methodeDansE();
    }

}