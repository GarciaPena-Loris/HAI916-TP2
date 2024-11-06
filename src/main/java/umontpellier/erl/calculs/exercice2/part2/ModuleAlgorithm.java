package umontpellier.erl.calculs.exercice2.part2;

import org.antlr.v4.runtime.misc.Pair;
import umontpellier.erl.calculs.exercice2.part1.ClassNode;
import umontpellier.erl.calculs.exercice2.part1.ClusterNode;
import umontpellier.erl.calculs.exercice2.part1.ClusteringAlgorithm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ModuleAlgorithm extends ClusteringAlgorithm {
    // Ajoute du seuil de couplage
    private final Double cp;

    public ModuleAlgorithm(String projectPath, double cp) throws IOException {
        super(projectPath);
        this.cp = cp;
    }

    // Calcul du couplage entre deux clusters
    public Pair<ClusterNode[], Double> closestClusters(List<ClusterNode> clusters) {
        ClusterNode[] closest = null;
        double maxCoupling = -1.0;

        for (int i = 0; i < clusters.size(); i++) {
            for (int j = i + 1; j < clusters.size(); j++) {
                double coupling = calculateCoupling(clusters.get(i), clusters.get(j));

                // Si le couplage est supérieur au seuil de couplage et au couplage maximal
                if (coupling > maxCoupling && coupling > cp) {
                    maxCoupling = coupling;
                    // On garde les clusters les plus proches
                    closest = new ClusterNode[]{clusters.get(i), clusters.get(j)};
                }
            }
        }
        // Retourne le couple de clusters les plus proches et leur couplage
        return closest == null ? null : new Pair<>(closest, maxCoupling);
    }

    // Calcul du couplage entre deux clusters
    public void printHierarchicalClustering(List<ClassNode> classes) {
        List<ClusterNode> clusters = new ArrayList<>(classes);
        int nbModules = 0;

        while (clusters.size() > 1) {
            Pair<ClusterNode[], Double> closest = closestClusters(clusters);

            // Si plus aucun couple de clusters n'a un couplage supérieur au seuil de couplage
            if (closest == null) {
                System.out.println("No more modules exist with coupling greater than CP = " + cp);
                // Construction du module final avec les clusters restants
                Module finalCluster = Module.buildModule(clusters);
                System.out.println("Final module: \n" + finalCluster);
                finalCluster.writeDotFile("dendrogram_module.dot");
                return;
            }
            ClusterNode c1 = closest.a[0];
            ClusterNode c2 = closest.a[1];
            Double couplingc1c2 = closest.b;

            Module c3;
            // Si plus de la moitié des classes sont dans le premier module, on le met en rouge
            if (nbModules > classes.size() / 2)
                c3 = new Module(c1, c2, couplingc1c2, "red");
            else
                c3 = new Module(c1, c2, couplingc1c2, "blue");

            clusters.remove(c1);
            clusters.remove(c2);
            clusters.add(c3);

            nbModules++;
        }

        // Si un seul module a été créé
        if (clusters.get(0) instanceof Module) {
            // On récupère le module final et on crée le fichier .dot
            Module finalModule = (Module) clusters.get(0);
            System.out.println("Final module: \n" + finalModule);
            finalModule.writeDotFile("dendrogram_module.dot");
        } else {
            System.out.println("Error: No module were created.");
        }
    }
}