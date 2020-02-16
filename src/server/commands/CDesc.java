package server.commands;

import server.ChatServer;
import server.ChatServer.ChatServerThread;
import server.Room;
import java.util.ArrayList;
/**
 * Shows room's and server's description.
 */
public class CDesc implements server.ICommand {
	@Override
    public void execute(ChatServerThread chatServerThread, String msg) {
        Room room = chatServerThread.user.getCurrentRoom();
        if(room != null) {
            chatServerThread.sendMessageToUser("** Room description: " + room.roomSettings.getDescription() + " **");
        } else {
            chatServerThread.sendMessageToUser("** Server description: " + ChatServer.serverSettings.getDescription() + " **");
        }
    }
    @Override
	public String getInfo() {
		return "/desc - Get room's/server's description";
	}
}