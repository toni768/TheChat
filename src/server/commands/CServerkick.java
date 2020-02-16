package server.commands;

import server.ChatServer;
import server.ChatServer.ChatServerThread;
import server.Room;
import server.User;
import server.Messages;

import java.util.ArrayList;
import java.util.Iterator;
/**
 * Kick user from server, requires mode 3.
 */
public class CServerkick implements server.ICommand {

    @Override
    public void execute(ChatServerThread chatServerThread, String msg) {
    	User user = chatServerThread.user;
    	ChatServerThread ct = chatServerThread;
    	Room room = user.getCurrentRoom();
        if ((user.getMode() < 3)) {
       		ct.sendMessageToUser(Messages.permissionDeniedMessage());
        	return;
        }        
        if(room==null) {
			chatServerThread.sendMessageToUser(Messages.notInRoomMessage());
            return;
        }
   	
        String reason = "";
        String[] splittedMsg = msg.split(" ");
        String kickReciever = splittedMsg[0];
		int index = msg.indexOf(' ');
		if(splittedMsg.length > 1) {
			reason = msg.substring(index+1);
		}
        Iterator<User> it = room.users.iterator();
        while (it.hasNext()) {
        	User u = it.next();
        	if (u.getName().equals(kickReciever)) {
            	chatServerThread.sendMessageToCurrentRoom((u.getName() + " was kicked from the server for " + reason), "SERVER");
    			u.setCurrentRoom(null);
    	        try {
					u.getCommunication().sendMessage("You were kicked from the server!");
					it.remove();
    	            u.getSocket().close();
    	        } catch (Exception e) {
    	            //TODO: handle exception
    	        }      
        	}
        }
	}
	@Override
	public String getInfo() {
		return "/serverkick <user> [reason] - Kick user from the server";
	}
}