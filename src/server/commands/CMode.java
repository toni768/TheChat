package server.commands;

import server.ChatServer;
import server.Messages;
import server.ChatServer.ChatServerThread;
import server.Room;
import server.User;

import java.util.ArrayList;
/**
 * Get mode/Change user mode, requires mode 3.
 */
public class CMode implements server.ICommand {
    @Override
    public void execute(ChatServerThread chatServerThread, String msg) {
    	User user = chatServerThread.user;
    	ChatServerThread ct = chatServerThread;
		Room r = user.getCurrentRoom();
		if(msg.isEmpty()) {
			ct.sendMessageToUser("Your mode: "+user.getMode());
			return;
		}
        if(r==null) {
			chatServerThread.sendMessageToUser(Messages.notInRoomMessage());
            return;
        }
        if (!(user.getMode() == 3)) {
       		ct.sendMessageToUser(Messages.permissionDeniedMessage());
        	return;
        }
        	
        int modelevel = 100;
        String[] splittedMsg = msg.split(" ");
        String modeReciever = splittedMsg[0];
        int index = msg.indexOf(' ');
        try {
        	modelevel = Integer.parseInt(msg.substring(index+1));
        } catch (Exception e) {
       		ct.sendMessageToUser("Not valid number.");
        }
        if ((modelevel == 1) || (modelevel == 2) || (modelevel == 3) || (modelevel == 0)) {
        	boolean found = false;
            for(User u : r.users) {
            	if (u.getName().equals(modeReciever)) {
                    	u.setMode(modelevel);
                    	found = true;            	
                    	try {
                    		String modelevelString = "error";
                    		switch (modelevel) {
	                    		case 0: modelevelString = "normal user.";
	                    		break;
	                    		case 1: modelevelString = "room moderator.";
	                    		break;
	                    		case 2: modelevelString = "room administrator.";
	                    		break;
	                    		case 3: modelevelString = "server administrator.";
	                    		break;
                    		}
                    		ct.sendMessageToUser("User " + u.getName() + " is now " + modelevelString);
                    	} catch (Exception ex) {
                    		System.out.println("Could not send message.");
                    	}
            	}
            }
            if (!(found)) {
           		ct.sendMessageToUser("Could not find that user.");
            }
        } else {
        		ct.sendMessageToUser("Not valid number.");
        }
	}
	@Override
	public String getInfo() {
		return "/mode [<username> <mode>] - Get mode/Change user's mode, 0 = user, 1 = room moderator, 2 = room administrator, 3 = server administrator";
	}
}