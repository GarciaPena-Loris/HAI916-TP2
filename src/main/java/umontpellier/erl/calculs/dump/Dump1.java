package umontpellier.erl.calculs.dump;

import umontpellier.erl.calculs.parser.Parser;

// Classe exemple
public class Dump1 {

    public void method1() {
        Dump2 dump2 = new Dump2();
        dump2.methodA();
        dump2.methodA();
        dump2.methodB();
        dump2.methodD();

        Parser parser = new Parser("path");
        parser.configure();
    }

    public void method2() {
        Dump2 dump2 = new Dump2();
        dump2.methodA();
        dump2.methodC();
    }

    public void method3() {
        Dump2 dump2 = new Dump2();
        dump2.methodB();
        dump2.methodC();
    }
}