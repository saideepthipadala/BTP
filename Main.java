import java.util.ArrayList;

public class Main {

    private static ArrayList<Node> nodeList;

    public static void main(String[] args) {
        // Create 4 nodes
        Node nn1 = new Node("nn1", NodeType.nn);
        nodeList.add(nn1);
        Node cn1 = new Node("cn1", NodeType.cn);
        nodeList.add(cn1);
        Node cn2 = new Node("cn2", NodeType.cn);
        nodeList.add(cn2);
        Node cn3 = new Node("cn3", NodeType.cn);
        nodeList.add(cn3);
        Node cn4 = new Node("cn4", NodeType.cn);
        nodeList.add(cn4);
        // Start the nodes
        nn1.setFuncNo(1);
        nn1.start();
        cn1.start();
        cn2.start();
        cn3.start();
        cn4.start();

        // Wait for all nodes to finish
        try {
            nn1.join();
            cn1.join();
            cn2.join();
            cn3.join();
            cn4.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("All nodes have finished.");
    }
}
