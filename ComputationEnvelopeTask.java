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
        // StringBuilder results = new StringBuilder();
        ArrayList<String> hashes = new ArrayList<>();
        for (Envelope e : rRes) {
            // System.out.println("Encrypted Content:" + e.getEncryptedContent());
            try {
                String decryptedArr[] = e.getEncryptedContent().split(" ");
                for (int i = 0; i < decryptedArr.length; i++) {
                    String decryptedContent = decryptWithPrivateKey(decryptedArr[i],
                            e.getReceivedBy().getPrivateKey());
                    // System.out.println("Decrypted Content:" + decryptedContent);
                    results.add(decryptedContent);
                }

                hashes.add(e.calculateHash());
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        MerkleTree tree = new MerkleTree(results);
        Envelope envelope = new Envelope(EnvelopeType.envcm, Sender, null);
        envelope.setRootr(tree.getRootHash());
        envelope.setNumL(rRes.size());
        envelope.setCsL(hashes);
        System.out.println(tree.getRootHash());
        return envelope;
    }
}
