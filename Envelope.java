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

    public String toString() {
        return this.envType.toString() + this.sentBy.getNodeId() + this.receivedBy.getNodeId();
    }

    public String getEncryptedContent() {
        return EncryptedContent;
    }

    public void setEncryptedContent(String encryptedContent) {
        EncryptedContent = encryptedContent;
    }
}
