import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;

public class VerifyReleaseSubTaskEnvelope extends Envelope {

    public VerifyReleaseSubTaskEnvelope(EnvelopeType envType, Node sentBy, Node receivedBy) {
        super(envType, sentBy, receivedBy);
    }

    private static byte[] decryptWithPrivateKey(byte[] encryptedData, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(encryptedData);
    }

    private static byte[] encryptWithPublicKey(String data, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] byteData = data.getBytes();
        return cipher.doFinal(byteData);
    }

    private static String decryptWithPrivateKey(String encryptedData, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] data = Base64.getDecoder().decode(encryptedData.getBytes());
        byte[] decrypt = cipher.doFinal(data);
        String decryptedData = new String(decrypt, StandardCharsets.UTF_8);
        return decryptedData;
    }

    public static Envelope createEnvelope(Node Sender, Node Reciever, Envelope prevEnvelope, Envelope linkedEnvelope) {

        try {
            String decryptedContent = decryptWithPrivateKey(prevEnvelope.getEncryptedContent(),
                    prevEnvelope.getReceivedBy().getPrivateKey());
            // System.out.println(decryptedContent);
            String[] results = decryptedContent.substring(1, decryptedContent.length() - 1).split(", ");
            String decryptedContentOfEnvelope = decryptWithPrivateKey(linkedEnvelope.getEncryptedContent(),
                    linkedEnvelope.getReceivedBy().getPrivateKey());
            // System.out.println(decryptedContentOfEnvelope);
            String[] inputs = decryptedContentOfEnvelope.substring(1, decryptedContentOfEnvelope.length() - 1)
                    .split(", ");
            ArrayList<String> testcase = new ArrayList<>();
            testcase.addAll(Arrays.asList(results));
            testcase.addAll(Arrays.asList(inputs));
            // System.out.println(testcase);
            Envelope envelope = new Envelope(EnvelopeType.envrv, Sender, Reciever);
            String f2 = "primeNumber";
            testcase.add(f2);
            System.out.println("Object is " + testcase.toString());
            byte[] encryptedContent = encryptWithPublicKey(testcase.toString(), Reciever.getPublicKey());
            String base64Encoded = Base64.getEncoder().encodeToString(encryptedContent);
            envelope.setEncryptedContent(base64Encoded);
            return envelope;
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }

        return null;
    }

    public static Envelope divideTaskAndCreateEnvelope(Node Sender, Node Reciever, Envelope prevEnvelope,
            Envelope linkedEnvelope, int num) {
        try {
            String decryptedContent = decryptWithPrivateKey(prevEnvelope.getEncryptedContent(),
                    prevEnvelope.getReceivedBy().getPrivateKey());
            // System.out.println(decryptedContent);
            String[] results = decryptedContent.substring(1, decryptedContent.length() - 1).split(", ");
            String decryptedContentOfEnvelope = decryptWithPrivateKey(linkedEnvelope.getEncryptedContent(),
                    linkedEnvelope.getReceivedBy().getPrivateKey());
            // System.out.println(decryptedContentOfEnvelope);
            int halfIndex = results.length / 2;
            if (num == 1) {
                String[] inputs = decryptedContentOfEnvelope.substring(1, decryptedContentOfEnvelope.length() - 1)
                        .split(", ");
                ArrayList<String> testcase = new ArrayList<>();
                String firstHalfResult[] = Arrays.copyOfRange(results, 0, halfIndex);
                testcase.addAll(Arrays.asList(firstHalfResult));
                System.out.println(
                        "First Half of the results " + Arrays.toString(Arrays.copyOfRange(results, 0, halfIndex)));
                inputs[2] = Integer.toString(((Integer.parseInt(inputs[1]) + Integer.parseInt(inputs[2])) / 2));
                testcase.addAll(Arrays.asList(inputs));
                System.out.println("testcase " + testcase);
                Envelope envelope = new Envelope(EnvelopeType.envrv, Sender, Reciever);
                String f2 = "primeNumber";
                // Object testcase = new Object() {
                //     public String test = testcase.toString();
                //     public String exec = f2;

                //     @Override
                //     public String toString() {
                //         return test + " " + exec;
                //     }
                // };
                testcase.add(f2);
                System.out.println("Object is " + testcase.toString());
                byte[] encryptedContent = encryptWithPublicKey(testcase.toString(),
                        Reciever.getPublicKey());
                String base64Encoded = Base64.getEncoder().encodeToString(encryptedContent);
                envelope.setEncryptedContent(base64Encoded);
                return envelope;
            } else {
                String[] inputs = decryptedContentOfEnvelope.substring(1, decryptedContentOfEnvelope.length() - 1)
                        .split(", ");
                ArrayList<String> testcase = new ArrayList<>();
                String secondHalfResult[] = Arrays.copyOfRange(results, halfIndex, results.length);
                testcase.addAll(Arrays.asList(secondHalfResult));
                inputs[1] = Integer.toString(((Integer.parseInt(inputs[1]) + Integer.parseInt(inputs[2])) / 2 + 1));
                testcase.addAll(Arrays.asList(inputs));
                System.out.println("testcase " + testcase);
                String f2 = "primeNumber";
                // Object testcase = new Object() {
                //     public String test = testcase.toString();
                //     public String exec = f2;

                //     @Override
                //     public String toString() {
                //         return test + " " + exec;
                //     }
                // };
                testcase.add(f2);
                Envelope envelope = new Envelope(EnvelopeType.envrv, Sender, Reciever);
                byte[] encryptedContent = encryptWithPublicKey(testcase.toString(),
                        Reciever.getPublicKey());
                String base64Encoded = Base64.getEncoder().encodeToString(encryptedContent);
                envelope.setEncryptedContent(base64Encoded);
                return envelope;
            }
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }
        return null;
    }

}
