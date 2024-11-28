
// import java.util.ArrayList;
// import java.util.LinkedHashMap;
// import java.util.Scanner;
// import java.util.concurrent.ExecutionException;
// import java.util.concurrent.ExecutorService;
// import java.util.concurrent.Executors;
// import java.util.concurrent.TimeUnit;

// public class Main {

//     public static LinkedHashMap<Envelope, ArrayList<Envelope>> DAG;
//     public static Node[] nodeList;
//     public static int[][] connectionMatrix;

//     public static Node[] getNodes() {
//         return nodeList;
//     }

// public static void printMatrix(int[][] matrix) {
//     for (int i = 0; i < matrix.length; i++) {
//         for (int j = 0; j < matrix[i].length; j++) {
//             System.out.print(matrix[i][j] + " ");
//         }
//         System.out.println();
//     }
// }

//     public static LinkedHashMap<Envelope, ArrayList<Envelope>> getDAG() {
//         return DAG;
//     }

//     public static void main(String[] args) throws InterruptedException, ExecutionException {
//         long startTime = System.currentTimeMillis();

//         // Get the thread bean to measure CPU time

//         try {
//             Scanner sc = new Scanner(System.in);
//             System.out.println("Enter number of nodes in the network:");
//             int numNodes = sc.nextInt();
//             nodeList = new Node[numNodes];
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

//             ExecutorService executor = Executors.newCachedThreadPool();

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
// //                    System.out.println("index is " + i);
//                     if (noOfConnections <= 1) {
//                         System.out.println("Prime function is executing");
//                         nodeList[i].setFunctionType("prime");
//                         nodeList[i].setFuncNo(2);
//                         executor.submit(nodeList[i]).get();
//                     } else {
//                         System.out.println("Sqrt function is executing");
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
//                     if (matrix[i][j] == 1) {
//                         nodeList[i].setFuncNo(4);
//                         executor.submit(nodeList[i]).get();
//                     }
//                 }
//             }
// //            for(Node i:nodeList){
// //                executor.submit(i).get();
// //            }

//             nodeList[0].setFuncNo(5);
//             executor.submit(nodeList[0]).get();

//             executor.shutdown();
//             executor.awaitTermination(1, TimeUnit.MINUTES);

//             sc.close();
//         } catch (InterruptedException | ExecutionException e) {
//             e.printStackTrace();
//         }

//         // long cpuEndTime = bean.getCurrentThreadCpuTime();
//         long endTime = System.currentTimeMillis();

//         System.out.println("All nodes have finished.");
//         System.out.println("Execution time: " + (endTime - startTime) + "ms");
//         // System.out.println("CPU time: " + (cpuEndTime - cpuStartTime) / 1_000_000 + "ms");
//     }

//     public static Node[] getNodeList() {
//         return nodeList;
//     }
// }

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static LinkedHashMap<Envelope, ArrayList<Envelope>> DAG;
    public static Node[] nodeList;
    public static int[][] connectionMatrix;

    public static Node[] getNodes() {
        return nodeList;
    }

    public static LinkedHashMap<Envelope, ArrayList<Envelope>> getDAG() {
        return DAG;
    }

    public static void printMatrix(int[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        long startTime = System.currentTimeMillis();

        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the no.of nodes : ");
        int n = sc.nextInt();
        nodeList = new Node[n];
        ExecutorService executor = Executors.newCachedThreadPool();
        Node nnNode = new Node("nn" + 1, NodeType.nn, executor);
        nodeList[0] = nnNode;
        for (int i = 1; i < n; i++) {
            Node newNode = new Node("cn" + i, NodeType.cn, executor);
            nodeList[i] = newNode;
        }
        connectionMatrix = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (j == i + 1 || (i == n - 1 && j == 0))
                    connectionMatrix[i][j] = 1;
                else
                    connectionMatrix[i][j] = 0;
            }
        }
        printMatrix(connectionMatrix);
        for (int i = 0; i < nodeList.length; i++) {
            nodeList[i].setMatrix(connectionMatrix);
        }

        DAG = new LinkedHashMap<>();
        nnNode.setFuncNo(1);
        executor.submit(nnNode).get();

        System.out.println(DAG);
        sc.close();

        long endTime = System.currentTimeMillis();

        long executionTime = endTime - startTime;
        System.out.println("Execution Time (in milliseconds): " + executionTime);
    }
}
