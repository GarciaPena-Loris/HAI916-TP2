package umontpellier.erl.calculs.exercice1;

import org.eclipse.jdt.core.dom.*;
import umontpellier.erl.calculs.parser.Parser;
import umontpellier.erl.calculs.Visitor.MethodCallVisitor;

import java.io.IOException;
import java.util.*;

public class CouplingMetric {
    private final Map<String, Map<String, Integer>> invocations = new HashMap<>();

    // Constructeur qui initialise le parseur et analyse le projet
    public CouplingMetric(String projectPath) throws IOException {
        Parser parser = new Parser(projectPath);

        for (CompilationUnit cUnit : parser.parseProject()) {
            List<TypeDeclaration> types = getTypeDeclarations(cUnit);
            for (TypeDeclaration typ : types) {
                for (MethodDeclaration method : typ.getMethods()) {
                    addMethodAndInvocations(typ, method);
                }
            }
        }
    }

    // Retourne la carte des invocations
    public Map<String, Map<String, Integer>> getInvocations() {
        return invocations;
    }

    // Calcule le couplage entre deux classes
    public double calculateCoupling(String classA, String classB) {
        int relationsAB = getRelationsBetweenClasses(classA, classB);
        int relationsBA = getRelationsBetweenClasses(classB, classA);
        int totalRelations = getTotalRelationsBetweenClasses();
        return (double) (relationsAB + relationsBA) / totalRelations;
    }

    // Obtient le nombre de relations entre deux classes
    public int getRelationsBetweenClasses(String classA, String classB) {
        int count = 0;

        // Itère sur les clés de la carte des invocations
        for (String source : invocations.keySet()) {
            String sourceClass = source.split("::")[0]; // Extraction de la classe source

            // Vérifie si la classe source est classA
            if (sourceClass.equals(classA)) {
                // Itère sur les méthodes appelées par la méthode source
                for (String destination : invocations.get(source).keySet()) {
                    // Extraction de la classe de la méthode appelée (en format simple)
                    String destinationClass = destination.split("::")[0];

                    // Vérifie si la classe de destination correspond au nom simple de classB
                    if (classB.endsWith(destinationClass)) {
                        // Accumule le nombre d'invocations
                        count += invocations.get(source).get(destination);
                    }
                }
            }
        }

        return count; // Retourne le nombre total d'invocations
    }

    // Obtient le nombre total de relations entre différentes classes
    public int getTotalRelationsBetweenClasses() {
        int count = 0;

        for (Map<String, Integer> methodCalls : invocations.values()) {
            for (String destination : methodCalls.keySet()) {
                count += methodCalls.get(destination);
            }
        }

        return count; // Retourne le nombre total d'invocations entre différentes classes
    }

    // Obtient les déclarations de type d'une unité de compilation
    private static List<TypeDeclaration> getTypeDeclarations(CompilationUnit cUnit) {
        List<TypeDeclaration> types = new ArrayList<>();
        for (Object type : cUnit.types()) {
            if (type instanceof TypeDeclaration) {
                types.add((TypeDeclaration) type);
            }
        }
        return types;
    }

    // Ajoute les méthodes et les invocations à la carte des invocations
    private void addMethodAndInvocations(TypeDeclaration cls, MethodDeclaration method) {
        if (method.getBody() != null) {
            String methodName = getMethodFullyQualifiedName(cls, method);

            MethodCallVisitor invocationCollector = new MethodCallVisitor();
            method.accept(invocationCollector);

            for (Map.Entry<String, Integer> entry : invocationCollector.getMethodCalls().entrySet()) {
                invocations.computeIfAbsent(methodName, k -> new HashMap<>())
                           .merge(entry.getKey(), entry.getValue(), Integer::sum);
            }
        }
    }

    // Obtient le nom completement qualifié d'une classe
    public static String getClassFullyQualifiedName(TypeDeclaration typeDeclaration) {
        String name = typeDeclaration.getName().getIdentifier();

        // Vérifie si la classe est déclarée dans un fichier source
        if (typeDeclaration.getRoot().getClass() == CompilationUnit.class) {
            CompilationUnit root = (CompilationUnit) typeDeclaration.getRoot();

            // Vérifie si la classe est déclarée dans un package
            if (root.getPackage() != null) {
                // Concatène le nom du package avec le nom de la classe
                name = root.getPackage().getName().getFullyQualifiedName() + "." + name;
            }
        }

        return name;
    }

    // Obtient le nom completement qualifié d'une méthode
    public static String getMethodFullyQualifiedName(TypeDeclaration cls, MethodDeclaration method) {
        return getClassFullyQualifiedName(cls) + "::" + method.getName();
    }

    // Obtient le couplage entre les classes
    public Map<String, Map<String, Double>> getClassCoupling() {
        Map<String, Map<String, Double>> classCoupling = new HashMap<>();
        Set<String> classes = new HashSet<>();
        Set<String> processedPairs = new HashSet<>();

        // Collecte tous les noms de classes
        for (String method : invocations.keySet()) {
            classes.add(method.split("::")[0]);
        }

        classes.add("Unknown");

        // Calcule le couplage entre chaque paire de classes, y compris "Unknown"
        for (String classA : classes) {
            for (String classB : classes) {
                if (!classA.equals(classB) || classB.equals("Unknown")) {
                    String pairKey = classA + "-" + classB;
                    String reversePairKey = classB + "-" + classA;

                    if (!processedPairs.contains(pairKey) && !processedPairs.contains(reversePairKey)) {
                        double coupling = calculateCoupling(classA, classB);
                        if (coupling > 0) {
                            // Ajoute le couplage entre les classes à la map des couplages
                            classCoupling.computeIfAbsent(classA, k -> new HashMap<>())
                                    .put(classB, coupling);
                        }
                        processedPairs.add(pairKey);
                    }
                }
            }
        }

        return classCoupling;
    }
}