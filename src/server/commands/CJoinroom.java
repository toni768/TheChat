package server.commands;

import server.ChatServer;
import server.ChatServer.ChatServerThread;
import server.Room;
import server.User;

import java.util.ArrayList;
/**
 * Join a room.
 */
public class CJoinroom implements server.ICommand {
	@Override
    public void execute(ChatServerThread chatServerThread, String msg) {
        String[] splitMsg = msg.split(" ");
        Room room = ChatServer.rooms.get(splitMsg[0]);
    	User user = chatServerThread.user;
        // Check if the room exists
        if(room==null) {
            chatServerThread.sendMessageToUser(msg+" room doesn't exist!");
            return;
        }
        if(room.users.size() >= room.roomSettings.getMaxUsers()) {
            chatServerThread.sendMessageToUser("Room is full!");
            return;
        }
        // Check for same names
        for(User u : room.users) {
            String name = u.getName();
            if(chatServerThread.user.getName().equals(name)) {
                chatServerThread.sendMessageToUser(chatServerThread.user.getName()+" already taken! Change your username with /username");
                return;
            }
        }
   	
        // Check mode and room password
        if(chatServerThread.user.getMode() > 2) {
            chatServerThread.sendMessageToUser("You are server admin!");
        } else {
            if(splitMsg.length > 1) {
                String password = splitMsg[1];
                if(password.equals(room.roomSettings.getRoomAdminPassword())) {
                        chatServerThread.user.setMode(2);
                        chatServerThread.sendMessageToUser("You are room admin!");
                } else if(password.equals(room.roomSettings.getRoomModeratorPassword())) {
	                    chatServerThread.user.setMode(1);
	                    chatServerThread.sendMessageToUser("You are room moderator!");
                } else if(password.equals(room.roomSettings.getRoomPassword())) {
	                    chatServerThread.user.setMode(0);
	                    chatServerThread.sendMessageToUser("You are normal user");
                } else {
                    chatServerThread.sendMessageToUser("Wrong password!");
                    return;
                }
            } else {
                if(room.roomSettings.getRoomPassword() != "") {
                    chatServerThread.sendMessageToUser("Wrong password!");
                    return;
                }
            }
        }

        // Check if user/username is banned from the room
        // Doesn't work if client connects using localhost
        // Can't ban room admin
        String address = chatServerThread.user.getSocket().getInetAddress().getHostAddress();
        if(room.isBanned(address, chatServerThread.user.getName()) && chatServerThread.user.getMode() < 2 ) {            
            chatServerThread.sendMessageToUser("You are banned from this room!");
            chatServerThread.user.setMode(0);
            return;
        }
        room.users.add(chatServerThread.user);
        chatServerThread.user.setCurrentRoom(room);
        chatServerThread.sendMessageToCurrentRoom((chatServerThread.user.getName() + " joined room: " + room.roomSettings.getName()), "SERVER");
        chatServerThread.sendMessageToUser("** Room name: " + room.roomSettings.getName());
        chatServerThread.sendMessageToUser("** Room description: " + room.roomSettings.getDescription());
        chatServerThread.sendMessageToUser("** Room rules: "+ room.roomSettings.getRules());
        chatServerThread.sendMessageToUser("** Message of the Day: "+room.roomSettings.getMotd());
        chatServerThread.sendMessageToUser(" ");
    }
    @Override
	public String getInfo() {
		return "/joinroom <roomname> [password] - Join a room";
	}
}