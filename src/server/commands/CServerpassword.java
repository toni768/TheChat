package server.commands;

import server.ChatServer;
import server.Messages;
import server.ChatServer.ChatServerThread;
import server.Room;
import java.util.ArrayList;

/**
 * Change server password, requires mode 3.
 */
public class CServerpassword implements server.ICommand {
    @Override
    public void execute(ChatServerThread chatServerThread, String msg) {

        if(chatServerThread.user.getMode() < 3) {
            chatServerThread.sendMessageToUser(Messages.permissionDeniedMessage());
            return;
        }
        msg = msg.trim();
        ChatServer.serverSettings.setServerPassword(msg);
        chatServerThread.sendMessageToModeServer("Server password is now: "+msg, 3);
    }
    @Override
	public String getInfo() {
		return "/serverpassword <password> - Change server password";
	}
}