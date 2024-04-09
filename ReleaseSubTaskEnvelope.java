import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;

import java.util.ArrayList;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

public class ReleaseSubTaskEnvelope extends Envelope {

    private long timeForSubTaskCompletion;
    private String encryptedFunctionType;
    private String encryptedRangeStart;
    private String encryptedRangeEnd;

    public long getTimeForSubTaskCompletion() {
        return timeForSubTaskCompletion;
    }

    public void setTimeForSubTaskCompletion(long timeForSubTaskCompletion) {
        this.timeForSubTaskCompletion = timeForSubTaskCompletion;
    }

    public String getEncryptedFunctionType() {
        return encryptedFunctionType;
    }

    public void setEncryptedFunctionType(String encryptedFunctionType) {
        this.encryptedFunctionType = encryptedFunctionType;
    }

    public String getEncryptedRangeStart() {
        return encryptedRangeStart;
    }

    public void setEncryptedRangeStart(String encryptedRangeStart) {
        this.encryptedRangeStart = encryptedRangeStart;
    }

    public String getEncryptedRangeEnd() {
        return encryptedRangeEnd;
    }

    public void setEncryptedRangeEnd(String encryptedRangeEnd) {
        this.encryptedRangeEnd = encryptedRangeEnd;
    }

    private static byte[] generateSignature(PrivateKey secretKey, PublicKey publicKey, Envelope envelope) {
        try {
            // Obtain the encoded bytes of the secret key
            byte[] secretKeyBytes = secretKey.getEncoded();

            // Initialize the HMAC-SHA256 algorithm with the secret key
            javax.crypto.Mac sha256_HMAC = javax.crypto.Mac.getInstance("HmacSHA256");
            javax.crypto.spec.SecretKeySpec secretKeySpec = new javax.crypto.spec.SecretKeySpec(secretKeyBytes,
                    "HmacSHA256");
            sha256_HMAC.init(secretKeySpec);
            byte[] hash = sha256_HMAC.doFinal(envelope.toString().getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encode(hash);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public ReleaseSubTaskEnvelope(EnvelopeType envType, Node sentBy, Node receivedBy) {
        super(envType, sentBy, receivedBy);
    }

    public ReleaseSubTaskEnvelope(EnvelopeType envType, Node sentBy, Node receivedBy, String functionType,
            int rangeStart,
            int rangeEnd, long timeForSubTaskCompletion, String encryptedFunctionType, String encryptedRangeStart,
            String encryptedRangeEnd) {
        super(envType, sentBy, receivedBy);
        // this.functionType = functionType;
        // this.rangeStart = rangeStart;
        // this.rangeEnd = rangeEnd;
        this.timeForSubTaskCompletion = timeForSubTaskCompletion;
        this.encryptedFunctionType = encryptedFunctionType;
        this.encryptedRangeStart = encryptedRangeStart;
        this.encryptedRangeEnd = encryptedRangeEnd;
    }

    private static byte[] encryptWithPublicKey(byte[] data, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    // private static byte[] decryptWithPrivateKey(byte[] encryptedData, PrivateKey privateKey) throws Exception {
    //     Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
    //     cipher.init(Cipher.DECRYPT_MODE, privateKey);
    //     return cipher.doFinal(encryptedData);
    // }

    static Envelope createEnvelope(Node sender, Node receiver, String functionType, int rangeStart,
            int rangeEnd, long time) {
        ReleaseSubTaskEnvelope envelope = new ReleaseSubTaskEnvelope(EnvelopeType.envrv, sender, receiver);
        envelope.setHashOfPrevEnvelope(null);
        try {
            // Encrypt functionType, rangeStart, and rangeEnd before creating the envelope
            PublicKey publicKey = receiver.getPublicKey();
            ArrayList<String> content = new ArrayList<>();
            content.add(functionType);
            content.add(String.valueOf(rangeStart));
            content.add(String.valueOf(rangeEnd));
            byte[] encryptedContent = encryptWithPublicKey(content.toString().getBytes(StandardCharsets.UTF_8),
                    publicKey);
            envelope.setEncryptedContent(Base64.getEncoder().encodeToString(encryptedContent));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return envelope;
    }

}
