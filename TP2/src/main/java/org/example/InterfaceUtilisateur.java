package org.example;

import java.util.Scanner;

public class InterfaceUtilisateur {

    public void interfaceUtilisateur() {
    }

    private Scanner scanner;
    private int choixExercice;

    public void printCLI() {

        System.out.println("\nQuel exercice voulez vous exécuter ?");
        System.out.println("Tapez 1 pour l'exercice 1");
        System.out.println("Tapez 2 pour l'exercice 2");
        System.out.println("Tapez 3 pour quitter");
        scanner = new Scanner(System.in);
        choixExercice = scanner.nextInt();
        while (choixExercice != 1 && choixExercice != 2 && choixExercice != 3) {
            System.out.println("Veuillez uniquement choisir entre 1, 2 ou 3");
            choixExercice = scanner.nextInt();
        }

    }

    public int getChoixExercice() {
        return choixExercice;
    }

    public void setChoixExercice(int choixExercice) {
        this.choixExercice = choixExercice;
    }
}