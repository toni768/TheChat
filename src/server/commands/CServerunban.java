package server.commands;

import server.ChatServer;
import server.Messages;
import server.ChatServer.ChatServerThread;
import server.Room;
import server.User;

import java.util.ArrayList;
import java.util.Iterator;
/**
 * Unban user from server, requires mode 3.
 */
public class CServerunban implements server.ICommand {

    @Override
    public void execute(ChatServerThread chatServerThread, String msg) {
        // Check mode
        if(chatServerThread.user.getMode() < 3) {
            chatServerThread.sendMessageToUser(Messages.permissionDeniedMessage());
            return;
        }
        //Check if address is banned
        if(!chatServerThread.isBanned(msg)) {
            chatServerThread.sendMessageToUser("Address is not banned!");
            return;
        }
        Iterator<String> it = ChatServer.serverSettings.getBannedAddresses().iterator();
        while(it.hasNext()) {
            String[] splitBanned = it.next().split(":");
            if(splitBanned[0].equals(msg)) {
                it.remove();
                ChatServer.serverSettings.saveBannedUsers();
                chatServerThread.sendMessageToUser(splitBanned[0]+" was unbanned from the server!");
            }
        }
    }
    @Override
	public String getInfo() {
		return "/serverunban <ip> - Unban ip address";
	}
}