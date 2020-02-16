package server.commands;

import server.ChatServer;
import server.Messages;
import server.ChatServer.ChatServerThread;
import server.Room;
import server.User;

import java.util.ArrayList;
/**
 * Unban user from room, requires mode 1.
 */
public class CRoomunban implements server.ICommand {

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
        //Check if address/username is banned
        if(!room.isBanned(msg, msg)) {
            chatServerThread.sendMessageToUser("User/address is not banned!");
            return;
        }
        String username = "";
        String address = "";
        for(String banned : room.roomSettings.getBannedAddresses()) {
            String[] splitBanned = banned.split(":");
            if(splitBanned[0].equals(msg) || splitBanned[1].equals(msg)) {
                room.roomSettings.getBannedAddresses().remove(banned); // Not a problem since we only remove one
                room.roomSettings.saveBannedUsers();
                address = splitBanned[0];
                username = splitBanned[1];
                chatServerThread.sendMessageToCurrentRoom(address+":"+username + " was unbanned from the room", "SERVER");
                break;
            }
        }
        
    }
    @Override
	public String getInfo() {
		return "/roomunban <ip|username> - Remove room ban from user/address";
	}
}