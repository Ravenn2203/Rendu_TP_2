package org.example;

public class ClasseA {

    public String a;
    public ClasseB B = new ClasseB("classeB");
    public ClasseC C = new ClasseC("classeC");
    public ClasseD D = new ClasseD("classeD");
    public ClasseE E = new ClasseE("classeE");

    public ClasseA() {
    }

    public ClasseA(String a) {
        this.a = a;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    @Override
    public String toString() {
        return "ClasseA{" +
                "a='" + a + '\'' +
                '}';
    }

    public void methodeDansA(){
        System.out.println("Méthode dans A");
    }

    public void methodeDansAQuiAppelleToutesLesAutresClasses(){
        System.out.println("Méthode dans A");
        B.methodeDansB();
        B.methodeDansB();
        C.methodeDansC();
        C.methodeDansC();
        D.methodeDansD();
        D.methodeDansD();
        E.methodeDansE();
        E.methodeDansE();
    }

    public void methodeDansAQuiAppelleMethodeB5Fois(){
        B.methodeDansB();
        B.methodeDansB();
        B.methodeDansB();
        B.methodeDansB();
        B.methodeDansB();
    }
}
