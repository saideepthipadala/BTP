import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

public class PairingSetup {
    private static Pairing pairing;
    private static Element g1, g2;

    public static void initPairing() {
        pairing = PairingFactory.getPairing("a.properties");
        g1 = pairing.getG1().newRandomElement();
        g2 = pairing.getG2().newRandomElement();
    }

    public static Pairing getPairing() {
        return pairing;
    }

    public static Element getG1() {
        return g1;
    }

    public static Element getG2() {
        return g2;
    }
}
