package server.commands;

import server.ChatServer;
import server.Messages;
import server.ChatServer.ChatServerThread;
import server.Room;
import server.User;

import java.util.ArrayList;
/**
 * Sends a notification to every user in the room, requires mode 1.
 */
public class CRoomnotice implements server.ICommand {

	@Override
    public void execute(ChatServerThread chatServerThread, String msg) {
    	User user = chatServerThread.user;
    	Room room = user.getCurrentRoom();
        
    	if(user.getMode() < 1) {
            chatServerThread.sendMessageToUser(Messages.permissionDeniedMessage());
            return;
        }
        if(room == null) {
            chatServerThread.sendMessageToUser(Messages.notInRoomMessage());
        }
    	
    	chatServerThread.sendMessageToCurrentRoom("ROOM NOTIFICATION: " + msg, "SERVER");
    }
    @Override
	public String getInfo() {
		return "/roomnotice <message> - Send a notification to everyone in the room";
	}
}