package server.commands;

import server.ChatServer;
import server.ChatServer.ChatServerThread;
import server.Room;
import server.User;
import server.Messages;
import java.util.ArrayList;

/**
 * Command that lists all the users in the current room.
 */
public class CUsers implements server.ICommand {

    @Override
    public void execute(ChatServerThread chatServerThread, String msg) {
    	User user = chatServerThread.user;
    	Room r = user.getCurrentRoom();

    	if(r==null) {
			chatServerThread.sendMessageToUser(Messages.notInRoomMessage());
            return;
        }
    	
		chatServerThread.sendMessageToUser("** Users currently in room **");
		for(User u : r.users) {
			//If mode > 0, send also ip address
			chatServerThread.sendMessageToUser(
					"> " + u.getName() +" "+ (user.getMode() > 0 ? u.getSocket().getInetAddress().getHostAddress() : "")
				);
		}
 
	}
	@Override
	public String getInfo() {
		return "/users - Show users in room";
	}
}