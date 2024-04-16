import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    // Declare nn1 and cn1 as static variables
    public static Node nn1;
    public static Node cn1;
    public static Node cn2;
    public static Node cn3;
    public static Node cn4;
    public static HashMap<Envelope, ArrayList<Envelope>> DAG;

    public static ArrayList<Node> getAllNodes() {
        ArrayList<Node> nodes = new ArrayList<>();
        nodes.add(nn1);
        nodes.add(cn1);
        nodes.add(cn2);
        nodes.add(cn3);
        nodes.add(cn4);
        return nodes;
    }

    public static HashMap<Envelope, ArrayList<Envelope>> getDAG() {
        return DAG;
    }

    public static void main(String[] args) throws InterruptedException {

        ExecutorService executor = Executors.newCachedThreadPool();
        // Create 4 nodes
        nn1 = new Node("nn1", NodeType.nn);
        cn1 = new Node("cn1", NodeType.cn);
        cn2 = new Node("cn2", NodeType.cn);
        cn3 = new Node("cn3", NodeType.cn);
        cn4 = new Node("cn4", NodeType.cn);
        DAG = new HashMap<>();

        // Start the nodes
        nn1.setFuncNo(1);
        try {
            executor.submit(nn1).get();
        } catch (InterruptedException | ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // nn1.start();
        cn1.setFuncNo(2);
        cn3.setFuncNo(2);
        executor.submit(cn1);
        executor.submit(cn3);
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        executor = Executors.newCachedThreadPool();
        nn1.setFuncNo(3);
        executor.submit(nn1);

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        // cn1.setFuncNo(2);
        // cn3.setFuncNo(2);
        // cn1.start();
        // cn3.start();
        // try {
        // cn1.join();
        // cn3.join();
        // } catch (Exception e) {
        // e.printStackTrace();
        // // TODO: handle exception
        // }
        // nn1.setFuncNo(3);
        // nn1.resume();
        // // cn2.start();
        // // cn4.start();

        // // Wait for all nodes to finish
        // try {
        // nn1.join();
        // cn1.join();
        // cn2.join();
        // cn3.join();
        // cn4.join();
        // } catch (InterruptedException e) {
        // e.printStackTrace();
        // }

        System.out.println("All nodes have finished.");
    }

}
