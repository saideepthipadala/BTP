import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import java.security.MessageDigest;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.jpbc.Field;
import javax.crypto.Cipher;

public class ComputationEnvelopeSubtask extends Envelope {

    private String Proof;
    private Pairing pairings;
    private Element g1;
    private Element g2;
    private Element PublicKey;

    // private Pairing pairing;

    public Pairing getPairings() {
        return pairings;
    }

    public void setPairings(Pairing pairing) {
        this.pairings = pairing;
    }

    public Element getG1() {
        return g1;
    }

    public void setG1(Element g1) {
        this.g1 = g1;
    }

    public Element getG2() {
        return g2;
    }

    public void setG2(Element g2) {
        this.g2 = g2;
    }

    private static String encryptWithPublicKey(String data, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] byteData = data.getBytes(StandardCharsets.UTF_8);
        byte[] encryptedData = cipher.doFinal(byteData);
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    private static String decryptWithPrivateKey(String encryptedData, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] data = Base64.getDecoder().decode(encryptedData.getBytes());
        byte[] decrypt = cipher.doFinal(data);
        String decryptedData = new String(decrypt, StandardCharsets.UTF_8);
        return decryptedData;
    }

    public static boolean isPrime(int n, double sqrt) {

        if (n <= 1)
            return false;

        if (n == 2 || n == 3)
            return true;

        if (n % 2 == 0 || n % 3 == 0)
            return false;

        for (int i = 5; i <= sqrt; i = i + 6)
            if (n % i == 0 || n % (i + 2) == 0)
                return false;
        return true;
    }

    public ComputationEnvelopeSubtask(EnvelopeType envType, Node sentBy, Node receivedBy) {
        super(envType, sentBy, receivedBy);
    }

    public static Envelope createEnvelope(Node sender, Node receiver, Envelope prevEnvelope, String functiontype) {
        try {
            String decryptedString = decryptWithPrivateKey(prevEnvelope.getEncryptedContent(), sender.getPrivateKey());
            String decryptedArray[] = decryptedString.substring(1, decryptedString.length() - 1).split(", ");
            StringBuilder results = new StringBuilder();
            if (functiontype.equals("sqrt")) {
                for (int i = Integer.parseInt(decryptedArray[1]); i <= Integer.parseInt(decryptedArray[2]); i++) {
                    String encryptedResult = encryptWithPublicKey(String.valueOf((int) Math.sqrt(i)),
                            receiver.getPublicKey());
                    results.append(encryptedResult);
                    results.append(" ");
                }
                ComputationEnvelopeSubtask envelope = new ComputationEnvelopeSubtask(EnvelopeType.envcs, sender,
                        receiver);
                envelope.setEncryptedContent(results.toString());
                return envelope;
            } else {
                for (int i = Integer.parseInt(decryptedArray[1]); i <= Integer.parseInt(decryptedArray[2]); i++) {
                    Boolean TorF = isPrime(i, (int) Math.sqrt(i));
                    String res = String.valueOf(i) + "->" + String.valueOf(TorF);
                    System.out.println(res);
                    String encryptedResult = encryptWithPublicKey(res,
                            receiver.getPublicKey());

                    results.append(encryptedResult);
                    results.append(" ");
                }
                ComputationEnvelopeSubtask envelope = new ComputationEnvelopeSubtask(EnvelopeType.envcs, sender,
                        receiver);
                envelope.setEncryptedContent(results.toString());
                return envelope;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    public static ComputationEnvelopeSubtask createCsEnvelope(Node sender, Node receiver, Envelope prevEnvelope) {
        try {

            String decryptedString = decryptWithPrivateKey(prevEnvelope.getEncryptedContent(), sender.getPrivateKey());
            String[] parts = decryptedString.replaceAll("[\\[\\]]", "").split(",");
            List<String> encryptedResults = new ArrayList<>();
            int start = Integer.parseInt(parts[0].trim());
            int end = Integer.parseInt(parts[1].trim());

            Pairing pairing = PairingFactory.getPairing("a.properties");
            Field G1 = pairing.getG1();
            Field G2 = pairing.getG2();
            Field Zr = pairing.getZr();

           
            ComputationEnvelopeSubtask envelope = new ComputationEnvelopeSubtask(EnvelopeType.envcs, sender, receiver);
            envelope.setPairings(pairing);

            Element privateKey = Zr.newRandomElement().getImmutable();
            Element g1 = G1.newRandomElement().getImmutable();
            Element g2 = G2.newRandomElement().getImmutable();
            Element publicKey = g2.duplicate().mulZn(privateKey);

            envelope.setG1(g1);
            envelope.setG2(g2);
            envelope.setPublicKey(publicKey);

            StringBuilder proofBuilder = new StringBuilder();

            while (start <= end) {
                double sqrt = Math.sqrt(start);
                boolean primeOrNot = isPrime(start, sqrt);
                if (primeOrNot) {
                    // Prepare the result string for encryption
                    String str = start + "->true";
                    List<String> encryptedChunks = encryptInChunks(str, receiver.getPublicKey());
                    encryptedResults.addAll(encryptedChunks);

                    byte[] numberBytes = Integer.toString(start).getBytes(StandardCharsets.UTF_8);
                    Element hashedNumber = G1.newElementFromHash(numberBytes, 0, numberBytes.length);
                    Element signature = hashedNumber.duplicate().mulZn(privateKey);

                    String encodedSignature = Base64.getEncoder().encodeToString(signature.toBytes());
                    String proof = start + "->Signature:" + encodedSignature;

                    if (proofBuilder.length() > 0) {
                        proofBuilder.append("|");
                    }
                    proofBuilder.append(proof);
                }
                start++;
            }

        
            String joinedResults = String.join("|", encryptedResults);
            envelope.setEncryptedContent(joinedResults);

            envelope.setProof(proofBuilder.toString().trim());

            return envelope;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static List<String> encryptInChunks(String data, PublicKey publicKey) throws Exception {
        List<String> chunks = new ArrayList<>();
        int maxChunkSize = 245;

        // Break data into smaller chunks
        for (int i = 0; i < data.length(); i += maxChunkSize) {
            int end = Math.min(data.length(), i + maxChunkSize);
            String chunk = data.substring(i, end);
            String encryptedChunk = encryptWithPublicKey(chunk, publicKey);
            chunks.add(encryptedChunk);
        }

        return chunks;
    }

    public String getProof() {
        return Proof;
    }

    public void setProof(String proof) {
        Proof = proof;
    }

    public Element getPublicKey() {
        return PublicKey;
    }

    public void setPublicKey(Element publicKey) {
        PublicKey = publicKey;
    }

}
