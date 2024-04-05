import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;



public class ReleaseSubTaskEnvelope {
    private String envId;
    private Node sentBy;
    private Node receivedBy;
    private byte[] EncryptedMessage;
    private long timeForSubTaskCompletion;

    

    public static ReleaseSubTaskEnvelope releaseSubTask(Node sender,Node receiver,String func,String y,long time){

        

        return new ReleaseSubTaskEnvelope();
    }

    // public static void main(String[] args) {
    //     long currentTimestampMillis = System.currentTimeMillis();
    //     System.out.println("Current timestamp in milliseconds: " + currentTimestampMillis);
    // }

}
