import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.crypto.Cipher;

public class ComputationEnvelopeTask extends Envelope {

    public ComputationEnvelopeTask(EnvelopeType envType, Node sentBy, Node receivedBy) {
        super(envType, sentBy, receivedBy);
    }

    private static String decryptWithPrivateKey(String encryptedData, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] data = Base64.getDecoder().decode(encryptedData.getBytes());
        byte[] decrypt = cipher.doFinal(data);
        String decryptedData = new String(decrypt, StandardCharsets.UTF_8);
        return decryptedData;
    }

    public static Envelope createEnvelope(Node Sender, Node Reciever,
            List<Envelope> rRes) {
        List<String> results = new ArrayList<>();
        ArrayList<String> hashes = new ArrayList<>();
        for (Envelope e : rRes) {
            try {
                String decryptedArr[] = e.getEncryptedContent().split(" ");
                for (int i = 0; i < decryptedArr.length; i++) {
                    String decryptedContent = decryptWithPrivateKey(decryptedArr[i],
                            e.getReceivedBy().getPrivateKey());
                    results.add(decryptedContent);
                }
                hashes.add(e.calculateHash());
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        Envelope envelope = new Envelope(EnvelopeType.envcm, Sender, null);
        return envelope;
    }
}
