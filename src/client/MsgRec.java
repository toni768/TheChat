package client;

import javafx.application.Platform;
/**
 * Message receiver
 */
public class MsgRec extends Thread {
    
	public MsgRec() {
    }
	/**
	 * Run thread
	 */
	public void run() {
	    		try {
		            while(true) {
						String msg = ChatClient.communication.receiveMessage();
						if(msg == null) {
							break;
						}

						Platform.runLater(() -> { 	ChatClient.taMessages.appendText(msg+"\n");
													ChatClient.taMessages.setScrollTop(Double.MAX_VALUE);});
					}
					Platform.runLater(() -> { 	ChatClient.taMessages.appendText("Disconnected!\n");
												ChatClient.taMessages.setScrollTop(Double.MAX_VALUE);});					

					System.gc(); // Collect garbage
					System.out.println("MsgRec thread closing...");
		        } catch (Exception er) {
					Platform.runLater(() -> { 	ChatClient.taMessages.appendText("Disconnected!\n");
												ChatClient.taMessages.setScrollTop(Double.MAX_VALUE);});
					System.out.println("MsgRec thread closing through exception");
					System.gc(); // Collect garbage
		        }
    }
}