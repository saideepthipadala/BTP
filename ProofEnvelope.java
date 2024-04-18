public class ProofEnvelope {
    public static Envelope createEnvelope(Node Sender, Node Reciever) {
        return new Envelope(EnvelopeType.envpr, Sender, Reciever);
    }
}
