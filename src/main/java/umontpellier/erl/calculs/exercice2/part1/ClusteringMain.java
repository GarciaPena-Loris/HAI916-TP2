package umontpellier.erl.calculs.exercice2.part1;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class ClusteringMain {

    public static void main(String[] args) {
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
        String projectPath;

        try {
            if (args.length < 1) {
                projectPath = demanderCheminProjet(inputReader);
            } else {
                projectPath = verifierCheminProjet(inputReader, args[0]);
            }

            ClassExtractor classExtractor = new ClassExtractor(projectPath);
            try {
                // Extraction des classes
                List<ClassNode> classes = classExtractor.extractClasses();
                System.out.println("Nombre de classe dans l'application : " + classes.size() + "\n");

                ClusteringAlgorithm clusteringAlgorithm = new ClusteringAlgorithm(projectPath);
                clusteringAlgorithm.printHierarchicalClustering(classes);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected static String demanderCheminProjet(BufferedReader inputReader) throws IOException {
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

    protected static String verifierCheminProjet(BufferedReader inputReader, String cheminUtilisateur) throws IOException {
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
}