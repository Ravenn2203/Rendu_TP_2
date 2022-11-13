package org.example;

public class ClasseC {

    public String c;
    public ClasseD d;

    public ClasseC(String c) {
        this.c = c;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    @Override
    public String toString() {
        return "ClasseC{" +
                "c='" + c + '\'' +
                '}';
    }

    public void methodeDansC(){
        System.out.println("Méthode dans C");
        d.methodeDansD();
        d.methodeDansD();
        d.methodeDansD();
        d.methodeDansD();
        d.methodeDansD();
        d.methodeDansD();
        d.methodeDansD();
        d.methodeDansD();
        d.methodeDansD();
        d.methodeDansD();
        d.methodeDansD();
        d.methodeDansD();
        d.methodeDansD();
        d.methodeDansD();
    }
}
