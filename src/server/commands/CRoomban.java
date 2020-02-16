package server.commands;

import server.ChatServer;
import server.Messages;
import server.ChatServer.ChatServerThread;
import server.Room;
import server.User;

import java.util.ArrayList;
/**
 * Ban user from room.
 */
public class CRoomban implements server.ICommand {
	@Override
    public void execute(ChatServerThread chatServerThread, String msg) {
        Room room = chatServerThread.user.getCurrentRoom();
        // Check mode
        if(chatServerThread.user.getMode() < 1) {
            chatServerThread.sendMessageToUser(Messages.permissionDeniedMessage());
            return;
        }
        if(room == null) {
            chatServerThread.sendMessageToUser(Messages.notInRoomMessage());
            return;
        }
        
        int index = msg.indexOf(" ");
        String address;
        String username;
        String reason = "";
        if(index == -1) {
            username = msg;
        } else {
            username = msg.substring(0,index);
            reason = msg.substring(index+1);
        }

        User toBeBanned = room.getUser(username);
        if(toBeBanned==null) {
            chatServerThread.sendMessageToUser("User not found!");
            return;
        }
        address = toBeBanned.getSocket().getInetAddress().getHostAddress();
        // Can only ban lower modes; And can't ban yourself
        if(toBeBanned.getMode()>=chatServerThread.user.getMode()) {
            chatServerThread.sendMessageToUser(Messages.permissionDeniedMessage());
            return;
        }
        String ban = address +":"+username+":"+reason;
        System.out.println(ban);
        chatServerThread.sendMessageToCurrentRoom(toBeBanned.getName()+" was banned from the room! Reason: "+reason, "SERVER");
        
        room.roomSettings.getBannedAddresses().add(ban);
        room.roomSettings.saveBannedUsers();
        room.users.remove(toBeBanned);

        toBeBanned.setCurrentRoom(null);
        toBeBanned.setMode(0);
        
    }
    @Override
	public String getInfo() {
		return "/roomban <user> [reason] - Ban user from room";
	}
}