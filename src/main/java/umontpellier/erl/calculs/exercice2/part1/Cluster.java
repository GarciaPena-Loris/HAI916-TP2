package umontpellier.erl.calculs.exercice2.part1;

import org.antlr.v4.runtime.misc.Pair;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Cluster extends ClusterNode {
    protected final Pair<ClusterNode, ClusterNode> clusterNodes;
    protected final double couplingDegree;

    // Constructeur de la classe Cluster
    public Cluster(ClusterNode clusterNode1, ClusterNode clusterNode2, Double couplingDegree) {
        super(generateClusterName(clusterNode1, clusterNode2));
        this.clusterNodes = new Pair<>(clusterNode1, clusterNode2);
        this.couplingDegree = couplingDegree;
    }

    // Retourne la paire de noeuds de cluster
    public Pair<ClusterNode, ClusterNode> getClusterNodesPair() {
        return clusterNodes;
    }

    // Retourne une représentation en chaîne de caractères du cluster
    @Override
    public String toString() {
        return toDendrogramString(0, "");
    }

    // Génère une représentation en chaîne de caractères du dendrogramme
    private String toDendrogramString(int depth, String prefix) {
        StringBuilder sb = new StringBuilder();

        // Ajoute le nom de la classe et le degré de couplage
        sb.append(prefix).append("+-- ").append(getClassName()).append(" : ").append(String.format("%.5f", couplingDegree)).append("\n");

        String childPrefix = prefix + (depth == 0 ? "   " : "|   ");

        // Ajoute les noeuds enfants
        if (clusterNodes != null) {
            ClusterNode left = clusterNodes.a;
            ClusterNode right = clusterNodes.b;
            if (left != null) {
                sb.append(left instanceof Cluster
                        ? ((Cluster) left).toDendrogramString(depth + 1, childPrefix)
                        : childPrefix + "+-- " + left.getClassName() + "\n");
            }
            if (right != null) {
                sb.append(right instanceof Cluster
                        ? ((Cluster) right).toDendrogramString(depth + 1, childPrefix)
                        : childPrefix + "+-- " + right.getClassName() + "\n");
            }
        }
        return sb.toString();
    }

    // Génère un nom de cluster basé sur les noms des noeuds
    private static String generateClusterName(ClusterNode node1, ClusterNode node2) {
        List<String> classNames = new ArrayList<>();
        collectClassNames(node1, classNames);
        collectClassNames(node2, classNames);

        if (classNames.size() > 3) {
            return String.join(", ", classNames.subList(0, 3)) + ", ...";
        } else {
            return String.join(", ", classNames);
        }
    }

    // Collecte les noms des classes des noeuds
    private static void collectClassNames(ClusterNode node, List<String> classNames) {
        if (node instanceof ClassNode) {
            classNames.add(node.getClassName());
        } else if (node instanceof Cluster) {
            Cluster cluster = (Cluster) node;
            collectClassNames(cluster.clusterNodes.a, classNames);
            collectClassNames(cluster.clusterNodes.b, classNames);
        }
    }

    /* DOT format */

    // Retourne une représentation en format DOT du cluster
    public String toDot() {
        StringBuilder dot = new StringBuilder();
        dot.append("digraph Dendrogramme {\n");
        AtomicInteger nodeId = new AtomicInteger(0);
        generateDot(dot, nodeId);
        dot.append("}\n");
        return dot.toString();
    }

    // Génère le contenu DOT pour le cluster
    protected int generateDot(StringBuilder dot, AtomicInteger nodeId) {
        int currentId = nodeId.getAndIncrement();
        dot.append("  ").append(currentId).append(" [label=\"").append(getClassName()).append(" : ").append(String.format("%.5f", couplingDegree)).append("\"];\n");

        if (clusterNodes != null) {
            ClusterNode left = clusterNodes.a;
            ClusterNode right = clusterNodes.b;

            if (left != null) {
                int leftId = left instanceof Cluster
                        ? ((Cluster) left).generateDot(dot, nodeId)
                        : addLeafNode(dot, nodeId, left.getClassName(), left instanceof ClassNode);
                dot.append("  ").append(currentId).append(" -> ").append(leftId).append(" [dir=none];\n");
            }

            if (right != null) {
                int rightId = right instanceof Cluster
                        ? ((Cluster) right).generateDot(dot, nodeId)
                        : addLeafNode(dot, nodeId, right.getClassName(), right instanceof ClassNode);
                dot.append("  ").append(currentId).append(" -> ").append(rightId).append(" [dir=none];\n");
            }
        }
        return currentId;
    }

    // Ajoute un noeud feuille au contenu DOT
    protected int addLeafNode(StringBuilder dot, AtomicInteger nodeId, String className, boolean isClassNode) {
        int id = nodeId.getAndIncrement();
        dot.append("  ").append(id).append(" [label=\"").append(className).append("\"");
        if (isClassNode) {
            dot.append(" color=blue");
        }
        dot.append("];\n");
        return id;
    }

    // Écrit le contenu DOT dans un fichier
    public void writeDotFile(String filename) {
        try (FileWriter fileWriter = new FileWriter(filename)) {
            fileWriter.write(toDot());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}