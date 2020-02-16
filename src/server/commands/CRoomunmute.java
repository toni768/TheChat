package server.commands;

import server.ChatServer;
import server.Messages;
import server.ChatServer.ChatServerThread;
import server.Room;
import server.User;

import java.util.ArrayList;
/**
 * Remove user's room mute, requires mode 1.
 */
public class CRoomunmute implements server.ICommand {

    @Override
    public void execute(ChatServerThread chatServerThread, String msg) {
        Room room = chatServerThread.user.getCurrentRoom();
        // Check mode
        if(chatServerThread.user.getMode() < 1) {
            chatServerThread.sendMessageToUser(Messages.permissionDeniedMessage());
            return;
        }
        if(room==null) {
            chatServerThread.sendMessageToUser(Messages.notInRoomMessage());
        }
        
        if(!room.isMuted(msg, msg)) {
            chatServerThread.sendMessageToUser("User is not muted!");
            return;
        }
        String username = "";
        String address = "";
        for(String muted : room.roomSettings.getMutedAddresses()) {
            String[] splitMuted = muted.split(":");
            if(splitMuted[0].equals(msg) || splitMuted[1].equals(msg)) {
                room.roomSettings.getMutedAddresses().remove(muted);
                room.roomSettings.saveMutedUsers();
                username = splitMuted[1];
                address = splitMuted[0];
                chatServerThread.sendMessageToCurrentRoom(address+":"+username + " was unmuted!", "SERVER");
                break;
            }
        }
    }
    @Override
	public String getInfo() {
		return "/roomunmute <username|ip> - Unmute user in the room";
	}
}