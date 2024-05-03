import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import javax.crypto.Cipher;

public class ComputationEnvelopeSubtask extends Envelope {

    private static String encryptWithPublicKey(String data, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] byteData = data.getBytes();
        byte[] encryptedData = cipher.doFinal(byteData);
        return Base64.getEncoder().encodeToString(encryptedData);
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

    private static String decryptWithPrivateKey(String encryptedData, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] data = Base64.getDecoder().decode(encryptedData.getBytes());
        byte[] decrypt = cipher.doFinal(data);
        String decryptedData = new String(decrypt, StandardCharsets.UTF_8);
        return decryptedData;
    }

    public ComputationEnvelopeSubtask(EnvelopeType envType, Node sentBy, Node receivedBy) {
        super(envType, sentBy, receivedBy);
    }

    public static Envelope createEnvelope(Node sender, Node receiver, Envelope prevEnvelope) {
        try {
            String decryptedString = decryptWithPrivateKey(prevEnvelope.getEncryptedContent(), sender.getPrivateKey());
            // System.out.println(Arrays.toString(decryptedArray));
            String decryptedArray[] = decryptedString.substring(1, decryptedString.length() - 1).split(", ");
            StringBuilder results = new StringBuilder();

            for (int i = Integer.parseInt(decryptedArray[1]); i <= Integer.parseInt(decryptedArray[2]); i++) {
                String encryptedResult = encryptWithPublicKey(String.valueOf((int) Math.sqrt(i)),
                        receiver.getPublicKey());
                results.append(encryptedResult);
                results.append(" ");
            }
            ComputationEnvelopeSubtask envelope = new ComputationEnvelopeSubtask(EnvelopeType.envcs, sender, receiver);
            envelope.setEncryptedContent(results.toString());
            return envelope;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Envelope createCsEnvelope(Node sender, Node receiver, Envelope prevEnvelope) {

        try {
            // System.out.println("Encrypted Content : " +
            // prevEnvelope.getEncryptedContent());
            String decryptedarr[] = prevEnvelope.getEncryptedContent().split(" ");
            // System.out.println(Arrays.toString(decryptedarr));
            StringBuilder result = new StringBuilder();
            String decryptedprev = decryptWithPrivateKey(decryptedarr[decryptedarr.length - 2],
                    sender.getPrivateKey());
            String arr[] = decryptedprev.substring(1, decryptedprev.length() - 1).split(", ");
            int start = Integer.parseInt(arr[1]);
            int end = Integer.parseInt(arr[2]);
            for (int i = 0; i < decryptedarr.length - 2; i++) {
                if (start <= end) {
                    int sqrt = Integer.parseInt(decryptWithPrivateKey(decryptedarr[i], sender.getPrivateKey()));
                    boolean primeOrNot = isPrime(start, sqrt);
                    if (primeOrNot) {
                        result.append(encryptWithPublicKey(start + "->" +
                                String.valueOf(isPrime(start, sqrt)),
                                receiver.getPublicKey()));
                        result.append(" ");
                    }
                    // result.append(encryptWithPublicKey(start + "->" +
                    // String.valueOf(isPrime(start, sqrt)),
                    // receiver.getPublicKey()));
                    // result.append(" ");
                    start++;
                }
            }
            Envelope envelope = new Envelope(EnvelopeType.envcs, sender, receiver);
            envelope.setEncryptedContent(result.toString());
            return envelope;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
