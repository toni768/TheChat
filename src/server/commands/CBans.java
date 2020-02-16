package server.commands;

import server.ChatServer;
import server.Messages;
import server.ChatServer.ChatServerThread;
import server.Room;
import java.util.ArrayList;
/**
 * Show bans from room or server, depending on the user who's using the command.
 */
public class CBans implements server.ICommand {
	@Override
    public void execute(ChatServerThread chatServerThread, String msg) {
        if(chatServerThread.user.getMode()<1) {
            chatServerThread.sendMessageToUser(Messages.permissionDeniedMessage());
            return;
        }
        Room room = chatServerThread.user.getCurrentRoom();
        String bans = "";
        if(room==null) {
            if(chatServerThread.user.getMode() < 3) { // Show server bans only to mode 3 users
                chatServerThread.sendMessageToUser(Messages.permissionDeniedMessage());
                return;
            }
            for(String ban : ChatServer.serverSettings.getBannedAddresses()) {
                bans += ">"+ ban + "\n";
            }
            
        } else {
            for(String ban : room.roomSettings.getBannedAddresses()) {
                bans += ">"+ ban + "\n";
            }
        }
      
        chatServerThread.sendMessageToUser("Banned users:\n"+bans);
    }

    @Override
	public String getInfo() {
		return "/bans - Show banned users";
	}
}