package server.commands;

import server.ChatServer;
import server.Messages;
import server.ChatServer.ChatServerThread;
import server.Room;
import server.User;

import java.util.ArrayList;
/**
 * Kick user from room, requires mode 1.
 */
public class CKick implements server.ICommand {
	@Override
    public void execute(ChatServerThread chatServerThread, String msg) {
    	User user = chatServerThread.user;
    	ChatServerThread ct = chatServerThread;
    	Room room = user.getCurrentRoom();
        
        if(room==null) {
            ct.sendMessageToUser(Messages.notInRoomMessage());
            return;
        }
        if ((user.getMode() < 1)) {
       		ct.sendMessageToUser(Messages.permissionDeniedMessage());
        	return;
        }
    	
        String reason = "";
        String[] splittedMsg = msg.split(" ");
        String kickReciever = splittedMsg[0];
        int index = msg.indexOf(' ');
        if(splittedMsg.length > 1) {
            reason = msg.substring(index+1);
        }
               
        for (int i = 0; i < room.users.size(); i++) {
        	User u = room.users.get(i);
        	if (u.getName().equals(kickReciever)) {
            	chatServerThread.sendMessageToCurrentRoom((u.getName() + " was kicked from the room for: " + reason), "SERVER");
                room.users.remove(u);
                u.setCurrentRoom(null);
                if(u.getMode() < 3) {
                    u.setMode(0);
                }
        	}
        }
    }
    @Override
	public String getInfo() {
		return "/kick <username> [reason] - Kick user from room";
	}
}