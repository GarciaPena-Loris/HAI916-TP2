package umontpellier.erl.calculs.exercice1;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class GraphGenerator {

    // Génère un fichier DOT à partir de la carte des dépendances
    public String generateDotGraph(Map<String, Map<String, Double>> classCoupling) {
        StringBuilder dotGraph = new StringBuilder();
        dotGraph.append("digraph Dendrogramme {\n");

        // Itère sur chaque classe source et ses dépendances
        for (Map.Entry<String, Map<String, Double>> entry : classCoupling.entrySet()) {
            String fromClass = entry.getKey();
            Map<String, Double> dependencies = entry.getValue();

            // Ajoute chaque relation de dépendance au graphe DOT
            for (Map.Entry<String, Double> dependency : dependencies.entrySet()) {
                String toClass = dependency.getKey();
                double weight = dependency.getValue();
                dotGraph.append(String.format("    \"%s\" -> \"%s\" [label=\"%.6f\"];\n", fromClass, toClass, weight));
            }
        }

        dotGraph.append("}\n");
        return dotGraph.toString();
    }

    // Enregistre le fichier DOT dans un fichier
    public void writeDotFile(String file, String filename) {
        try (FileWriter fileWriter = new FileWriter(filename)) {
            fileWriter.write(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}