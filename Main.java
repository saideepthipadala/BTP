import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    public static Node nn1;
    public static Node cn1;
    public static Node cn2;
    public static Node cn3;
    public static Node cn4;
    public static LinkedHashMap<Envelope, ArrayList<Envelope>> DAG;

    public static ArrayList<Node> getAllNodes() {
        ArrayList<Node> nodes = new ArrayList<>();
        nodes.add(nn1);
        nodes.add(cn1);
        nodes.add(cn2);
        nodes.add(cn3);
        nodes.add(cn4);
        return nodes;
    }

    public static LinkedHashMap<Envelope, ArrayList<Envelope>> getDAG() {
        return DAG;
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        try {

            final long startTime = System.nanoTime();

            ExecutorService executor = Executors.newCachedThreadPool();
            nn1 = new Node("nn1", NodeType.nn);
            TrustScore nn1trust = new TrustScore();
            nn1.setTrustScore(nn1trust);
            cn1 = new Node("cn1", NodeType.cn);
            TrustScore cn1trust = new TrustScore();
            cn1.setTrustScore(cn1trust);
            cn2 = new Node("cn2", NodeType.cn);
            TrustScore cn2Trust = new TrustScore();
            cn2.setTrustScore(cn2Trust);
            cn3 = new Node("cn3", NodeType.cn);
            TrustScore cn3Trust = new TrustScore();
            cn3.setTrustScore(cn3Trust);
            cn4 = new Node("cn4", NodeType.cn);
            TrustScore cn4Trust = new TrustScore();
            cn4.setTrustScore(cn4Trust);

            DAG = new LinkedHashMap<>();

            // Start nn1 node
            nn1.setFuncNo(1);
            executor.submit(nn1).get();

            // Start cn1 and cn3 nodes
            cn1.setFuncNo(2);
            cn3.setFuncNo(2);
            executor.submit(cn1).get();
            executor.submit(cn3).get();
            nn1.setFuncNo(3);
            executor.submit(nn1).get();

            cn1.setFuncNo(4);
            cn2.setFuncNo(4);
            cn4.setFuncNo(4);
            executor.submit(cn1).get();
            executor.submit(cn2).get();
            executor.submit(cn4).get();

            nn1.setFuncNo(5);
            executor.submit(nn1).get();

            for (Node n : getAllNodes()) {
                if (n.getNodeType() != NodeType.nn) {
                    n.setFuncNo(6);
                    executor.submit(n).get();
                }
            }

            nn1.setFuncNo(7);
            executor.submit(nn1).get();

            // Ensure all tasks complete and shutdown this executor
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.MINUTES);

            final long endTime = System.nanoTime();

            final long Duration = endTime - startTime;

            System.out.println("Duration: " + Duration);

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        System.out.println("All nodes have finished.");
    }

}
