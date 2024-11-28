import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import javax.crypto.Cipher;

enum NodeType {
    nn,
    cn
}

public class Node extends Thread {
    private String nodeId;
    private int funcNo;
    private Node receiver;
    private String functionType;
    private NodeType nodeType;
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private TrustScore trustScore;
    private int[][] matrix;
    private ExecutorService executor;

    public Node(String nodeId) {
        this.nodeId = nodeId;
    }

    public Node(String nodeId, NodeType nodeType, ExecutorService executor) {
        this.nodeId = nodeId;
        this.nodeType = nodeType;
        this.executor = executor;
        generateKeyPair();
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

    public TrustScore getTrustScore() {
        return trustScore;
    }

    public void setTrustScore(TrustScore trustScore) {
        this.trustScore = trustScore;
    }

    public int[][] getMatrix() {
        return matrix;
    }

    public void setMatrix(int[][] matrix) {
        this.matrix = matrix;
    }

    public Node getReceiver() {
        return receiver;
    }

    public void setReceiver(Node receiver) {
        this.receiver = receiver;
    }

    public String getFunctionType() {
        return functionType;
    }

    public void setFunctionType(String functionType) {
        this.functionType = functionType;
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

    private Envelope getLastEnvelope(LinkedHashMap<Envelope, ArrayList<Envelope>> DAG) {
        Envelope lastEnvelope = null;
        for (Envelope envelope : DAG.keySet()) {
            lastEnvelope = envelope;
        }
        return lastEnvelope;
    }

    private static String decryptWithPrivateKey(String encryptedData, PrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            String[] parts = encryptedData.split("\\|");
            StringBuilder decryptedResult = new StringBuilder();

            for (String part : parts) {
                byte[] data = Base64.getDecoder().decode(part.trim());
                byte[] decrypt = cipher.doFinal(data);
                decryptedResult.append(new String(decrypt, StandardCharsets.UTF_8)).append(" ");
            }

            return decryptedResult.toString().trim();
        } catch (IllegalArgumentException e) {
            System.err.println("Error decoding Base64 string: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("Error during decryption: " + e.getMessage());
            return null;
        }
    }

    public static boolean verifyEnvelope(ComputationEnvelopeSubtask envelope) {
        String[] proofs = envelope.getProof().split("\\|");
        Pairing pairing = envelope.getPairings();
        Element g1 = envelope.getG1();
        Element g2 = envelope.getG2();
        Element publicKey = envelope.getPublicKey();


        for (int i = 0; i < proofs.length; i++) {
            String proof = proofs[i];
            String[] parts = proof.split("->Signature:");
            if (parts.length != 2) {
                System.out.println("Invalid proof format for proof " + (i + 1));
                return false;
            }

            int number = Integer.parseInt(parts[0]);
            String encodedSignature = parts[1];

            try {
                // Decode the signature
                byte[] signatureBytes = Base64.getDecoder().decode(encodedSignature);
                Element signature = pairing.getG1().newElementFromBytes(signatureBytes);

                // Hash the number
                byte[] numberBytes = Integer.toString(number).getBytes(StandardCharsets.UTF_8);
                Element hashedNumber = pairing.getG1().newElementFromHash(numberBytes, 0, numberBytes.length);

                // Verify the pairing
                Element leftSide = pairing.pairing(signature, g2);
                Element rightSide = pairing.pairing(hashedNumber, publicKey);

                if (!leftSide.isEqual(rightSide)) {
                    System.out.println("Proof verification failed for number: " + number);
                    return false;
                }

                // System.out.println("Proof " + (i + 1) + " verified successfully");
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                // System.out.println("Base64 decoding failed for proof " + (i + 1) + ": " +
                // e.getMessage());
                // System.out.println("Problematic encoded signature: " + encodedSignature);
                // // Instead of returning false, we'll skip this proof and continue with the
                // next
                // // one
                // continue;
            } catch (Exception e) {
                System.out.println("Error verifying proof " + (i + 1) + ": " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }



    @Override
    public void run() {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        long startCpuTime = bean.getCurrentThreadCpuTime();

        Node[] nodes = Main.getNodes();
        LinkedHashMap<Envelope, ArrayList<Envelope>> DAG = Main.getDAG();
        System.out.println("Node " + nodeId + " is running.");

        int tasksize = 10000;
        switch (funcNo) {
            case 1:
                System.out.println("Executing case 1:");
                Node sender = getNodeById(this.nodeId, nodes);
                ArrayList<Node> li = new ArrayList<>();

                for (int i = 0; i < matrix[0].length; i++) {
                    for (int j = 0; j < matrix.length; j++) {
                        if (matrix[i][j] == 1 && i != matrix.length - 1) {
                            li.add(nodes[j]);
                        }
                    }
                }

                int rangeStart = 1;
                int rangeEnd = tasksize;
                int count = li.size();
                int subRangeSize = (rangeEnd - rangeStart + 1) / count;
                int index = 0;


                for (Node receiver : li) {
                    int subRangeStart = rangeStart + index * subRangeSize;
                    int subRangeEnd = (index == count - 1) ? rangeEnd : subRangeStart + subRangeSize - 1;

                    Envelope envelope = ReleaseSubTaskEnvelope.createEnvelope(sender, receiver,
                            "primalityTest", subRangeStart, subRangeEnd, 1000);
                    System.out.println("Releasing subtask envelope for " + this.nodeId
                            + " to find prime from " + subRangeStart + " to " + subRangeEnd + " with receiver "
                            + receiver.getNodeId());

                    Envelope previousEnvelope = getLastEnvelope(DAG);
                    System.out.println("Previous Envelope: " + previousEnvelope);

                    ArrayList<Envelope> envelopeList = new ArrayList<>();
                    if (previousEnvelope != null) {
                        envelopeList.add(previousEnvelope);
                    }

                    DAG.put(envelope, envelopeList.isEmpty() ? null : envelopeList);
                    receiver.setFuncNo(2);
                    try {
                        executor.submit(receiver).get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                    index++;
                }
                break;

            case 2:
                System.out.println("Executing case 2:");
                ArrayList<Envelope> keys = new ArrayList<>(DAG.keySet());
                LinkedHashMap<Envelope, ArrayList<Envelope>> newEntries = new LinkedHashMap<>();
                System.out.println("keys: " + keys);
                System.out.println("nodeId: " + this.nodeId);
                for (Envelope e : keys) {
                    if (e.getReceivedBy().getNodeId().equals(nodeId)) {
                        Node newSender = e.getReceivedBy();
                        Node newReceiver = e.getSentBy();
                        System.out.println(DAG.toString());
                        ArrayList<Envelope> previousEnvelopes = DAG.get(e);
                        // System.out.println("previous Envelopes " + previousEnvelopes);
                        if (previousEnvelopes != null && !previousEnvelopes.isEmpty()) {

                            Envelope lastEnvelope = previousEnvelopes.get(0);
                            if (lastEnvelope != null && lastEnvelope instanceof ComputationEnvelopeSubtask) {
                                ComputationEnvelopeSubtask en = (ComputationEnvelopeSubtask) lastEnvelope;
                                Boolean bool = verifyEnvelope(en);
                                if(bool){
                                    System.out.println("Verified proof of result by " + en.getSentBy().getNodeId());
                                }
                                // System.out.println("Proof verification " + bool);
                                try {
                                    Envelope newEnvelope = ComputationEnvelopeSubtask.createCsEnvelope(newSender,
                                            newReceiver, e);
                                    ArrayList<Envelope> env = new ArrayList<>();
                                    env.add(e);
                                    newEntries.put(newEnvelope, env);
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                            }
                        } else {
                            System.out
                                    .println("No previous envelope found for this node. Proceeding with new subtask.");
                            try {
                                Envelope newEnvelope = ComputationEnvelopeSubtask.createCsEnvelope(newSender,
                                        newReceiver, e);
                                ArrayList<Envelope> env = new ArrayList<>();
                                env.add(e);
                                newEntries.put(newEnvelope, env);
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                }

                DAG.putAll(newEntries);
                System.out.println("DAG: " + formatDAG(DAG));
                break;

            default:
                break;
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Node " + this.getNodeId() + " has finished execution.");
        System.out.println("DAG: " + formatDAG(DAG));

        long endCpuTime = bean.getCurrentThreadCpuTime();
        System.out.println("Node " + nodeId + " CPU time: " + (endCpuTime - startCpuTime) / 1_000_000 + "ms");
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
            stringBuilder.setLength(stringBuilder.length() - 2);
        }
        stringBuilder.append("\n}");
        return stringBuilder.toString();
    }

    private static List<Envelope> getLastNEnvcs(LinkedHashMap<Envelope, ArrayList<Envelope>> dag, int n) {
        List<Envelope> result = new ArrayList<>();
        if (n <= 0) {
            return result;
        }
        int envcsCount = 0;
        for (Envelope entry : new ArrayList<>(dag.keySet()).subList(Math.max(dag.size() - n, 0), dag.size())) {
            if (entry.getEnvType() == EnvelopeType.envcs) {
                result.add(entry);
                envcsCount++;
                if (envcsCount == n) {
                    break;
                }
            }
        }
        return result;
    }

    public static Node getNodeById(String nodeId, Node[] nodeList) {
        for (Node node : nodeList) {
            if (node.getNodeId().equals(nodeId)) {
                return node;
            }
        }
        return null;
    }

    public static int getNodeIndexById(String nodeId, Node[] nodeList) {
        for (int i = 0; i < nodeList.length; i++) {
            if (nodeList[i].getNodeId().equals(nodeId)) {
                return i;
            }
        }
        return -1;
    }
}
