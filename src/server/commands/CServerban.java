package server.commands;

import server.ChatServer;
import server.Messages;
import server.ChatServer.ChatServerThread;
import server.Room;
import server.User;

import java.util.ArrayList;
import java.util.Iterator;
/**
 * Ban user from server, requires mode 3.
 */
public class CServerban implements server.ICommand {

    @Override
    public void execute(ChatServerThread chatServerThread, String msg) {

        // Check mode
        if(chatServerThread.user.getMode() < 3) {
            chatServerThread.sendMessageToUser(Messages.permissionDeniedMessage());
            return;
        }
        if(msg.isEmpty() || !msg.contains(".")) {
            chatServerThread.sendMessageToUser("Give valid ip address!");
            return;
        }
        //Determine if username or address
        int index = msg.indexOf(" ");
        String reason = "";
        String address = "";
        if(index == -1) {
            address = msg;
        } else {
            address = msg.substring(0,index);
            reason = msg.substring(index+1);
        }
        // Find, ban and kick possible users on the server
        boolean banned = false;
        Iterator<User> it = ChatServer.users.iterator();
        while(it.hasNext()) {
            User u = it.next();
            if(u.getSocket().getInetAddress().getHostAddress().equals(address) && u.getMode() < 3) {
                if(chatServerThread.user.getCurrentRoom() != null) {
                    chatServerThread.sendMessageToCurrentRoom(u.getName()+" was banned from the server! Reason: "+reason, "SERVER");
                }               
                chatServerThread.sendMessageToUser(u.getName()+" was banned!");
                if(u.getCurrentRoom() != null) {
                    u.getCurrentRoom().users.remove(u);
                    u.setCurrentRoom(null);
                }
                u.setMode(0);

                String ban = address +":"+u.getName()+":"+reason;
                System.out.println(ban);
                ChatServer.serverSettings.getBannedAddresses().add(ban);
                ChatServer.serverSettings.saveBannedUsers();
                banned = true;
                try {
                    u.getCommunication().sendMessage("You were banned from the server!");
                    it.remove();
                    u.getSocket().close();
                } catch (Exception e) {
                    //TODO: handle exception
                }             
            }
        }  
        // If no users were found, ban the address
        if(!banned && address.contains(".")) {
            String ban = address +":"+""+":"+reason;
            System.out.println(ban);
            ChatServer.serverSettings.getBannedAddresses().add(ban);
            ChatServer.serverSettings.saveBannedUsers();
            chatServerThread.sendMessageToUser(address+" was banned!");
        }
    }
    @Override
	public String getInfo() {
		return "/serverban <ipaddress> [reason] - Ban address from the server";
	}
}