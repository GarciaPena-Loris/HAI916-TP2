package umontpellier.erl.calculs.exercice2.part2;

import umontpellier.erl.calculs.exercice2.part1.ClassExtractor;
import umontpellier.erl.calculs.exercice2.part1.ClassNode;
import umontpellier.erl.calculs.exercice2.part1.ClusteringMain;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class ModuleMain extends ClusteringMain {

    public static void main(String[] args) {
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
        String projectPath;
        double CP;

        try {
            if (args.length < 1) {
                projectPath = demanderCheminProjet(inputReader);
            } else {
                projectPath = verifierCheminProjet(inputReader, args[0]);
            }

            // Demander la valeur de CP
            CP = demanderCP(inputReader);

            ClassExtractor classExtractor = new ClassExtractor(projectPath);
            try {
                // Extraction des classes
                List<ClassNode> classes = classExtractor.extractClasses();
                System.out.println("Nombre de classe dans l'application : " + classes.size() + "\n");

                ModuleAlgorithm moduleAlgorithm = new ModuleAlgorithm(projectPath, CP);
                moduleAlgorithm.printHierarchicalClustering(classes);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static double demanderCP(BufferedReader inputReader) throws IOException {
        System.out.print("Veuillez entrer la valeur du seuil de couplage moyen (CP) : ");
        String cpInput = inputReader.readLine();
        double CP;
        try {
            CP = Double.parseDouble(cpInput);
        } catch (NumberFormatException e) {
            System.out.println("Entrée invalide. Utilisation de la valeur par défaut 0.5 pour CP.");
            CP = 0.5;
        }
        return CP;
    }
}