package org.example;

public class ClasseH {

    public ClasseG g = new ClasseG();

    public void methodeDansH(){
        System.out.println("Méthode dans H");
        g.methodeDansG();
    }
}
