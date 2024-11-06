package umontpellier.erl.calculs.exercice1;

import java.io.*;
import java.util.Map;

public class CouplingMetricMain {

    public static void main(String[] args) {
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
        String projectPath;

        try {
            if (args.length < 1) {
                projectPath = demanderCheminProjet(inputReader);
            } else {
                projectPath = verifierCheminProjet(inputReader, args[0]);
            }
            CouplingMetric couplingMetric = new CouplingMetric(projectPath);

            // Afficher le menu et demander le choix de l'utilisateur
            System.out.println("Choisissez une option :");
            System.out.println("1. Calculer le couplage entre deux classes");
            System.out.println("2. Calculer le couplage total de l'application");
            int choix = Integer.parseInt(inputReader.readLine().trim());

            if (choix == 1) {
                // Demander les noms des classes à analyser
                System.out.println("Entrez le nom de la première classe (par défaut: umontpellier.erl.calculs.dump.Dump1): ");
                String classA = inputReader.readLine().trim();
                if (classA.isEmpty()) {
                    classA = "umontpellier.erl.calculs.dump.Dump1";
                }

                System.out.println("Entrez le nom de la deuxième classe (par défaut: umontpellier.erl.calculs.dump.Dump2): ");
                String classB = inputReader.readLine().trim();
                if (classB.isEmpty()) {
                    classB = "umontpellier.erl.calculs.dump.Dump2";
                }

                // Vérifier si les classes sont valides et récupérer les noms complètement qualifiés
                String qualifiedClassA = getQualifiedClassName(couplingMetric, classA);
                String qualifiedClassB = getQualifiedClassName(couplingMetric, classB);

                if (qualifiedClassA == null || qualifiedClassB == null) {
                    System.err.println("Une ou les deux classes spécifiées ne sont pas valides.");
                    return;
                }

                double coupling = couplingMetric.calculateCoupling(qualifiedClassA, qualifiedClassB);
                int relationsAB = couplingMetric.getRelationsBetweenClasses(qualifiedClassA, qualifiedClassB);
                int relationsBA = couplingMetric.getRelationsBetweenClasses(qualifiedClassB, qualifiedClassA);

                System.out.println("Nombre de relations de " + qualifiedClassA + " vers " + qualifiedClassB + " : " + relationsAB);
                System.out.println("Nombre de relations de " + qualifiedClassB + " vers " + qualifiedClassA + " : " + relationsBA);
                System.out.println("Nombre total de relations entre " + qualifiedClassA + " et " + qualifiedClassB + " : " + (relationsAB + relationsBA));
                System.out.println("Couplage entre " + qualifiedClassA + " et " + qualifiedClassB + " : " + coupling);

            } else if (choix == 2) {
                System.out.println("Liste des invocations :");
                System.out.println(formatInvocations(couplingMetric.getInvocations()));

                // Calculer la somme des couplages de l'application
                Map<String, Map<String, Double>> classCoupling = couplingMetric.getClassCoupling();
                double totalCouplingSum = 0.0;
                for (Map<String, Double> dependencies : classCoupling.values()) {
                    for (double value : dependencies.values()) {
                        totalCouplingSum += value;
                    }
                }
                System.out.println("\n---\n");

                // Afficher le graphe en format DOT
                GraphGenerator graphGenerator = new GraphGenerator();
                String dotGraph = graphGenerator.generateDotGraph(classCoupling);

                // Enregistre le graphe en format DOT dans un fichier
                graphGenerator.writeDotFile(dotGraph, "coupling_graph.dot");
                System.out.println("Graphe enregistré dans le fichier coupling_graph.dot");

                // Calculer le nombre de relation de l'application
                int totalRelations = couplingMetric.getTotalRelationsBetweenClasses();

                System.out.println("Nombre total de relations : " + totalRelations);
            } else {
                System.err.println("Choix invalide.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String demanderCheminProjet(BufferedReader inputReader) throws IOException {
        System.out.println("Veuillez fournir le chemin vers le dossier src/ d'un projet Java : (laissez vide pour utiliser le répertoire actuel)");
        String cheminProjet = inputReader.readLine();
        if (cheminProjet.isEmpty()) {
            cheminProjet = System.getProperty("user.dir") + "/src/";
            System.out.println("Chemin non fourni. Utilisation du répertoire actuel : " + cheminProjet);
        }
        if (!cheminProjet.endsWith("/")) {
            cheminProjet += "/";
        }
        File dossierProjet = new File(cheminProjet);

        while (!dossierProjet.exists() || !cheminProjet.endsWith("src/")) {
            System.err.println("Erreur : " + cheminProjet + " n'existe pas ou n'est pas un dossier src/ d'un projet Java. Veuillez réessayer : ");
            cheminProjet = inputReader.readLine();
            if (cheminProjet.isEmpty()) {
                cheminProjet = System.getProperty("user.dir") + "/src/";
            }
            dossierProjet = new File(cheminProjet);
        }

        return cheminProjet;
    }

    private static String verifierCheminProjet(BufferedReader inputReader, String cheminUtilisateur) throws IOException {
        if (cheminUtilisateur.isEmpty()) {
            cheminUtilisateur = System.getProperty("user.dir") + "/src/";
        }
        File dossierProjet = new File(cheminUtilisateur);

        while (!dossierProjet.exists() || !cheminUtilisateur.endsWith("src/")) {
            System.err.println("Erreur : " + cheminUtilisateur + " n'existe pas ou n'est pas un dossier src/ d'un projet Java. Veuillez réessayer : ");
            cheminUtilisateur = inputReader.readLine();
            if (cheminUtilisateur.isEmpty()) {
                cheminUtilisateur = System.getProperty("user.dir") + "/src/";
            }
            dossierProjet = new File(cheminUtilisateur);
        }

        return cheminUtilisateur;
    }

    private static String formatInvocations(Map<String, Map<String, Integer>> invocations) {
        StringBuilder formatted = new StringBuilder();
        for (Map.Entry<String, Map<String, Integer>> entry : invocations.entrySet()) {
            formatted.append(entry.getKey()).append(":\n");
            for (Map.Entry<String, Integer> subEntry : entry.getValue().entrySet()) {
                formatted.append("  ").append(subEntry.getKey()).append(" -> ").append(subEntry.getValue()).append("\n");
            }
        }
        return formatted.toString();
    }

    private static String getQualifiedClassName(CouplingMetric couplingMetric, String className) {
        for (String method : couplingMetric.getInvocations().keySet()) {
            String qualifiedClassName = method.split("::")[0];
            if (qualifiedClassName.endsWith(className)) {
                return qualifiedClassName;
            }
        }
        return null;
    }
}