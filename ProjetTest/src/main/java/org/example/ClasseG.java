package org.example;

public class ClasseG {

    public ClasseF f = new ClasseF();

    public void methodeDansG(){
        System.out.println("Méthode dans G");
        f.methodeDansF();
        f.methodeDansF();
    }

}
