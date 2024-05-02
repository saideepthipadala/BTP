import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class MerkleTree {

    private TreeNode root;

    public MerkleTree(List<String> transactions) {
        List<TreeNode> leafNodes = new ArrayList<>();
        for (String transaction : transactions) {
            leafNodes.add(new TreeNode(transaction));
        }

        this.root = buildTree(leafNodes);
    }

    

    private TreeNode buildTree(List<TreeNode> nodes) {
        if (nodes.size() == 1) {
            return nodes.get(0);
        }

        List<TreeNode> newNodes = new ArrayList<>();
        for (int i = 0; i < nodes.size() - 1; i += 2) {
            TreeNode left = nodes.get(i);
            TreeNode right = nodes.get(i + 1);
            TreeNode newNode = new TreeNode(left.getHash() + right.getHash());
            newNodes.add(newNode);
        }

        return buildTree(newNodes);
    }

    public String getRootHash() {
        return root.getHash();
    }

    private class TreeNode {

        private String hash;
        private String data;

        public TreeNode(String data) {
            this.data = data;
            this.hash = calculateHash(data);
        }

        public String getHash() {
            return hash;
        }

        public String getData() {
            return data;
        }

        private String calculateHash(String data) {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hash = digest.digest(data.getBytes());
                return bytesToHex(hash);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("SHA-256 algorithm not found", e);
            }
        }

        private String bytesToHex(byte[] bytes) {
            StringBuilder hexString = new StringBuilder();
            for (byte b : bytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        }
    }
}