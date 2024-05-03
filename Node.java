import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

enum NodeType {
    nn,
    cn
}

public class Node extends Thread {
    private String nodeId;
    private int funcNo;
    private NodeType nodeType;

    public void setTrustScore(TrustScore trustScore) {
        this.trustScore = trustScore;
    }

    private PrivateKey privateKey;
    private PublicKey publicKey;
    private TrustScore trustScore;

    int[][] matrix = {
            { 0, 1, 0, 1, 0 },
            { 1, 0, 1, 0, 0 },
            { 1, 0, 0, 0, 0 },
            { 0, 1, 0, 0, 1 },
            { 1, 0, 0, 0, 0 }
    };

    public Node(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public int getFuncNo() {
        return funcNo;
    }

    public void setFuncNo(int funcNo) {
        this.funcNo = funcNo;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public Node(String nodeId, NodeType nodeType) {
        this.nodeId = nodeId;
        this.nodeType = nodeType;
        // this.trustScore = trustScore;
        generateKeyPair();
    }

    private void generateKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            this.privateKey = keyPair.getPrivate();
            this.publicKey = keyPair.getPublic();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        ArrayList<Node> nodes = Main.getAllNodes();
        LinkedHashMap<Envelope, ArrayList<Envelope>> DAG = Main.getDAG();
        System.out.println("Node " + nodeId + " is running.");
        // System.out.println("My private Key:" + this.privateKey);

        switch (funcNo) {
            case 1:
                int envelopeIndex = 0;

                // HAVE TO MAKE IT PARALLEL EXECUTION
                for (int i = 0; i < nodes.size(); i++) {
                    if (matrix[0][i] == 1) {
                        int rangeStart, rangeEnd;
                        if (envelopeIndex == 0) {
                            rangeStart = 1;
                            rangeEnd = 1000;
                        } else {
                            rangeStart = 1001;
                            rangeEnd = 2000;
                        }

                        Envelope envelope = ReleaseSubTaskEnvelope.createEnvelope(nodes.get(0), nodes.get(i),
                                "SquareRootFinding", rangeStart, rangeEnd, 1000);
                        System.out.println("Releasing subtask envelope for " + nodes.get(i).getNodeId()
                                + " to find prime from " + rangeStart + " to " + rangeEnd);
                        DAG.put(envelope, null);
                        envelopeIndex++;
                    }
                }
                break;

            case 2:
                ArrayList<Envelope> keys = new ArrayList<>(DAG.keySet());
                LinkedHashMap<Envelope, ArrayList<Envelope>> newEntries = new LinkedHashMap<>();

                // Iterate over the copied key set
                for (Envelope e : keys) {
                    if (e.getReceivedBy().getNodeId().equals(nodeId)) {
                        Node newSender = e.getReceivedBy();
                        Node newReceiver = e.getSentBy();
                        try {
                            // Create a new envelope
                            Envelope envelope = ComputationEnvelopeSubtask.createEnvelope(newSender, newReceiver, e);
                            ArrayList<Envelope> env = new ArrayList<>();
                            env.add(e);

                            // Add the new entry to the temporary map
                            newEntries.put(envelope, env);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                }
                // System.out.println(this.setTrustScore());
                this.trustScore.setTimelinessSubtaskCompletion(this.trustScore.getTimelinessSubtaskCompletion() + 1);
                DAG.putAll(newEntries);
                break;

            case 3:
                LinkedHashMap<Envelope, ArrayList<Envelope>> updates = new LinkedHashMap<>(); // Temporary storage for
                                                                                              // updates

                for (Map.Entry<Envelope, ArrayList<Envelope>> entry : DAG.entrySet()) {
                    Envelope envelope = entry.getKey();
                    ArrayList<Envelope> associatedEnvelopes = entry.getValue();

                    if (envelope.getEnvType() == EnvelopeType.envcs) {
                        Node newSender = envelope.getReceivedBy();
                        // Node newReciever = envelope.getSentBy();
                        if (envelope.getSentBy().equals(nodes.get(1))) {
                            Envelope e = VerifyReleaseSubTaskEnvelope.createEnvelope(newSender, nodes.get(2),
                                    envelope, associatedEnvelopes.get(0));
                            ArrayList<Envelope> env = new ArrayList<>();
                            env.add(envelope);
                            updates.put(e, env);
                        } else {
                            Envelope e1 = VerifyReleaseSubTaskEnvelope.divideTaskAndCreateEnvelope(newSender,
                                    nodes.get(4), envelope, associatedEnvelopes.get(0), 1);
                            Envelope e2 = VerifyReleaseSubTaskEnvelope.divideTaskAndCreateEnvelope(newSender,
                                    nodes.get(1), envelope, associatedEnvelopes.get(0), 2);
                            ArrayList<Envelope> env = new ArrayList<>();
                            env.add(envelope);
                            updates.put(e1, env);
                            env.clear();
                            env.add(envelope);
                            updates.put(e2, env);
                        }
                    }
                }
                DAG.putAll(updates);
                break;

            case 4:
                System.out.println("Node " + nodeId + "is running case 4");
                LinkedHashMap<Envelope, ArrayList<Envelope>> updates1 = new LinkedHashMap<>();
                for (Map.Entry<Envelope, ArrayList<Envelope>> entry : DAG.entrySet()) {
                    Envelope envelope = entry.getKey();
                    ArrayList<Envelope> associatedEnvelopes = entry.getValue();
                    if (envelope.getEnvType() == EnvelopeType.envrv
                            && envelope.getReceivedBy().getNodeId().equals(nodeId) && associatedEnvelopes != null) {
                        Node newSender = envelope.getReceivedBy();
                        Node newReciever = envelope.getSentBy();
                        // System.out.println(envelope.getEncryptedContent());
                        Envelope e = ComputationEnvelopeSubtask.createCsEnvelope(newSender,
                                newReciever, envelope);
                        ArrayList<Envelope> env = new ArrayList<>();
                        env.add(envelope);
                        updates1.put(e, env);
                    }
                }
                this.trustScore.setTimelinessSubtaskCompletion(this.trustScore.getTimelinessSubtaskCompletion() + 1);
                DAG.putAll(updates1);
                break;

            case 5:
                System.out.println("Node " + nodeId + " is running case 5");
                List<Envelope> lastThreeEnvelopes = getLastThreeEnvelopes(DAG);
                Envelope envelope = ComputationEnvelopeTask.createEnvelope(nodes.get(0),
                        null, lastThreeEnvelopes);
                ArrayList<Envelope> associatedEnvelopes = new ArrayList<>(lastThreeEnvelopes);
                DAG.put(envelope, associatedEnvelopes);
                this.trustScore.setTaskcompletion(this.trustScore.getTaskcompletion() + 1);
                break;

            case 6:
                System.out.println("Node " + nodeId + " is running case 6");
                Envelope e1 = ChallengeEnvelope.createEnvelope(this, nodes.get(0));
                ArrayList<Envelope> en = new ArrayList<>();
                for (Envelope e : DAG.keySet()) {
                    if (e.getEnvType() == EnvelopeType.envcm) {
                        en.add(e);
                    }
                }
                DAG.put(e1, en);
                break;

            case 7:
                System.out.println("Node " + nodeId + " is running case 7");
                Map<Envelope, ArrayList<Envelope>> newDAG = new LinkedHashMap<>();
                for (Map.Entry<Envelope, ArrayList<Envelope>> entry : new LinkedHashMap<>(DAG).entrySet()) {
                    Envelope e = entry.getKey();
                    if (e.getEnvType() == EnvelopeType.envch) {
                        Envelope newEnvelope = ProofEnvelope.createEnvelope(nodes.get(0), e.getSentBy());
                        ArrayList<Envelope> envList = new ArrayList<>();
                        envList.add(e);
                        newDAG.put(newEnvelope, envList);
                    }
                }

                DAG.putAll(newDAG);
                break;

            case 8:
                System.out.println("Node " + nodeId + " is running case 8");
                Random rd = new Random();
                Map<Envelope, ArrayList<Envelope>> newDAGvt = new LinkedHashMap<>();
                for (Map.Entry<Envelope, ArrayList<Envelope>> entry : new LinkedHashMap<>(DAG).entrySet()) {
                    Envelope e = entry.getKey();
                    ArrayList<Envelope> ar = new ArrayList<>();
                    if (e.getEnvType() == EnvelopeType.envpr && e.getReceivedBy().getNodeId() == nodeId
                            && rd.nextBoolean()) {
                        Envelope newEnvelope = new Envelope(EnvelopeType.envvt, e.getReceivedBy(), nodes.get(0));
                        ar.add(e);
                        newDAGvt.put(newEnvelope, ar);
                        e.getReceivedBy().trustScore.setCorrectnessOfVerificationTask(
                                e.getReceivedBy().trustScore.getCorrectnessOfVerificationTask() - 1);
                    }
                }
                DAG.putAll(newDAGvt);
                break;

            default:
                break;
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Print DAG after node's execution
        System.out.println("Node " + nodeId + " has finished execution.");
        System.out.println("DAG: " + formatDAG(DAG));
    }

    private String formatDAG(LinkedHashMap<Envelope, ArrayList<Envelope>> DAG) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{\n");

        for (Map.Entry<Envelope, ArrayList<Envelope>> entry : DAG.entrySet()) {
            Envelope key = entry.getKey();
            List<Envelope> value = entry.getValue();
            stringBuilder.append("  ").append(key).append("=[");

            if (value != null) {
                for (int i = 0; i < value.size(); i++) {
                    stringBuilder.append(value.get(i));
                    if (i < value.size() - 1) {
                        stringBuilder.append(", ");
                    }
                }
            }

            stringBuilder.append("],\n");
        }

        if (!DAG.isEmpty()) {
            stringBuilder.setLength(stringBuilder.length() - 2); // Removing the last ", "
        }

        stringBuilder.append("\n}");
        return stringBuilder.toString();
    }

    private static List<Envelope> getLastThreeEnvelopes(
            LinkedHashMap<Envelope, ArrayList<Envelope>> dag) {
        List<Envelope> result = new ArrayList<>();

        if (dag.size() <= 3) {
            result.addAll(dag.keySet());
        } else {
            int startIndex = dag.size() - 3;
            int i = 0;
            for (Envelope entry : dag.keySet()) {
                if (i++ >= startIndex) {
                    result.add(entry);
                }
            }
        }

        return result;
    }
}
