package server.commands;

import server.ChatServer;
import server.Messages;
import server.ChatServer.ChatServerThread;
import server.Room;
import server.User;

import java.util.ArrayList;
/**
 * Show server and room admins.
 */
public class CAdmins implements server.ICommand {
	@Override
    public void execute(ChatServerThread chatServerThread, String msg) {
    	User user = chatServerThread.user;
    	ChatServerThread ct = chatServerThread;
    	Room r = user.getCurrentRoom();
        if(r==null) {
			chatServerThread.sendMessageToUser(Messages.notInRoomMessage());
            return;
        }
        
    	int i = 0;
        for(User u : r.users) {
			i++;
			if (i == 1) {
				ct.sendMessageToUser("** Server administrators in current room **:");
			}			
        	if (u.getMode() == 3) {
				ct.sendMessageToUser(">"+u.getName());
        	}
        }
        
        i = 0;
        for(User u : r.users) {
			i++;
			if (i == 1) {
				ct.sendMessageToUser("** Room administrators in current room **:");
			}			
        	if (u.getMode() == 2) {

				ct.sendMessageToUser(">"+u.getName());
        	}
        }
        
        i = 0;
        for(User u : r.users) {
			i++;
			if (i == 1) {
				ct.sendMessageToUser("** Room moderators in current room **:");
			}			
        	if (u.getMode() == 1) {
				ct.sendMessageToUser(">"+u.getName());
        	}
        }
	}
	@Override
	public String getInfo() {
		return "/admins - Show admins/moderators in the room";
	}
}