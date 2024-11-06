package umontpellier.erl.calculs.exercice2.part2;

import umontpellier.erl.calculs.exercice2.part1.Cluster;
import umontpellier.erl.calculs.exercice2.part1.ClusterNode;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Module extends Cluster {
    private final String color;

    // Construit un module à partir d'une liste de noeuds de cluster
    public static Module buildModule(List<ClusterNode> clusterNodes) {
        Module finalModule = new Module(clusterNodes.get(0), clusterNodes.get(1), 0.0, "purple");
        for (int i = 1; i < clusterNodes.size(); i++) {
            finalModule = new Module(finalModule, clusterNodes.get(i), 0.0, "purple");
        }
        return finalModule;
    }

    // Constructeur de la classe Module
    public Module(ClusterNode clusterNode1, ClusterNode clusterNode2, Double couplingDegree, String color) {
        super(clusterNode1, clusterNode2, couplingDegree);
        this.color = color;
    }

    // Génère le contenu DOT pour le module
    protected int generateDot(StringBuilder dot, AtomicInteger nodeId) {
        int currentId = nodeId.getAndIncrement();
        dot.append("  ").append(currentId).append(" [label=\"").append(getClassName()).append(" : ").append(String.format("%.5f", couplingDegree)).append("\", color=\"").append(color).append("\"];\n");

        if (clusterNodes != null) {
            ClusterNode left = clusterNodes.a;
            ClusterNode right = clusterNodes.b;

            if (left != null) {
                int leftId = left instanceof Module
                        ? ((Module) left).generateDot(dot, nodeId)
                        : addLeafNode(dot, nodeId, left.getClassName());
                dot.append("  ").append(currentId).append(" -> ").append(leftId).append(" [dir=none color=\"").append(color).append("\"];\n");
            }

            if (right != null) {
                int rightId = right instanceof Module
                        ? ((Module) right).generateDot(dot, nodeId)
                        : addLeafNode(dot, nodeId, right.getClassName());
                dot.append("  ").append(currentId).append(" -> ").append(rightId).append(" [dir=none color=\"").append(color).append("\"];\n");
            }
        }
        return currentId;
    }

    // Ajoute un noeud feuille au contenu DOT
    protected int addLeafNode(StringBuilder dot, AtomicInteger nodeId, String className) {
        int id = nodeId.getAndIncrement();
        dot.append("  ").append(id).append(" [label=\"").append(className).append("\", color=\"").append(color).append("\"];\n");
        return id;
    }
}