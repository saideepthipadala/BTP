import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import javax.crypto.Cipher;

public class VerifyReleaseSubTaskEnvelope extends Envelope {

    public VerifyReleaseSubTaskEnvelope(EnvelopeType envType, Node sentBy, Node receivedBy) {
        super(envType, sentBy, receivedBy);
    }

    private static String encryptWithPublicKey(String data, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] byteData = data.getBytes();
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

    // public static Envelope createEnvelope(Node Sender, Node Reciever, Envelope
    // prevEnvelope, Envelope linkedEnvelope) {

    // try {
    // String decryptedArray[] = prevEnvelope.getEncryptedContent().split(" ");
    // StringBuilder results = new StringBuilder();
    // StringBuilder testcase = new StringBuilder();
    // for (int i = 0; i < decryptedArray.length; i++) {
    // String decryptedString = decryptWithPrivateKey(decryptedArray[i],
    // prevEnvelope.getReceivedBy().getPrivateKey());
    // // System.out.println(decryptedString);
    // results.append(decryptedString);
    // testcase.append(encryptWithPublicKey(decryptedString,
    // Reciever.getPublicKey()));
    // testcase.append(" ");
    // }
    // String decryptedContentOfEnvelope =
    // decryptWithPrivateKey(linkedEnvelope.getEncryptedContent(),
    // linkedEnvelope.getReceivedBy().getPrivateKey());
    // String arr[] = decryptedContentOfEnvelope.substring(1,
    // decryptedContentOfEnvelope.length() - 1).split(", ");
    // System.out.println(Arrays.toString(arr));
    // testcase.append(encryptWithPublicKey(Arrays.toString(arr),
    // Reciever.getPublicKey()));
    // testcase.append(" ");
    // Envelope envelope = new Envelope(EnvelopeType.envrv, Sender, Reciever);
    // String f2 = "primeNumber";
    // String encryptedf2 = encryptWithPublicKey(f2, Reciever.getPublicKey());
    // testcase.append(encryptedf2);
    // envelope.setEncryptedContent(testcase.toString());
    // return envelope;
    // // return null;
    // } catch (Exception e) {
    // e.printStackTrace();
    // // TODO: handle exception
    // }

    // return null;
    // }

    // public static Envelope divideTaskAndCreateEnvelope(Node Sender, Node
    // Reciever, Envelope prevEnvelope,
    // Envelope linkedEnvelope, int num) {
    // try {
    // ArrayList<String> results = new ArrayList<>();
    // String decryptedArray[] = prevEnvelope.getEncryptedContent().split(" ");
    // for (int i = 0; i < decryptedArray.length; i++) {
    // String decryptedString = decryptWithPrivateKey(decryptedArray[i],
    // prevEnvelope.getReceivedBy().getPrivateKey());
    // results.add(decryptedString);
    // }
    // String decryptedContentOfEnvelope =
    // decryptWithPrivateKey(linkedEnvelope.getEncryptedContent(),
    // linkedEnvelope.getReceivedBy().getPrivateKey());
    // // System.out.println(decryptedContentOfEnvelope);
    // String arr[] = decryptedContentOfEnvelope.substring(1,
    // decryptedContentOfEnvelope.length() - 1).split(", ");
    // int halfIndex = results.size() / 2;
    // if (num == 1) {
    // // ArrayList<String> testcase = new ArrayList<>();
    // StringBuilder testcase = new StringBuilder();
    // for (int i = 0; i < halfIndex; i++) {
    // testcase.append(encryptWithPublicKey(results.get(i),
    // Reciever.getPublicKey()));
    // testcase.append(" ");
    // }

    // Envelope envelope = new Envelope(EnvelopeType.envrv, Sender, Reciever);
    // String f2 = "primeNumber";
    // int endNum = (Integer.parseInt(arr[1]) + Integer.parseInt(arr[2])) / 2;
    // String newarr[] = { arr[0], arr[1], String.valueOf(endNum) };

    // testcase.append(encryptWithPublicKey(Arrays.toString(newarr),
    // Reciever.getPublicKey()));
    // testcase.append(" ");
    // testcase.append(encryptWithPublicKey(f2, Reciever.getPublicKey()));

    // // // System.out.println("Object is " + testcase.toString());
    // envelope.setEncryptedContent(testcase.toString());
    // return envelope;
    // } else {

