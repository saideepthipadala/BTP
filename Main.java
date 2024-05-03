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

            // final long startTime = System.nanoTime();

            ExecutorService executor = Executors.newCachedThreadPool();
            nn1 = new Node("nn1", NodeType.nn);
            TrustScore nn1Trust = new TrustScore();
            nn1.setTrustScore(nn1Trust);
            cn1 = new Node("cn1", NodeType.cn);
            TrustScore cn1Trust = new TrustScore();
            cn1.setTrustScore(cn1Trust);
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

            for(Envelope e:DAG.keySet()){
                if(e.getEnvType() == EnvelopeType.envpr){
                    e.getReceivedBy().setFuncNo(8);
                    executor.submit(e.getReceivedBy());
                }
            }

            // Ensure all tasks complete and shutdown this executor
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.MINUTES);

            System.out.println("Trust Score of nn1 = " + nn1Trust.calculateTrustScore());
            System.out.println("Trust Score of cn1 = " + cn1Trust.calculateTrustScore());
            System.out.println("Trust Score of cn2 = " + cn2Trust.calculateTrustScore());
            System.out.println("Trust Score of cn3 = " + cn3Trust.calculateTrustScore());
            System.out.println("Trust Score of cn4 = " + cn4Trust.calculateTrustScore());

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        System.out.println("All nodes have finished.");
        long endTime = System.currentTimeMillis();
        System.out.println("Execution time: " + (endTime - startTime) + "ms");
    }

}
