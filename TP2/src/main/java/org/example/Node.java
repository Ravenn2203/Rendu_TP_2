package org.example;

public class Node {

    public String classe;
    public String methode;

    public Node(String classe, String methode) {
        this.classe = classe;
        this.methode = methode;
    }

    public String getClasse() {
        return classe;
    }

    public void setClasse(String classe) {
        this.classe = classe;
    }

    public String getMethode() {
        return methode;
    }

    public void setMethode(String methode) {
        this.methode = methode;
    }

    @Override
    public String toString() {
        return "Node{" +
                "classe='" + classe + '\'' +
                ", methode='" + methode + '\'' +
                '}';
    }

}
