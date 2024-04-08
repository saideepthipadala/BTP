import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

public class ReleaseSubTaskEnvelope extends Envelope {

    private String functionType;
    private int rangeStart;
    private int rangeEnd;
    private long timeForSubTaskCompletion;
    private String encryptedFunctionType;
    private String encryptedRangeStart;
    private String encryptedRangeEnd;


    private static byte[] generateSignature(PrivateKey secretKey, PublicKey publicKey,Envelope envelope) {
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
        this.functionType = functionType;
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
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

    private static byte[] decryptWithPrivateKey(byte[] encryptedData, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(encryptedData);
    }

    static byte[] createEnvelope(Node sender, Node receiver, String functionType, int rangeStart,
            int rangeEnd, long time) {
        ReleaseSubTaskEnvelope envelope = new ReleaseSubTaskEnvelope(EnvelopeType.envrv, sender, receiver);

        envelope.functionType = functionType;
        envelope.rangeStart = rangeStart;
        envelope.rangeEnd = rangeEnd;
        envelope.timeForSubTaskCompletion = time;

        try {
            // Encrypt functionType, rangeStart, and rangeEnd before creating the envelope
            PublicKey publicKey = receiver.getPublicKey();
            byte[] encryptedFunctionType = encryptWithPublicKey(functionType.getBytes(StandardCharsets.UTF_8),
                    publicKey);
            System.out.println(encryptedFunctionType);

            byte[] encryptedRangeStart = encryptWithPublicKey(
                    String.valueOf(rangeStart).getBytes(StandardCharsets.UTF_8), publicKey);
            byte[] encryptedRangeEnd = encryptWithPublicKey(String.valueOf(rangeEnd).getBytes(StandardCharsets.UTF_8),
                    publicKey);
            // Store the encrypted data in the envelope
            envelope.encryptedFunctionType = Base64.getEncoder().encodeToString(encryptedFunctionType);
            envelope.encryptedRangeStart = Base64.getEncoder().encodeToString(encryptedRangeStart);
            envelope.encryptedRangeEnd = Base64.getEncoder().encodeToString(encryptedRangeEnd);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return generateSignature(sender.getPrivateKey(), receiver.getPublicKey(), envelope);
    }

    // public void processTask(String functionType) {
    // try {
    // // Decrypt encrypted data before processing
    // PrivateKey privateKey =receivedBy.getPrivateKey();
    // byte[] decryptedFunctionType =
    // decryptWithPrivateKey(Base64.getDecoder().decode(encryptedFunctionType),
    // privateKey);
    // byte[] decryptedRangeStart =
    // decryptWithPrivateKey(Base64.getDecoder().decode(encryptedRangeStart),
    // privateKey);
    // byte[] decryptedRangeEnd =
    // decryptWithPrivateKey(Base64.getDecoder().decode(encryptedRangeEnd),
    // privateKey);

    // // Convert decrypted bytes back to strings
    // String decryptedFuncType = new String(decryptedFunctionType,
    // StandardCharsets.UTF_8);

    // // Process the task using the decrypted data
    // if (functionType.equals(decryptedFuncType)) {
    // int rangeStart = Integer.parseInt(new String(decryptedRangeStart,
    // StandardCharsets.UTF_8));
    // int rangeEnd = Integer.parseInt(new String(decryptedRangeEnd,
    // StandardCharsets.UTF_8));

    // if ("SquareRootFinding".equals(functionType)) {
    // for (int i = rangeStart; i <= rangeEnd; i++) {
    // double squareRoot = Math.sqrt(i);
    // System.out.println("Square root of " + i + " is: " + squareRoot);
    // }
    // }
    // } else {
    // System.out.println("Function type mismatch: " + functionType);
    // }
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }
}
