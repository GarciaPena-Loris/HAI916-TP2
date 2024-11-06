package umontpellier.erl.calculs.Visitor;

import org.eclipse.jdt.core.dom.*;

import java.util.HashMap;
import java.util.Map;

public class MethodCallVisitor extends ASTVisitor {
    private final Map<String, Integer> methodCalls = new HashMap<>();

    public MethodCallVisitor() {
    }

    @Override
    public boolean visit(MethodInvocation node) {
        String calledMethodName = node.getName().getFullyQualifiedName();
        String receiverType = resolveReceiverType(node);
        String fullyQualifiedCalledMethod = receiverType + "::" + calledMethodName;
        methodCalls.merge(fullyQualifiedCalledMethod, 1, Integer::sum);

        return super.visit(node);
    }

    private String resolveReceiverType(MethodInvocation node) {
        Expression expression = node.getExpression();
        if (expression != null) {
            ITypeBinding typeBinding = expression.resolveTypeBinding();
            if (typeBinding != null) {
                return typeBinding.getName();
            }
        }
        return "Unknown";
    }

    public Map<String, Integer> getMethodCalls() {
        return methodCalls;
    }
}