public class ChallengeEnvelope {
    public static Envelope createEnvelope(Node sender,Node receiver){
        return new Envelope(EnvelopeType.envch, sender, receiver);
    }
}
