package org.example;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.internal.core.SourceType;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.dot.DOTExporter;

import java.io.*;
import java.util.*;

import static java.lang.System.exit;

public class Main {

    public static String projectPath = "/home/garcialea/Bureau/EvolutionRestructurationDesLogiciels/Partie_2/ProjetTest";
    public static final String projectSourcePath = projectPath + "/src";
    public static String jrePath = "/usr/lib/jvm/java-1.11.0-openjdk-amd64";
    public static InterfaceUtilisateur interfaceUtilisateur = new InterfaceUtilisateur();
    public static CompilationUnit parse = null;
    //Ce visiteur me permet dans toutes les méthodes du projet d'analyse, de parcourir les fichiers parsés et de récupère les valeurs dont j'ai besoin
    public static TypeDeclarationVisitor visiteurClasse;

    public static void main(String[] args) throws IOException {

        //Mets dans une ArrayList javaFiles, l'ensemble des fichiers java d'un dossier
        final File folder = new File(projectSourcePath);
        ArrayList<File> javaFiles = listJavaFilesForFolder(folder);

        visiteurClasse = new TypeDeclarationVisitor();
        CompilationUnit parse = null;

        //Lecture puis parsage des fichiers
        for (File fileEntry : javaFiles) {
            String content = FileUtils.readFileToString(fileEntry);
            parse = parse(content.toCharArray());
            //J'ai décidé d'accepter un visiteur sur tous les fichiers de l'application et de l'utiliser en repartant de zéro à chaque fois
            parse.accept(visiteurClasse);
        }

        /*System.out.println("Veuillez entrer le chemin vers le projet 'ProjetTest' (projectPath) : ");
        Scanner scanner = new Scanner(System.in);
        projectPath = scanner.nextLine();

        System.out.println("Veuillez entrer le chemin vers votre JRE (jrePath) : ");
        jrePath = scanner.nextLine();
         */

        while (interfaceUtilisateur.getChoixExercice() != 4) {
            interfaceUtilisateur.printCLI();
            if (interfaceUtilisateur.getChoixExercice() == 1) {

                //Exercice 1 Question 1
                float nbCouplagesEntreDeuxClasses = couplageDeuxClasses("ClasseA", "ClasseB");
                System.out.println("____________________________________________________________________________");
                System.out.println("Exercice 1 Question 1");
                System.out.println("Nombre d'appels dans toute l'application : " + nbAppelsApplication());

                System.out.println("\nMétrique de couplage entre les deux classes 'ClasseA' et 'ClasseB' : " + nbCouplagesEntreDeuxClasses);
                System.out.println();

                //Exercice 1 Question 2
                System.out.println("____________________________________________________________________________");
                System.out.println("Exercice 1 Question 1");
                construireGraphePondere();
                System.out.println("Le graphe de couplage pondéré entre les classes de l'application donnée à analyser est prêt");
                System.out.println("Vous pouvez le visualiser dans le dossier example à la racine du projet 'TP2'\n");
                System.out.println("____________________________________________________________________________");

            } else if (interfaceUtilisateur.getChoixExercice() == 2) {

                //Exercice 2 Question 1
                System.out.println("____________________________________________________________________________");
                System.out.println("Exercice 2 Question 1");
                ArrayList<String> dendogramme = construireDendogrammeGraphe();
                System.out.println();

                //Exercice 2 Question 2
                System.out.println("____________________________________________________________________________");
                System.out.println("Exercice 2 Question 2");
                identificationModules(dendogramme);
                System.out.println();
                System.out.println("____________________________________________________________________________");

            } else if (interfaceUtilisateur.getChoixExercice() == 4) {
                exit(1);
            }
        }

    }

