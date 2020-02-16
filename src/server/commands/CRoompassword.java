package server.commands;

import server.ChatServer;
import server.Messages;
import server.ChatServer.ChatServerThread;
import server.Room;
import server.User;

import java.util.ArrayList;

/**
 * Set room password, requires mode 2
 */
public class CRoompassword implements server.ICommand {

	@Override
    public void execute(ChatServerThread chatServerThread, String msg) {
    	User user = chatServerThread.user;
    	ChatServerThread ct = chatServerThread;
    	Room room = user.getCurrentRoom();
         if ((user.getMode() < 2)) {
       		ct.sendMessageToUser(Messages.permissionDeniedMessage());
        	return;
        }       
        if(room==null) {
            chatServerThread.sendMessageToUser(Messages.notInRoomMessage());
            return;
        }

    	room.roomSettings.setRoomPassword(msg);
    	ct.sendMessageToUser("Room password is now " + room.roomSettings.getRoomPassword());
    }
    @Override
	public String getInfo() {
		return "/roompassword <password> - Set room's password";
	}
}