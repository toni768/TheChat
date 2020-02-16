package server.commands;

import server.ChatServer;
import server.ChatServer.ChatServerThread;
import server.Room;
import server.User;

import java.util.ArrayList;
/**
 * Login as a server admin (mode 3)
 */
public class CServerlogin implements server.ICommand {
    @Override
    public void execute(ChatServerThread chatServerThread, String msg) {  
        if(ChatServer.serverSettings.getServerAdminPassword().equals(msg)) {
            chatServerThread.user.setMode(3);
            chatServerThread.sendMessageToUser("You are now server admin!");
        } else {
            chatServerThread.sendMessageToUser("Wrong password!");
        }
	}
	@Override
	public String getInfo() {
		return "/serverlogin <password> - Change your mode to server admin";
	}
}