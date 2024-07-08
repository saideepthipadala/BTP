// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.LinkedHashMap;
// import java.util.Scanner;
// import java.util.concurrent.ExecutionException;
// import java.util.concurrent.ExecutorService;
// import java.util.concurrent.Executors;
// import java.util.concurrent.TimeUnit;
// import java.util.UUID;

// public class Main {

//     // public static Node nn1;
//     // public static Node cn1;
//     // public static Node cn2;
//     // public static Node cn3;
//     // public static Node cn4;
//     public static LinkedHashMap<Envelope, ArrayList<Envelope>> DAG;
//     public static Node nodeList[];
//     public static int[][] connectionMatrix;

//     public static Node[] getNodes() {
//         return nodeList;
//     }

//     // public static ArrayList<Node> getAllNodes() {
//     // ArrayList<Node> nodes = new ArrayList<>();
//     // nodes.add(nn1);
//     // nodes.add(cn1);
//     // nodes.add(cn2);
//     // nodes.add(cn3);
//     // nodes.add(cn4);
//     // return nodes;
//     // }

//     public static void printMatrix(int[][] matrix) {
//         for (int i = 0; i < matrix.length; i++) {
//             for (int j = 0; j < matrix[i].length; j++) {
//                 System.out.print(matrix[i][j] + " ");
//             }
//             System.out.println();
//         }
//     }

//     public static LinkedHashMap<Envelope, ArrayList<Envelope>> getDAG() {
//         return DAG;
//     }

//     public static void main(String[] args) throws InterruptedException, ExecutionException {
//         long startTime = System.currentTimeMillis();

//         try {
//             Scanner sc = new Scanner(System.in);
//             System.out.println("Enter number of nodes in the network:");
//             int numNodes = sc.nextInt();
//             nodeList = new Node[numNodes];
//             // UUID uuid = UUID.randomUUID();
//             Node nnNode = new Node("nn" + 1, NodeType.nn);
//             nodeList[0] = nnNode;

//             for (int i = 1; i < numNodes; i++) {
//                 Node newNode = new Node("cn" + i, NodeType.cn);
//                 nodeList[i] = newNode;
//             }

//             int[][] matrix = new int[numNodes][numNodes];
//             System.out.println("Enter the matrix (each entry 0 or 1):");
//             for (int i = 0; i < numNodes; i++) {
//                 for (int j = 0; j < numNodes; j++) {
//                     System.out.println("Is there an edge between " + i + " and " + j);
//                     matrix[i][j] = sc.nextInt();
//                 }
//             }

//             for (int i = 0; i < nodeList.length; i++) {
//                 nodeList[i].setMatrix(matrix);
//             }
//             printMatrix(matrix);
//             // final long startTime = System.nanoTime();

//             ExecutorService executor = Executors.newCachedThreadPool();
//             // nn1 = new Node("nn1", NodeType.nn);
//             // TrustScore nn1Trust = new TrustScore();
//             // nn1.setTrustScore(nn1Trust);
//             // cn1 = new Node("cn1", NodeType.cn);
//             // TrustScore cn1Trust = new TrustScore();
//             // cn1.setTrustScore(cn1Trust);
//             // cn2 = new Node("cn2", NodeType.cn);
//             // TrustScore cn2Trust = new TrustScore();
//             // cn2.setTrustScore(cn2Trust);
//             // cn3 = new Node("cn3", NodeType.cn);
//             // TrustScore cn3Trust = new TrustScore();
//             // cn3.setTrustScore(cn3Trust);
//             // cn4 = new Node("cn4", NodeType.cn);
//             // TrustScore cn4Trust = new TrustScore();
//             // cn4.setTrustScore(cn4Trust);

//             DAG = new LinkedHashMap<>();
//             LinkedHashMap<Node, Node> nodemap = new LinkedHashMap<>();
//             for (int i = 0; i < matrix.length; i++) {
//                 for (int j = 0; j < matrix.length; j++) {
//                     if (matrix[i][j] == 1) {
//                         nodemap.put(nodeList[i], nodeList[j]);
//                     }
//                 }
//             }
//             int noOfConnections = nodemap.size();
//             System.out.println("No of connections: " + noOfConnections);
//             // Start nn1 node
//             // nn1.setFuncNo(1);
//             nnNode.setFuncNo(1);
//             executor.submit(nnNode).get();
//             for (int i = 0; i < matrix[0].length; i++) {
//                 if (matrix[0][i] == 1) {
//                     noOfConnections--;
//                 }
//             }
//             System.out.println("No of connections: " + noOfConnections);
//             for (int i = 0; i < matrix.length; i++) {
//                 if (matrix[0][i] == 1) {
//                     System.out.println("index is " + i);
//                     if (noOfConnections <= 1) {
//                         nodeList[i].setFunctionType("");
//                         nodeList[i].setFuncNo(2);
//                         executor.submit(nodeList[i]).get();
//                     } else {
//                         nodeList[i].setFunctionType("sqrt");
//                         nodeList[i].setFuncNo(2);
//                         executor.submit(nodeList[i]).get();
//                     }

//                 }
//             }

//             nodeList[0].setFuncNo(3);
//             executor.submit(nodeList[0]).get();

//             for (int i = 1; i < matrix.length; i++) {
//                 for (int j = 1; j < matrix.length; j++) {
//                     // nodeList[j]
//                     if (matrix[i][j] == 1) {
//                         nodeList[i].setFuncNo(4);
//                         executor.submit(nodeList[i]).get();
//                     }
//                 }
//             }

