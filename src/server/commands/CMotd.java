package server.commands;

import server.ChatServer;
import server.ChatServer.ChatServerThread;
import server.Room;
import server.User;

import java.util.ArrayList;
/**
 * Show server/room message of the day.
 */
public class CMotd implements server.ICommand {
	@Override
    public void execute(ChatServerThread chatServerThread, String msg) {
        ChatServerThread ct = chatServerThread;
        if(ct.user.getCurrentRoom() != null) {
            ct.sendMessageToUser("**Message of the day: \n" + ct.user.getCurrentRoom().roomSettings.getMotd());
            return;
        }
   		ct.sendMessageToUser("**Message of the day: \n" + ChatServer.serverSettings.getMotd());
    }
    @Override
	public String getInfo() {
		return "/motd - Show server/room message of the day";
	}
}