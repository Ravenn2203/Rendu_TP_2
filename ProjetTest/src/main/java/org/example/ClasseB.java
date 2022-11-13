package org.example;

public class ClasseB {

    public String b;
    public ClasseA A = new ClasseA("classeA");

    public ClasseB(String b) {
        this.b = b;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }

    @Override
    public String toString() {
        return "ClasseB{" +
                "b='" + b + '\'' +
                '}';
    }

    public void methodeDansB(){
        System.out.println("Méthode dans B");
    }

    public void methodeDansAQuiAppelleMethodeB5Fois(){
        A.methodeDansA();
        A.methodeDansA();
        A.methodeDansA();
        A.methodeDansA();
        A.methodeDansA();
    }
}
