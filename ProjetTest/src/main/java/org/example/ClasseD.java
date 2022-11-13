package org.example;

public class ClasseD {

    public String d;

    public ClasseD(String d) {
        this.d = d;
    }

    public String getD() {
        return d;
    }

    public void setD(String d) {
        this.d = d;
    }

    @Override
    public String toString() {
        return "ClasseD{" +
                "d='" + d + '\'' +
                '}';
    }

    public void methodeDansD(){
        System.out.println("Méthode dans D");
    }
}
