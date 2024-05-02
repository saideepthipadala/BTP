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
        long startTime = System.currentTimeMillis();

        try {
            ExecutorService executor = Executors.newCachedThreadPool();
            nn1 = new Node("nn1", NodeType.nn);
            cn1 = new Node("cn1", NodeType.cn);
            cn2 = new Node("cn2", NodeType.cn);
            cn3 = new Node("cn3", NodeType.cn);
            cn4 = new Node("cn4", NodeType.cn);
            DAG = new LinkedHashMap<>();

            // System.out.println("Task is to find prime numbers between 1-51");

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
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        System.out.println("All nodes have finished.");
        long endTime = System.currentTimeMillis();
        System.out.println("Execution time: " + (endTime - startTime) + "ms");
    }

}
