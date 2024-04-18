import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedHashMap;

import javax.crypto.Cipher;

public class ComputationEnvelopeSubtask extends Envelope {

    private static byte[] encryptWithPublicKey(byte[] data, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    private static byte[] encryptWithPublicKey(String data, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] byteData = data.getBytes();
        return cipher.doFinal(byteData);
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

    private static byte[] decryptWithPrivateKey(byte[] encryptedData, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(encryptedData);
    }

    public ComputationEnvelopeSubtask(EnvelopeType envType, Node sentBy, Node receivedBy) {
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
            // System.out.println(Arrays.toString(elements));
            ArrayList<Integer> results = new ArrayList<>();
            for (int i = Integer.parseInt(elements[1]); i <= Integer.parseInt(elements[2]); i++) {
                results.add((int) Math.sqrt(i));
            }
            ComputationEnvelopeSubtask envelope = new ComputationEnvelopeSubtask(EnvelopeType.envcs, sender, receiver);
            byte[] encryptedResult = encryptWithPublicKey(results.toString().getBytes(StandardCharsets.UTF_8),
                    receiver.getPublicKey());
            envelope.setEncryptedContent(Base64.getEncoder().encodeToString(encryptedResult));
            // System.out.println("Encrypted Result in envcs :" +
            // envelope.getEncryptedContent());
            return envelope;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Envelope createCsEnvelope(Node sender, Node receiver, Envelope prevEnvelope) {
        byte[] decryptedContent;
        try {
            decryptedContent = decryptWithPrivateKey(
                    Base64.getDecoder().decode(prevEnvelope.getEncryptedContent().getBytes()),
                    prevEnvelope.getReceivedBy().getPrivateKey());
            String DecryptedContent = new String(decryptedContent, StandardCharsets.UTF_8);
            String trimmedContent = DecryptedContent.substring(1, DecryptedContent.length() - 1);
            System.out.println(trimmedContent);
            String[] elements = trimmedContent.split(", ");

            System.out.println("elements are " + Arrays.toString(elements));
            LinkedHashMap<Integer, Boolean> result = new LinkedHashMap<>();
            // ArrayList<Boolean> result = new ArrayList<>();
            int start = Integer.parseInt(elements[elements.length - 3]);
            int i = 0;
            int end = Integer.parseInt(elements[elements.length - 2]);
            while (i < elements.length - 4) {
                result.put(start, isPrime(start, Integer.parseInt(elements[i])));
                start++;
                i++;
            }
            System.out.println(result);

            byte[] encryptedContent = encryptWithPublicKey(result.toString(), receiver.getPublicKey());
            String base64EncodedContent = Base64.getEncoder().encodeToString(encryptedContent);
            Envelope envelope = new Envelope(EnvelopeType.envcs, sender, receiver);
            envelope.setEncryptedContent(base64EncodedContent);
            return envelope;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
