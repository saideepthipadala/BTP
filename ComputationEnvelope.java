import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Base64;

import javax.crypto.Cipher;

public class ComputationEnvelope extends Envelope {

    private static byte[] encryptWithPublicKey(byte[] data, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    private static byte[] decryptWithPrivateKey(byte[] encryptedData, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(encryptedData);
    }

    public ComputationEnvelope(EnvelopeType envType, Node sentBy, Node receivedBy) {
        super(envType, sentBy, receivedBy);
    }

    public static Envelope createEnvelope(Node sender, Node receiver, Envelope prevEnvelope) {
        byte[] decryptedContent;
        try {
            decryptedContent = decryptWithPrivateKey(
                    Base64.getDecoder().decode(prevEnvelope.getEncryptedContent().getBytes()),
                    prevEnvelope.getReceivedBy().getPrivateKey());
            String DecryptedContent = new String(decryptedContent, StandardCharsets.UTF_8);
            String[] elements = DecryptedContent.substring(1, DecryptedContent.length() - 1).split(", ");
            ArrayList<Integer> results = new ArrayList<>();
            for (int i = Integer.parseInt(elements[1]); i < Integer.parseInt(elements[2]); i++) {
                results.add((int) Math.sqrt(i));
            }
            ComputationEnvelope envelope = new ComputationEnvelope(EnvelopeType.envcs, sender, receiver);
            byte[] encryptedResult = encryptWithPublicKey(results.toString().getBytes(StandardCharsets.UTF_8),
                    receiver.getPublicKey());
            envelope.setEncryptedContent(Base64.getEncoder().encodeToString(encryptedResult));
            System.out.println("Encrypted Result in envcs  :" + envelope.getEncryptedContent());
            return envelope;
        } catch (Exception e) {
            System.out.println("Hello world");
            e.printStackTrace();
        }
        return null;
    }
}
