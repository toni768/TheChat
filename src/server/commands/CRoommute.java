package server.commands;

import server.ChatServer;
import server.Messages;
import server.ChatServer.ChatServerThread;
import server.Room;
import server.User;
import java.util.ArrayList;
/**
 * Mute user from talking in a room, requires mode 1.
 * Note: mutes EVERYONE who uses same ip
 */
public class CRoommute implements server.ICommand {

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
        }

        int index = msg.indexOf(" ");
        String address;
        String username;
        if(index == -1) {
            username = msg;
        } else {
            username = msg.substring(0,index);
        }

        User toBeMuted = room.getUser(username);
        if(toBeMuted == null) {
            chatServerThread.sendMessageToUser("User not found!");
            return;
        }
        address = toBeMuted.getSocket().getInetAddress().getHostAddress();
        
        // Check if user is already muted
        if(room.isMuted(address, username)) {
            chatServerThread.sendMessageToUser("User is already muted!");
            return;
        }
        // Can only mute lower modes; And can't mute yourself
        if(toBeMuted.getMode()>0) {
            chatServerThread.sendMessageToUser(Messages.permissionDeniedMessage());
            return;
        }
        String mute = address +":"+username;
        System.out.println(mute);
        chatServerThread.sendMessageToCurrentRoom(toBeMuted.getName()+" was muted!", "SERVER");
        
        room.roomSettings.getMutedAddresses().add(mute);
        room.roomSettings.saveMutedUsers();

    }
    @Override
	public String getInfo() {
		return "/roommute <user> - Give user room mute";
	}
}