    //Méthode qui permet de lire tous les fichiers java contenu dans le dossier (folder)
    public static ArrayList<File> listJavaFilesForFolder(final File folder) {
        ArrayList<File> javaFiles = new ArrayList<File>();
        for (File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                javaFiles.addAll(listJavaFilesForFolder(fileEntry));
            } else if (fileEntry.getName().contains(".java")) {
                javaFiles.add(fileEntry);
            }
        }
        return javaFiles;
    }

    //Méthode qui crée notre AST
    private static CompilationUnit parse(char[] classSource) {

        ASTParser parser = ASTParser.newParser(AST.JLS4); // java +1.6
        parser.setResolveBindings(true);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setBindingsRecovery(true);

        Map options = JavaCore.getOptions();
        parser.setCompilerOptions(options);
        parser.setUnitName("");

        String[] sources = {projectSourcePath};
        String[] classpath = {jrePath};

        parser.setEnvironment(classpath, sources, new String[]{"UTF-8"}, true);
        parser.setSource(classSource);

        return (CompilationUnit) parser.createAST(null);
    }

    //Méthode qui compte le nombre d'appels de méthodes dans toute l'application
    private static int nbAppelsApplication() {
        int nbAppels = 0;
        for (TypeDeclaration type : visiteurClasse.getTypes()) {
            for (MethodDeclaration methode : type.getMethods()) {
                MethodInvocationVisitor visiteurMethodInvocation = new MethodInvocationVisitor();
                methode.accept(visiteurMethodInvocation);
                nbAppels += visiteurMethodInvocation.getMethods().size();
            }
        }
        return nbAppels;
    }

    //Méthode qui calcule le couplage entre deux classes passées entre argument via des String
    private static float couplageDeuxClasses(String classeA, String classeB) {

        int nbCouplage = 0;
        int nbAppelsApplication = nbAppelsApplication();

        for (TypeDeclaration type : visiteurClasse.getTypes()) {
            for (MethodDeclaration methode : type.getMethods()) {

                MethodInvocationVisitor visiteurMethodInvocation = new MethodInvocationVisitor();
                //J'accepte un visiteur sur ma méthode qui est une classe MethodDeclaration et je récupère tous les appels de méthodes qu'il y a à l'intérieur de cette classe
                methode.accept(visiteurMethodInvocation);

                if (visiteurMethodInvocation.getMethods().size() != 0) {
                    for (MethodInvocation methodeInv : visiteurMethodInvocation.getMethods()) {
                        if (methodeInv.getExpression() != null && methodeInv.getExpression().resolveTypeBinding() != null) {
                            if ((type.getName().toString().equals(classeA) && methodeInv.getExpression().resolveTypeBinding().getName().equals(classeB)) || (type.getName().toString().equals(classeB) && methodeInv.getExpression().resolveTypeBinding().getName().equals(classeA))) {
                                nbCouplage++;
                            }
                        }
                    }
                }
            }

        }

        //On divise par le nombre d'appels de l'application entière
        return (float) nbCouplage / nbAppelsApplication;
    }

    //Méthode qui construit le graphe de couplage pondéré
    private static void construireGraphePondere() throws IOException {

        //Déclaration de mon graphe JGraphT
        SimpleWeightedGraph<String, DefaultWeightedEdge> graphe = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
        float couplage = 0;

        for (TypeDeclaration type : visiteurClasse.getTypes()) {
            for (TypeDeclaration type2 : visiteurClasse.getTypes()) {

                //Je teste que les deux classes sélectionnées ne soient pas une seule et même classe
                if (!type.getName().equals(type2.getName())) {
                    //J'ajoute à mon graphe des sommets pour les classes qui viennent d'être sélectionnées dans les for (s'ils n'existent pas déjà)
                    String noeud1 = type.getName().toString();
                    String noeud2 = type2.getName().toString();
                    if (!graphe.containsVertex(noeud1)) {
                        graphe.addVertex(noeud1);
                    }
                    if (!graphe.containsVertex(noeud2)) {
                        graphe.addVertex(noeud2);
                    }

                    //Si mon graphe ne contient pas déjà l'arête, je l'ajoute
                    if (!graphe.containsEdge(noeud1, noeud2)) {
                        couplage = couplageDeuxClasses(type.getName().toString(), type2.getName().toString());
                        //Le couplage est dans les deux sens, on regarde les appels de méthodes d'une classe vers l'autre et inversement
                        //Donc pas besoin de repasser sur l'arête les deux fois, une fois suffit, la méthode renvoie le couplage
                        DefaultWeightedEdge edge = graphe.addEdge(noeud1, noeud2);
                        graphe.setEdgeWeight(edge, couplage);
                    }
                }
            }
        }
        exportGraphEnDOT(graphe);
    }

    //Méthode qui exporte le graphe JgraphT vers un format .dot que l'on transforme ensuite en image .png
    private static void exportGraphEnDOT(SimpleWeightedGraph<String, DefaultWeightedEdge> graphe) throws IOException {

        DOTExporter<String, DefaultWeightedEdge> exporter = new DOTExporter<String, DefaultWeightedEdge>();
        //Ce code permet de rajouter le nom des sommets ('v' c'est un String)
        exporter.setVertexAttributeProvider((v) -> {
            Map<String, Attribute> map = new LinkedHashMap<String, Attribute>();
            map.put("label", DefaultAttribute.createAttribute(v));
            return map;
        });

        //Ce code permet de rajouter les poids sur les arêtes ('e' est une DefaultWeightedEdge donc je récupère d'abord son poids et je l'ajoute ensuite
        exporter.setEdgeAttributeProvider(e -> {
            //Je récupère le poids de chaque arête e
            float weight = (float) graphe.getEdgeWeight(e);
            Map<String, Attribute> map = new HashMap<>();
            map.put("label", DefaultAttribute.createAttribute(weight));
            return map;
        });


        Writer writer = new StringWriter();
        exporter.exportGraph(graphe, writer);
        MutableGraph g = new guru.nidi.graphviz.parse.Parser().read(writer.toString());
        Graphviz.fromGraph(g).height(1000).render(Format.PNG).toFile(new File("example/grapheAppelsPondere.png"));

    }

    //Méthode représentant un algorithme de regroupement hiérarchique de classes selon la valeur de leur couplage
    private static ArrayList<String> construireDendogrammeGraphe() throws IOException {

        //Je déclare une arrayList de arrayList
        ArrayList<ArrayList<String>> listeClusters = new ArrayList<>();

        ArrayList<String> dendogramme = new ArrayList<>();
        ArrayList<String> dendogrammeDetails = new ArrayList<>();

        for (TypeDeclaration type : visiteurClasse.getTypes()) {
            ArrayList<String> unCluster = new ArrayList<>();
            //J'ajoute dans une arrayList, la classe qui vient d'être sélectionnnée dans le for
            unCluster.add(type.getName().toString());
            //J'ajoute dans ma arrayList d'arrayList, l'arrayList contenant la classe qui vient d'être sélectionnée dans le for
            listeClusters.add(unCluster);
        }

        double maxCouplageEntreTout = 0;
        double maxCouplageAvecDeuxiemeListe = 0;
        double couplage = 0;
        double nbCouplagesTestes = 0;
        ArrayList<ArrayList<String>> listemaxCouplageAvecDeuxiemeListe = new ArrayList<>();
        ArrayList<ArrayList<String>> listemaxCouplageEntreTout = new ArrayList<>();

        //Tant que ma liste de cluster n'est pas composée d'un seul élément
        while (listeClusters.size() != 1) {

            //Je remets à 0 mes 2 variables
            maxCouplageAvecDeuxiemeListe = 0;
            maxCouplageEntreTout = 0;

            //Je parcours ma liste de clusters
            //Je veux trouver le meilleur cluster à faire (meilleur couplage ou couplage moyen)
            for (ArrayList<String> firstList : listeClusters) {

                //Je parcours une deuxième fois ma liste pour faire les correspondances
                for (ArrayList<String> secondList : listeClusters) {
                    if (firstList.size() == 0 || secondList.size() == 0) {
                        System.out.println("Erreur, la liste sélectionnée est vide !");

                        //Quand ce sont les deux mêmes clusters, je saute car ils ne peuvent pas être mis en correspondance avec eux mêmes
                    } else if (!firstList.equals(secondList)) {

                        couplage = 0;
                        nbCouplagesTestes = 0;
                        //J'ai ma liste firstList qui contient 1 à n classes (représentées par des String)
                        //J'ai ma liste secondList qui contient 1 à n classes (représentées par des String)
                        //Je récupère tous les couplages possibles entre les deux listes ainsi que le nombre total de couplages possibles
                        for (String elementFirstList : firstList) {
                            for (String elementSecondList : secondList) {
                                couplage += couplageDeuxClasses(elementFirstList, elementSecondList);
                                nbCouplagesTestes++;
                            }
                        }

                        //Si le couplage est meilleur que tous ceux trouvés avant alors je remplace dans la variable maxCouplage
                        //Je divise par le nombre de couplages testés pour faire une moyenne
                        if (couplage / nbCouplagesTestes >= maxCouplageAvecDeuxiemeListe) {
                            maxCouplageAvecDeuxiemeListe = couplage / nbCouplagesTestes;
                            //Je vide ma liste du meilleur couplage, j'ai trouvé mieux
                            listemaxCouplageAvecDeuxiemeListe = new ArrayList<>();
                            //J'ajoute à ma liste qui détermine quel est le meilleur couplage pour le cluster sélectionné dans le premier for
                            listemaxCouplageAvecDeuxiemeListe.add(firstList);
                            listemaxCouplageAvecDeuxiemeListe.add(secondList);
                        }
                    }

                }
                if (maxCouplageAvecDeuxiemeListe >= maxCouplageEntreTout) {
                    maxCouplageEntreTout = maxCouplageAvecDeuxiemeListe;
                    //Je vide ma liste du meilleur couplage entre tous, j'ai trouvé mieux
                    listemaxCouplageEntreTout = new ArrayList<>();
                    //J'ajoute à ma liste qui détermine quel est le meilleur couplage parmi tous les couplages possibles
                    listemaxCouplageEntreTout.add(listemaxCouplageAvecDeuxiemeListe.get(0));
                    listemaxCouplageEntreTout.add(listemaxCouplageAvecDeuxiemeListe.get(1));
                }
            }

            System.out.println("\nLe meilleur couplage trouvé est entre : " + listemaxCouplageEntreTout.get(0) + " et " + listemaxCouplageAvecDeuxiemeListe.get(1));

            //Ici j'ai obtenu la liste des clusters à clusteuriser grace à la variable listemaxCouplageEntreTout
            //Je supprime les clusters qui vont être rassemblés
            listeClusters.remove(listemaxCouplageEntreTout.get(0));
            listeClusters.remove(listemaxCouplageEntreTout.get(1));

            //Je fais une nouvelle liste pour mon cluster que je vais ajouter à la liste de mes clusters
            ArrayList<String> listeClusteurisation = new ArrayList<>();
            listeClusteurisation.addAll(listemaxCouplageEntreTout.get(0));
            listeClusteurisation.addAll(listemaxCouplageEntreTout.get(1));
            listeClusters.add(listeClusteurisation);
            dendogrammeDetails.add(String.valueOf(listemaxCouplageEntreTout.get(0)));
            dendogrammeDetails.add(String.valueOf(listemaxCouplageEntreTout.get(1)));
            dendogrammeDetails.add(String.valueOf(listeClusteurisation));
            dendogramme.add(String.valueOf(listeClusteurisation));
            System.out.println("Mon nouvel ensemble de clusters : " + listeClusters);

        }

        System.out.println("\n\nLe dendogramme est créé !");
        System.out.println("Voici les clusters créés à chaque tour par notre algorithme : ");
        System.out.println("(La différence entre une liste et la liste à sa droite, c'est l'ajout en fin de liste, de la classe ou l'ensemble de classes que l'on vient de clusteuriser à elle)");
        System.out.println(dendogramme);
        return dendogrammeDetails;

            /* Finalement je vais rester sur une liste simple, trop dur de faire un graphe
            //A la fin de mon algorithme on ne voit rien car toutes les classes sont rassemblées dans un cluster
            //J'ai donc décidé de le représenter sous forme de graphe
            if(listemaxCouplageEntreTout.get(0).size()>1 && listemaxCouplageEntreTout.get(1).size()==1){
                //Je crée un sommet pour le deuxième uniquement
                graphe.addVertex(listemaxCouplageEntreTout.get(1).toString());
                //Je recup le nom de mon sommet
                StringBuilder nomSommet = new StringBuilder();
                for(String nom : listemaxCouplageEntreTout.get(0)){
                    nomSommet.append("["+nom.toString()+"]");
                }

                //Je crée un nouveau sommet
                graphe.addVertex(nomSommet+listemaxCouplageEntreTout.get(1).toString());
                //J'ajoute les arêtes vers le nouveau sommet intermédiaire
                graphe.addEdge(String.valueOf(nomSommet), nomSommet+listemaxCouplageEntreTout.get(1).toString());
                graphe.addEdge(listemaxCouplageEntreTout.get(1).toString(), nomSommet+listemaxCouplageEntreTout.get(1).toString());

            }else if (listemaxCouplageEntreTout.get(0).size()==1 && listemaxCouplageEntreTout.get(1).size()>1) {

                //Je crée un sommet pour le deuxième uniquement
                graphe.addVertex(listemaxCouplageEntreTout.get(0).toString());
                //Je recup le nom de mon sommet
                StringBuilder nomSommet = new StringBuilder();
                for(String nom : listemaxCouplageEntreTout.get(1)){
                    nomSommet.append("["+nom.toString()+"]");
                }

                //Je crée un nouveau sommet
                graphe.addVertex(nomSommet+listemaxCouplageEntreTout.get(0).toString());
                //J'ajoute les arêtes vers le nouveau sommet intermédiaire
                graphe.addEdge(String.valueOf(nomSommet), nomSommet+listemaxCouplageEntreTout.get(0).toString());
                graphe.addEdge(listemaxCouplageEntreTout.get(1).toString(), nomSommet+listemaxCouplageEntreTout.get(0).toString());

            }else {
                graphe.addVertex(listemaxCouplageEntreTout.get(0).toString());
                graphe.addVertex(listemaxCouplageEntreTout.get(1).toString());
                graphe.addVertex(listemaxCouplageEntreTout.get(0).toString() + listemaxCouplageEntreTout.get(1).toString());
                graphe.addEdge(listemaxCouplageEntreTout.get(0).toString() + listemaxCouplageEntreTout.get(1).toString(), listemaxCouplageEntreTout.get(0).toString());
                graphe.addEdge(listemaxCouplageEntreTout.get(0).toString() + listemaxCouplageEntreTout.get(1).toString(), listemaxCouplageEntreTout.get(1).toString());
            }
             exportGraphEnDOT(graphe, false);
             */

    }

    //Méthode représentant un algorithme de détection de groupes de classes couplées avec comme conditions
    //L'application que l'on utilise donc être divisée en maximum M/2 groupes avec M = nombre de classes de l'application
    //Chaque groupe doit contenir uniquement les classes d'une seule branche du dendogramme <=> chaque classe n'est que dans un seul groupe
    //La moyenne du couplage de tous les couples de classes du groupe doit être supérieure à CP, CP paramètre donné par l'utilisateur
    private static void identificationModules(ArrayList<String> dendogramme) {

        System.out.println("\nMon dendogramme détaillé (pour la coupe) :");
        System.out.println(dendogramme);


        System.out.println("Veuillez entrer une valeur pour CP :");
        System.out.println("Je vous conseille de choisir la valeur 0,02");
        Scanner scanner = new Scanner(System.in);
        //Les trois contraintes
        double nbMinMoyenneCouplage = scanner.nextDouble();
        double nbMaxNbModules = (visiteurClasse.getTypes().size()) / 2.0;
        //Chaque classe n'est que dans un seul module
        boolean conditionX3 = true;
        int boucle = 0;

        //On coupe la première branche du dendogramme en prenant les deux derniers ensembles que l'on a assemblés
        ArrayList<String> listeModules = new ArrayList<>();
        listeModules.add(dendogramme.get(dendogramme.size() - 2));
        listeModules.add(dendogramme.get(dendogramme.size() - 3));

        //Pour trouver mes modules je dois faire une coupe dans le dendogramme
        while (conditionX3 && boucle<=(dendogramme.size()/3)-1) {

            boucle++;

            if (listeModules.size() <= nbMaxNbModules) {
                //TODO calcul couplage
                double moyennecouplage = 1;
                if (moyennecouplage >= nbMinMoyenneCouplage) {
                    //TODO Impossible de récupèrer ce que je veux je ne sais pas quelle branche couper j'aurais dû suivre mon idée de graphe
                   // listeModules = new ArrayList<>();
                   // listeModules.add(dendogramme.get(dendogramme.size() - (3 * boucle)));
                   // listeModules.add(dendogramme.get(dendogramme.size() - (3 * boucle) + 1));
                } else {
                    System.out.println("(Les conditions ne sont plus respectées on arrête de découper)");
                    System.out.println(listeModules);
                    conditionX3 = false;
                }
            } else {
                System.out.println("(Les conditions ne sont plus respectées on arrête de découper)");
                System.out.println(listeModules);
                conditionX3 = false;
            }
        }

        System.out.println("Voici les différents modules de votre projet :");
        System.out.println(listeModules);

    }
}