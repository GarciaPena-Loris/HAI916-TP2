package umontpellier.erl.calculs.dump;

// Classe exemple
public class Dump2 {
    public void methodA() {
        System.out.println("Method A");
    }

    public void methodB() {
        System.out.println("Method B");
    }

    public void methodC() {
        System.out.println("Method C");
        methodE();
    }

    public void methodD() {
        Dump1 dump1 = new Dump1();
        dump1.method1();
        dump1.method2();
    }

    public void methodE() {
        Dump1 dump1 = new Dump1();
        dump1.method3();
    }
}