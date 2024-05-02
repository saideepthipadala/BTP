import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.UUID;

enum EnvelopeType {
    envrv,
    envcs,
    envvs,
    envcm,
    envch,
    envpr,
    envvt
}

public class Envelope {
    private EnvelopeType envType;
    private Node sentBy;
    private Node receivedBy;
    private String envId;
    private String sign;
    private String hashOfPrevEnvelope;
    private String EncryptedContent;
    private String testcase;

    public Envelope(EnvelopeType envType, Node sentBy, Node receivedBy) {
        this.envType = envType;
        this.sentBy = sentBy;
        this.receivedBy = receivedBy;
        this.envId = generateEnvId();
    }

    public String getHashOfPrevEnvelope() {
        return hashOfPrevEnvelope;
    }

    public void setHashOfPrevEnvelope(String hashOfPrevEnvelope) {
        this.hashOfPrevEnvelope = hashOfPrevEnvelope;
    }

    public EnvelopeType getEnvType() {
        return envType;
    }

    public void setEnvType(EnvelopeType envType) {
        this.envType = envType;
    }

    public Node getSentBy() {
        return sentBy;
    }

    public void setSentBy(Node sentBy) {
        this.sentBy = sentBy;
    }

    public Node getReceivedBy() {
        return receivedBy;
    }

    public void setReceivedBy(Node receivedBy) {
        this.receivedBy = receivedBy;
    }

    public String getEnvId() {
        return envId;
    }

    public void setEnvId(String envId) {
        this.envId = envId;
    }

    private static String generateEnvId() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        // Assuming receivedBy might be null and you want to include its nodeId if it's
        // not null.
        String receivedById = (this.receivedBy != null) ? this.receivedBy.getNodeId() : "";
        return this.envType.toString() + this.sentBy.getNodeId() + receivedById;
    }

    public String getEncryptedContent() {
        return EncryptedContent;
    }

    public void setEncryptedContent(String encryptedContent) {
        EncryptedContent = encryptedContent;
    }

    private String rootr;
    private int numL;
    private ArrayList<String> csL;

    public String getRootr() {
        return rootr;
    }

    public void setRootr(String rootr) {
        this.rootr = rootr;
    }

    public int getNumL() {
        return numL;
    }

    public void setNumL(int numL) {
        this.numL = numL;
    }

    public ArrayList<String> getCsL() {
        return csL;
    }

    public void setCsL(ArrayList<String> csL) {
        this.csL = csL;
    }

    public String calculateHash() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(this.getEncryptedContent().getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Cannot find SHA-256 algorithm", e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1)
                hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