    // // ArrayList<String> testcase = new ArrayList<>();
    // StringBuilder testcase = new StringBuilder();
    // for (int i = halfIndex; i < results.size(); i++) {
    // testcase.append(encryptWithPublicKey(results.get(i),
    // Reciever.getPublicKey()));
    // testcase.append(" ");
    // }
    // Envelope envelope = new Envelope(EnvelopeType.envrv, Sender, Reciever);
    // String f2 = "primeNumber";
    // int startNum = (Integer.parseInt(arr[1]) + Integer.parseInt(arr[2])) / 2 + 1;
    // String newarr[] = { arr[0], String.valueOf(startNum), arr[2] };
    // testcase.append(encryptWithPublicKey(Arrays.toString(newarr),
    // Reciever.getPublicKey()));
    // testcase.append(" ");
    // testcase.append(encryptWithPublicKey(f2, Reciever.getPublicKey()));
    // // // System.out.println("Object is " + testcase.toString());
    // envelope.setEncryptedContent(testcase.toString());
    // return envelope;
    // }
    // } catch (Exception e) {
    // e.printStackTrace();
    // // TODO: handle exception
    // }
    // return null;
    // }

    public static List<Envelope> createEnvelopeBasedOnOnes(Node sender, Node receiver, Envelope prevEnvelope,
            Envelope linkedEnvelope, int numOfOnes) {

        try {
            ArrayList<Envelope> envelopes = new ArrayList<>();
            ArrayList<String> results = new ArrayList<>();
            String decryptedArray[] = prevEnvelope.getEncryptedContent().split(" ");
            for (String decrypted : decryptedArray) {
                String decryptedString = decryptWithPrivateKey(decrypted, prevEnvelope.getReceivedBy().getPrivateKey());
                results.add(decryptedString);
            }

            String decryptedContentOfEnvelope = decryptWithPrivateKey(linkedEnvelope.getEncryptedContent(),
                    linkedEnvelope.getReceivedBy().getPrivateKey());
            String arr[] = decryptedContentOfEnvelope.substring(1, decryptedContentOfEnvelope.length() - 1).split(", ");
            StringBuilder testcase = new StringBuilder();

            if (numOfOnes == 1) {
                // Create a single envelope
                for (String result : results) {
                    testcase.append(encryptWithPublicKey(result, receiver.getPublicKey()));
                    testcase.append(" ");
                }

                String f2 = "primeNumber";
                testcase.append(encryptWithPublicKey(Arrays.toString(arr), receiver.getPublicKey()));
                testcase.append(" ");
                testcase.append(encryptWithPublicKey(f2, receiver.getPublicKey()));

                Envelope envelope = new Envelope(EnvelopeType.envrv, sender, receiver);
                envelope.setEncryptedContent(testcase.toString());
                envelopes.add(envelope);
                return envelopes;
            } else {
                // Create multiple envelopes
                int halfIndex = results.size() / 2;

                for (int part = 1; part <= 2; part++) {
                    StringBuilder partTestcase = new StringBuilder();
                    if (part == 1) {
                        for (int i = 0; i < halfIndex; i++) {
                            partTestcase.append(encryptWithPublicKey(results.get(i), receiver.getPublicKey()));
                            partTestcase.append(" ");
                        }
                        int endNum = (Integer.parseInt(arr[1]) + Integer.parseInt(arr[2])) / 2;
                        String newarr[] = { arr[0], arr[1], String.valueOf(endNum) };
                        partTestcase.append(encryptWithPublicKey(Arrays.toString(newarr), receiver.getPublicKey()));
                    } else {
                        for (int i = halfIndex; i < results.size(); i++) {
                            partTestcase.append(encryptWithPublicKey(results.get(i), receiver.getPublicKey()));
                            partTestcase.append(" ");
                        }
                        int startNum = (Integer.parseInt(arr[1]) + Integer.parseInt(arr[2])) / 2 + 1;
                        String newarr[] = { arr[0], String.valueOf(startNum), arr[2] };
                        partTestcase.append(encryptWithPublicKey(Arrays.toString(newarr), receiver.getPublicKey()));
                    }
                    partTestcase.append(" ");
                    partTestcase.append(encryptWithPublicKey("primeNumber", receiver.getPublicKey()));

                    Envelope envelope = new Envelope(EnvelopeType.envrv, sender, receiver);
                    envelope.setEncryptedContent(partTestcase.toString());
                    envelopes.add(envelope);
                }

                return envelopes;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
