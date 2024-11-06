package umontpellier.erl.calculs.exercice2.part1;

import org.antlr.v4.runtime.misc.Pair;
import umontpellier.erl.calculs.exercice1.CouplingMetric;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClusteringAlgorithm {

    protected final CouplingMetric couplingMetric;

    // Constructeur de la classe ClusteringAlgorithm
    public ClusteringAlgorithm(String projectPath) throws IOException {
        this.couplingMetric = new CouplingMetric(projectPath);
    }

    // Trouve les clusters les plus proches
    public Pair<ClusterNode[], Double> closestClusters(List<ClusterNode> clusters) {
        ClusterNode[] closest = new ClusterNode[2];
        double maxCoupling = -1.0;

        // Itère sur chaque paire de clusters pour trouver le couplage maximum
        for (int i = 0; i < clusters.size(); i++) {
            for (int j = i + 1; j < clusters.size(); j++) {
                double coupling = calculateCoupling(clusters.get(i), clusters.get(j));

                if (coupling > maxCoupling) {
                    maxCoupling = coupling;
                    closest[0] = clusters.get(i);
                    closest[1] = clusters.get(j);
                }
            }
        }
        return new Pair<>(closest, maxCoupling);
    }

    // Calcule le couplage entre deux noeuds de cluster
    public double calculateCoupling(ClusterNode node1, ClusterNode node2) {
        // Si les deux noeuds sont des clusters, calcule le couplage entre les noeuds de chaque cluster
        if (node1 instanceof Cluster && node2 instanceof Cluster) {
            Cluster cluster1 = (Cluster) node1;
            Cluster cluster2 = (Cluster) node2;
            return calculateCoupling(cluster1.getClusterNodesPair().a, cluster2.getClusterNodesPair().a) +
                    calculateCoupling(cluster1.getClusterNodesPair().a, cluster2.getClusterNodesPair().b) +
                    calculateCoupling(cluster1.getClusterNodesPair().b, cluster2.getClusterNodesPair().a) +
                    calculateCoupling(cluster1.getClusterNodesPair().b, cluster2.getClusterNodesPair().b);
        } // Si le premier noeud est un cluster, calcule le couplage entre les noeuds de chaque cluster
        else if (node1 instanceof Cluster) {
            Cluster cluster1 = (Cluster) node1;
            return calculateCoupling(cluster1.getClusterNodesPair().a, node2) +
                    calculateCoupling(cluster1.getClusterNodesPair().b, node2);
        } // Si le deuxième noeud est un cluster, calcule le couplage entre les noeuds de chaque cluster
        else if (node2 instanceof Cluster) {
            Cluster cluster2 = (Cluster) node2;
            return calculateCoupling(node1, cluster2.getClusterNodesPair().a) +
                    calculateCoupling(node1, cluster2.getClusterNodesPair().b);
        } // Si les deux noeuds sont des classes, calcule le couplage entre les classes
        else {
            // Recupère les noms de classes qualifiés
            String qualifiedClass1 = getQualifiedClassName(couplingMetric, node1.getClassName());
            String qualifiedClass2 = getQualifiedClassName(couplingMetric, node2.getClassName());
            if (qualifiedClass1 == null || qualifiedClass2 == null) {
                return 0.0;
            }
            // Calcule le couplage entre les classes avec la classe CouplingMetric
            return couplingMetric.calculateCoupling(qualifiedClass1, qualifiedClass2);
        }
    }

    // Algorithm de clustering hiérarchique
    public void printHierarchicalClustering(List<ClassNode> classes) {
        List<ClusterNode> clusters = new ArrayList<>(classes);

        // Itère jusqu'à ce qu'il ne reste qu'un seul cluster
        while (clusters.size() > 1) {
            Pair<ClusterNode[], Double> closest = closestClusters(clusters);

            if (closest == null) {
                break;
            }
            ClusterNode c1 = closest.a[0];
            ClusterNode c2 = closest.a[1];
            Double couplingc1c2 = closest.b;

            Cluster c3 = new Cluster(c1, c2, couplingc1c2);

            clusters.remove(c1);
            clusters.remove(c2);
            clusters.add(c3);
        }

        // Affiche le cluster final ou un message si aucun cluster n'a été créé
        if (clusters.get(0) instanceof Cluster) {
            Cluster finalCluster = (Cluster) clusters.get(0);
            System.out.println("Final cluster: \n" + finalCluster);
            finalCluster.writeDotFile("dendrogram.dot");
        } else {
            System.out.println("No clusters were created.");
        }
    }

    // Obtient le nom de classe qualifié
    protected static String getQualifiedClassName(CouplingMetric couplingMetric, String className) {
        for (String method : couplingMetric.getInvocations().keySet()) {
            String qualifiedClassName = method.split("::")[0];
            if (qualifiedClassName.endsWith(className)) {
                return qualifiedClassName;
            }
        }
        return null;
    }
}