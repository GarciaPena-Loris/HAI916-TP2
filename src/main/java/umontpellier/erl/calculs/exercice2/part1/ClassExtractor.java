package umontpellier.erl.calculs.exercice2.part1;

import org.eclipse.jdt.core.dom.CompilationUnit;
import umontpellier.erl.calculs.parser.Parser;
import umontpellier.erl.calculs.Visitor.TypeDeclarationVisitor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClassExtractor {

    private final Parser parser;

    public ClassExtractor(String projectPath) {
        this.parser = new Parser(projectPath);
    }

    // Extrait toutes les classes d'un projet Java
    public List<ClassNode> extractClasses() throws IOException {
        List<ClassNode> classNodes = new ArrayList<>();
        // On parse le projet
        List<CompilationUnit> compilationUnits = parser.parseProject();

        for (CompilationUnit cu : compilationUnits) {
            // On visite chaque CompilationUnit pour extraire les classes
            TypeDeclarationVisitor visitor = new TypeDeclarationVisitor();
            cu.accept(visitor);
            classNodes.addAll(visitor.getClassNodes());
        }


        return classNodes;
    }
}