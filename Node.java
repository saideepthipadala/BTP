import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

enum NodeType {
    nn,
    cn
}

public class Node extends Thread {
    private String nodeId;
    private int funcNo;
    private NodeType nodeType;
    private PrivateKey privateKey;
    private PublicKey publicKey;
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
                        System.out.println("Releasing subtask envelope for " + nodes.get(i).getNodeId());
                        int rangeStart, rangeEnd;
                        if (envelopeIndex == 0) {
                            rangeStart = 1;
                            rangeEnd = 25;
                        } else {
                            rangeStart = 26;
                            rangeEnd = 51;
                        }

                        Envelope envelope = ReleaseSubTaskEnvelope.createEnvelope(nodes.get(0), nodes.get(i),
                                "SquareRootFinding", rangeStart, rangeEnd, 1000);
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
                System.out.println("DAG after Case 3 execution :" + DAG);
                break;

            case 4:
                System.out.println("Node " + nodeId + "is running case 4");
                LinkedHashMap<Envelope, ArrayList<Envelope>> updates1 = new LinkedHashMap<>();
                for (Map.Entry<Envelope, ArrayList<Envelope>> entry : DAG.entrySet()) {
                    Envelope envelope = entry.getKey();
                    ArrayList<Envelope> associatedEnvelopes = entry.getValue();
                    if (envelope.getEnvType() == EnvelopeType.envrv
                            && envelope.getReceivedBy().getNodeId().equals(nodeId) && associatedEnvelopes != null) {
                        System.out.println(envelope);
                        Node newSender = envelope.getReceivedBy();
                        Node newReciever = envelope.getSentBy();
                        Envelope e = ComputationEnvelopeSubtask.createCsEnvelope(newSender, newReciever, envelope);
                        ArrayList<Envelope> env = new ArrayList<>();
                        env.add(envelope);
                        updates1.put(e, env);
                    }
                }
                DAG.putAll(updates1);
                break;

            case 5:
                System.out.println("Node " + nodeId + " is running case 5");
                List<Envelope> lastThreeEnvelopes = getLastThreeEnvelopes(DAG);
                System.out.println(lastThreeEnvelopes);
                Envelope envelope = ComputationEnvelopeTask.createEnvelope(nodes.get(0),
                        null, lastThreeEnvelopes);
                System.out.println(envelope);
                ArrayList<Envelope> associatedEnvelopes = new ArrayList<>(lastThreeEnvelopes);
                DAG.put(envelope, associatedEnvelopes);
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

                // Collect changes without modifying the original map
                for (Map.Entry<Envelope, ArrayList<Envelope>> entry : new LinkedHashMap<>(DAG).entrySet()) {
                    Envelope e = entry.getKey();
                    if (e.getEnvType() == EnvelopeType.envch) {
                        Envelope newEnvelope = ProofEnvelope.createEnvelope(nodes.get(0), e.getSentBy());
                        ArrayList<Envelope> envList = new ArrayList<>();
                        envList.add(e);
                        newDAG.put(newEnvelope, envList);
                    }
                }

                // Now apply the collected changes
                DAG.putAll(newDAG);
                break;
            default:
                break;
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Node " + nodeId + " has finished.");
        System.out.println("DAG after " + nodeId + " finished execution: " + DAG);
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
