import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashSet;
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
    private Node receiver;
    private String functionType;
    private NodeType nodeType;
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private TrustScore trustScore;
    private int[][] matrix;

    public Node(String nodeId) {
        this.nodeId = nodeId;
    }

    public Node(String nodeId, NodeType nodeType) {
        this.nodeId = nodeId;
        this.nodeType = nodeType;
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

    @Override
    public void run() {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        long startCpuTime = bean.getCurrentThreadCpuTime();

        Node[] nodes = Main.getNodes();
        LinkedHashMap<Envelope, ArrayList<Envelope>> DAG = Main.getDAG();
        System.out.println("Node " + nodeId + " is running.");

        int tasksize = 9000;
        switch (funcNo) {
            case 1:
                System.out.println("Executing case 1:");
                Node sender = getNodeById(this.nodeId, nodes);
                ArrayList<Node> li = new ArrayList<>();
                for (int i = 0; i < matrix[0].length; i++) {
                    if (matrix[0][i] == 1) {
                        li.add(nodes[i]);
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
                            "SquareRootFinding", subRangeStart, subRangeEnd, 1000);
                    System.out.println(
                            "Releasing subtask envelope for " + this.nodeId
                                    + " to find prime from " + subRangeStart + " to " + subRangeEnd + " with receiver "
                                    + receiver.getNodeId());
                    DAG.put(envelope, null);
                    index++;
                }
                break;

            case 2:
                System.out.println("Executing case 2:");
                ArrayList<Envelope> keys = new ArrayList<>(DAG.keySet());
                LinkedHashMap<Envelope, ArrayList<Envelope>> newEntries = new LinkedHashMap<>();
                String functionType = this.getFunctionType();
                System.out.println(functionType);
                for (Envelope e : keys) {
                    if (e.getReceivedBy().getNodeId().equals(nodeId)) {
                        Node newSender = e.getReceivedBy();
                        Node newReceiver = e.getSentBy();
                        try {
                            Envelope envelope = ComputationEnvelopeSubtask.createEnvelope(newSender, newReceiver, e,
                                    functionType);
                            ArrayList<Envelope> env = new ArrayList<>();
                            env.add(e);
                            newEntries.put(envelope, env);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                }
                DAG.putAll(newEntries);
                break;

            case 3:
                System.out.println("Executing case 3:");
                LinkedHashMap<Envelope, ArrayList<Envelope>> updates = new LinkedHashMap<>();
                for (Map.Entry<Envelope, ArrayList<Envelope>> entry : DAG.entrySet()) {
                    Envelope envelope = entry.getKey();
                    ArrayList<Envelope> associatedEnvelopes = entry.getValue();
                    if (envelope.getEnvType() == EnvelopeType.envcs) {
                        Node envSentBy = envelope.getSentBy();
                        int indexOfNode = getNodeIndexById(envSentBy.getNodeId(), nodes);
//                        System.out.println("index of node " + indexOfNode);
                        int noOfOnes = 0;
                        HashSet<Node> set1 = new HashSet<>();
                        for (int i = 1; i < matrix[indexOfNode].length; i++) {
                            if (matrix[indexOfNode][i] == 1) {
                                set1.add(nodes[i]);
                                noOfOnes++;
                            }
                        }
                        for (Node n : set1) {
                            System.out.println(n.getNodeId());
                        }
                        Node newSender = envelope.getReceivedBy();
//                        System.out.println(newSender.getNodeType());
                        for (Node receiver : set1) {
                            List<Envelope> envelopes = VerifyReleaseSubTaskEnvelope.createEnvelopeBasedOnOnes(newSender,
                                    receiver, envelope,
                                    associatedEnvelopes.get(0), noOfOnes);
                            for (Envelope e : envelopes) {
                                ArrayList<Envelope> envList = new ArrayList<>();
                                envList.add(associatedEnvelopes.get(0));
                                updates.put(e, envList);
                            }
                        }
                    }
                }
                DAG.putAll(updates);
                break;

            case 4:
                System.out.println("Node " + nodeId + " is running case 4");
                LinkedHashMap<Envelope, ArrayList<Envelope>> updates1 = new LinkedHashMap<>();
                for (Map.Entry<Envelope, ArrayList<Envelope>> entry : DAG.entrySet()) {
                    Envelope envelope = entry.getKey();
                    ArrayList<Envelope> associatedEnvelopes = entry.getValue();
                    if (envelope.getEnvType() == EnvelopeType.envrv
                            && envelope.getReceivedBy().getNodeId().equals(nodeId) && associatedEnvelopes != null) {
                        Node newSender = envelope.getReceivedBy();
                        Node newReceiver = envelope.getSentBy();
                        Envelope e = ComputationEnvelopeSubtask.createCsEnvelope(newSender, newReceiver, envelope);
                        ArrayList<Envelope> env = new ArrayList<>();
                        env.add(envelope);
                        updates1.put(e, env);
                    }
                }
                DAG.putAll(updates1);
                break;

            case 5:
                System.out.println("Node " + nodeId + " is running case 5");
                int countOfOnes = 0;
                for (int i = 0; i < matrix.length; i++) {
                    if (matrix[i][0] == 1) {
                        countOfOnes++;
                    }
                }
                List<Envelope> lastNEnvcs = getLastNEnvcs(DAG, countOfOnes);
                System.out.println(lastNEnvcs);
                Envelope envelope = ComputationEnvelopeTask.createEnvelope(nodes[0], null, lastNEnvcs);
                ArrayList<Envelope> associatedEnvelopes = new ArrayList<>(lastNEnvcs);
                DAG.put(envelope, associatedEnvelopes);
                break;

            case 6:
                System.out.println("Node " + nodeId + " is running case 6");
                Envelope e1 = ChallengeEnvelope.createEnvelope(this, nodes[0]);
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
                        Envelope newEnvelope = ProofEnvelope.createEnvelope(nodes[0], e.getSentBy());
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
                        Envelope newEnvelope = new Envelope(EnvelopeType.envvt, e.getReceivedBy(), nodes[0]);
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