//             nodeList[0].setFuncNo(5);
//             executor.submit(nodeList[0]).get();

//             // for (Node n : getAllNodes()) {
//             // if (n.getNodeType() != NodeType.nn) {
//             // n.setFuncNo(6);
//             // executor.submit(n).get();
//             // }
//             // }

//             // nn1.setFuncNo(7);
//             // executor.submit(nn1).get();

//             // for (Envelope e : DAG.keySet()) {
//             // if (e.getEnvType() == EnvelopeType.envpr) {
//             // e.getReceivedBy().setFuncNo(8);
//             // executor.submit(e.getReceivedBy());
//             // }
//             // }

//             // // Ensure all tasks complete and shutdown this executor
//             executor.shutdown();
//             executor.awaitTermination(1, TimeUnit.MINUTES);

//             // System.out.println("Trust Score of nn1 = " + nn1Trust.calculateTrustScore());
//             // System.out.println("Trust Score of cn1 = " + cn1Trust.calculateTrustScore());
//             // System.out.println("Trust Score of cn2 = " + cn2Trust.calculateTrustScore());
//             // System.out.println("Trust Score of cn3 = " + cn3Trust.calculateTrustScore());
//             // System.out.println("Trust Score of cn4 = " + cn4Trust.calculateTrustScore());
//             sc.close();
//         } catch (InterruptedException | ExecutionException e) {
//             e.printStackTrace();
//         }

//         System.out.println("All nodes have finished.");
//         long endTime = System.currentTimeMillis();
//         System.out.println("Execution time: " + (endTime - startTime) + "ms");
//     }

//     public static Node[] getNodeList() {
//         return nodeList;
//     }

//     // public static int[][] getConnectionMatrix() {
//     // return connectionMatrix;
//     // }

// }

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class Main {

    public static LinkedHashMap<Envelope, ArrayList<Envelope>> DAG;
    public static Node[] nodeList;
    public static int[][] connectionMatrix;

    public static Node[] getNodes() {
        return nodeList;
    }

    public static void printMatrix(int[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static LinkedHashMap<Envelope, ArrayList<Envelope>> getDAG() {
        return DAG;
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        long startTime = System.currentTimeMillis();

        // Get the thread bean to measure CPU time


        try {
            Scanner sc = new Scanner(System.in);
            System.out.println("Enter number of nodes in the network:");
            int numNodes = sc.nextInt();
            nodeList = new Node[numNodes];
            Node nnNode = new Node("nn" + 1, NodeType.nn);
            nodeList[0] = nnNode;

            for (int i = 1; i < numNodes; i++) {
                Node newNode = new Node("cn" + i, NodeType.cn);
                nodeList[i] = newNode;
            }

            int[][] matrix = new int[numNodes][numNodes];
            System.out.println("Enter the matrix (each entry 0 or 1):");
            for (int i = 0; i < numNodes; i++) {
                for (int j = 0; j < numNodes; j++) {
                    System.out.println("Is there an edge between " + i + " and " + j);
                    matrix[i][j] = sc.nextInt();
                }
            }

            for (int i = 0; i < nodeList.length; i++) {
                nodeList[i].setMatrix(matrix);
            }
            printMatrix(matrix);

            ExecutorService executor = Executors.newCachedThreadPool();

            DAG = new LinkedHashMap<>();
            LinkedHashMap<Node, Node> nodemap = new LinkedHashMap<>();
            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix.length; j++) {
                    if (matrix[i][j] == 1) {
                        nodemap.put(nodeList[i], nodeList[j]);
                    }
                }
            }
            int noOfConnections = nodemap.size();
            System.out.println("No of connections: " + noOfConnections);

            nnNode.setFuncNo(1);
            executor.submit(nnNode).get();
            for (int i = 0; i < matrix[0].length; i++) {
                if (matrix[0][i] == 1) {
                    noOfConnections--;
                }
            }
            System.out.println("No of connections: " + noOfConnections);
            for (int i = 0; i < matrix.length; i++) {
                if (matrix[0][i] == 1) {
//                    System.out.println("index is " + i);
                    if (noOfConnections <= 1) {
                        System.out.println("Prime function is executing");
                        nodeList[i].setFunctionType("prime");
                        nodeList[i].setFuncNo(2);
                        executor.submit(nodeList[i]).get();
                    } else {
                        System.out.println("Sqrt function is executing");
                        nodeList[i].setFunctionType("sqrt");
                        nodeList[i].setFuncNo(2);
                        executor.submit(nodeList[i]).get();
                    }
                }
            }


            nodeList[0].setFuncNo(3);
            executor.submit(nodeList[0]).get();

            for (int i = 1; i < matrix.length; i++) {
                for (int j = 1; j < matrix.length; j++) {
                    if (matrix[i][j] == 1) {
                        nodeList[i].setFuncNo(4);
                        executor.submit(nodeList[i]).get();
                    }
                }
            }
//            for(Node i:nodeList){
//                executor.submit(i).get();
//            }

            nodeList[0].setFuncNo(5);
            executor.submit(nodeList[0]).get();

            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.MINUTES);

            sc.close();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        // long cpuEndTime = bean.getCurrentThreadCpuTime();
        long endTime = System.currentTimeMillis();

        System.out.println("All nodes have finished.");
        System.out.println("Execution time: " + (endTime - startTime) + "ms");
        // System.out.println("CPU time: " + (cpuEndTime - cpuStartTime) / 1_000_000 + "ms");
    }

    public static Node[] getNodeList() {
        return nodeList;
    }
}


