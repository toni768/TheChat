package server.commands;

import server.ChatServer;
import server.ChatServer.ChatServerThread;
import server.Room;
import server.User;
import server.Messages;

import java.util.ArrayList;
/**
 * Login as a room moderator or admin
 */
public class CRoomlogin implements server.ICommand {
    @Override
    public void execute(ChatServerThread chatServerThread, String msg) {
        Room room = chatServerThread.user.getCurrentRoom();
        User user = chatServerThread.user;
        if(room == null) {
            chatServerThread.sendMessageToUser(Messages.notInRoomMessage());
            return;
        }
        
        if(room.roomSettings.getRoomModeratorPassword().equals(msg)) {
            user.setMode(1);
            chatServerThread.sendMessageToUser("You are now room moderator!");
        } else if(room.roomSettings.getRoomAdminPassword().equals(msg)) {
            user.setMode(2);
            chatServerThread.sendMessageToUser("You are now room admin!");
        } else {
            chatServerThread.sendMessageToUser("Wrong password!");
        }
	}
	@Override
	public String getInfo() {
		return "/roomlogin <password> - Change your mode to room moderator or admin";
	}
}