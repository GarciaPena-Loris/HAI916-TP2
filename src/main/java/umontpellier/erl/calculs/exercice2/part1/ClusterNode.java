package umontpellier.erl.calculs.exercice2.part1;

public abstract class ClusterNode {
    private final String className;

    public ClusterNode(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    @Override
    public String toString() {
        return className;
    }

}
