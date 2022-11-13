package org.example;

public class ClasseE {

    public String e;

    public ClasseE(String e) {
        this.e = e;
    }

    public String getE() {
        return e;
    }

    public void setE(String e) {
        this.e = e;
    }

    @Override
    public String toString() {
        return "ClasseE{" +
                "e='" + e + '\'' +
                '}';
    }

    public void methodeDansE(){
        System.out.println("Méthode dans E");
    }
}
