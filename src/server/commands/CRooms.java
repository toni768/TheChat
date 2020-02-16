package server.commands;

import server.ChatServer;
import server.ChatServer.ChatServerThread;
import server.Room;
import java.util.ArrayList;

/**
 * Shows all the rooms in server
 */
public class CRooms implements server.ICommand {
    @Override
    public void execute(ChatServerThread chatServerThread, String msg) {
        String rooms = "Available rooms in the server:\n";
        for(Room r : ChatServer.rooms.values()) {
            rooms += r.roomSettings.getName();
            if(r.roomSettings.getRoomPassword() != "") {
                rooms += " (Password protected!)";
            }
            rooms += "\n";
        }
        chatServerThread.sendMessageToUser(rooms);
        
    }
    @Override
	public String getInfo() {
		return "/rooms - Show all rooms in the server";
	}
}