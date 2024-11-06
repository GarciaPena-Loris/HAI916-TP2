package umontpellier.erl.calculs.Visitor;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import umontpellier.erl.calculs.exercice2.part1.ClassNode;

import java.util.ArrayList;
import java.util.List;

public class TypeDeclarationVisitor extends ASTVisitor {
    private final List<ClassNode> classNodes;

    public TypeDeclarationVisitor() {
        classNodes = new ArrayList<>();
    }

    // Visite les déclarations de classes (ou interfaces)
    @Override
    public boolean visit(TypeDeclaration node) {
        String className = node.getName().getIdentifier();
        ClassNode classNode = new ClassNode(className);
        classNodes.add(classNode);
        return super.visit(node);
    }

    // Retourne la liste des classes extraites
    public List<ClassNode> getClassNodes() {
        return classNodes;
    }
}
