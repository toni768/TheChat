package server.commands;

import server.ChatServer;
import server.ChatServer.ChatServerThread;
import server.Room;
import server.User;

import java.util.ArrayList;
/**
 * Send a private message to user.
 */
public class CPrivmsg implements server.ICommand {
	@Override
    public void execute(ChatServerThread chatServerThread, String msg) {
    	Room r = chatServerThread.user.getCurrentRoom();
        if(r==null) {
            return;
        }
        
        String msgbody = "";
        String[] splittedMsg = msg.split(" ");
        String msgReciever = splittedMsg[0];
        int index = msg.indexOf(' ');
        msgbody = msg.substring(index+1);
        
        for(User u : r.users) {
        	if (u.getName().equals(msgReciever)) {
                try {
                	u.getCommunication().sendMessage(chatServerThread.user.getName() + " whispers: " + msgbody);
                	chatServerThread.user.getCommunication().sendMessage("You whispered to " + u.getName() + ": " + msgbody);
                } catch (Exception e) {
            		System.out.println("Could not send message");
                }
        	}
        }
    }
    @Override
	public String getInfo() {
		return "/privmsg <user> <message> - Send a private message to user";
	}
}