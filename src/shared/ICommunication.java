package shared;

/**
 * Interface for message sending and receiving.
 */
public interface ICommunication {
    public void sendMessage(String msg) throws Exception;
    public String receiveMessage() throws Exception;
}