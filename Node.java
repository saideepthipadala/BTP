import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;

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
        HashMap<Envelope, ArrayList<Envelope>> DAG = Main.getDAG();
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
                            rangeEnd = 50;
                        } else {
                            rangeStart = 51;
                            rangeEnd = 100;
                        }

                        Envelope envelope = ReleaseSubTaskEnvelope.createEnvelope(nodes.get(0), nodes.get(i),
                                "SquareRootFinding", rangeStart, rangeEnd, 1000);
                        DAG.put(envelope, null);
                        System.out.println(DAG);

                        envelopeIndex++;
                    }
                }
                break;

            case 2:
                ArrayList<Envelope> keys = new ArrayList<>(DAG.keySet());
                
                HashMap<Envelope, ArrayList<Envelope>> newEntries = new HashMap<>();

                // Iterate over the copied key set
                for (Envelope e : keys) {
                    if (e.getReceivedBy().getNodeId().equals(nodeId)) {
                        Node newSender = e.getReceivedBy();
                        Node newReceiver = e.getSentBy();
                        try {
                            // Create a new envelope
                            Envelope envelope = ComputationEnvelope.createEnvelope(newSender, newReceiver, e);
                            ArrayList<Envelope> env = new ArrayList<>();
                            env.add(e);

                            // Add the new entry to the temporary map
                            newEntries.put(envelope, env);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                }

                // Merge the new entries with the original DAG
                DAG.putAll(newEntries);
                System.out.println(DAG);
                break;

            
            default:
                break;
        }

        try {
            Thread.sleep((long) (Math.random() * 1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Node " + nodeId + " has finished.");
    }

}
