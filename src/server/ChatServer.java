package server;
import java.util.HashMap;
import java.util.Map;

import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import server.commands.*;

import shared.ICommunication;
import shared.DefaultCommunication;

import java.time.LocalDateTime;
import java.sql.Timestamp;
/**
 * ChatServer. Main class.
 */
public class ChatServer {
    // List of users in the server
    public static List<User> users;
    // All available commands
    public static Map<String,ICommand> commands;
    // Map of all rooms
    public static Map<String, Room> rooms;
    // Server's settings
    public static ServerSettings serverSettings;

    /**
     * Load settings and create rooms
     */
    static void initServer() {
        users = Collections.synchronizedList(new ArrayList<User>());
        // ConcurrentHashMap another option.
        commands = Collections.synchronizedMap(new HashMap<String, ICommand>());
        rooms = Collections.synchronizedMap(new HashMap<String,Room>());
        // Load server settings
        serverSettings = new ServerSettings();
        System.out.println("Loading settings");
        serverSettings.load();
        //Create rooms
        System.out.println("Init rooms");
        initRooms();
        //Create commands
        System.out.println("Init commands");
        initCommands();
        System.out.println("Server init done");
    }
    /**
     * Create rooms
     */
    static void initRooms() {
        for(String roomName : serverSettings.getRoomNames()) {
            Room room = new Room();
            System.out.println("Init room: "+roomName);
            room.initRoom(roomName);
            rooms.put(room.roomSettings.getName(),room);
        }
    }
    /**
     * Create commands and add them to the map
     */
    static void initCommands() {
        // All commands here
        //User level commands
        commands.put("/username", new CUsername());
        commands.put("/privmsg", new CPrivmsg());
        commands.put("/joinroom",new CJoinroom());
        commands.put("/leaveroom", new CLeaveroom());
        commands.put("/rules", new CRules());
        commands.put("/help",  new CHelp());
        commands.put("/quit", new CQuit());
        commands.put("/admins", new CAdmins());
        commands.put("/users",  new CUsers());
        commands.put("/motd",  new CMotd());
        commands.put("/desc", new CDesc());
        commands.put("/rooms", new CRooms());
        commands.put("/roomlogin", new CRoomlogin());
        commands.put("/serverlogin", new CServerlogin());
        
        //Moderator
        commands.put("/kick", new CKick());
        commands.put("/roomban", new CRoomban());
        commands.put("/roomunban", new CRoomunban());
        commands.put("/roomnotice",  new CRoomnotice());
        commands.put("/roommute", new CRoommute());
        commands.put("/roomunmute", new CRoomunmute());

        commands.put("/bans", new CBans());
        commands.put("/mutes", new CMutes());
        //Room admin
        commands.put("/roommode", new CRoommode());
        commands.put("/roompassword", new CRoompassword());
        commands.put("/roomdesc", new CRoomdesc());
        commands.put("/roommotd", new CRoommotd());
        //Server admin
        commands.put("/mode", new CMode());
        commands.put("/serverkick", new CServerkick());
        commands.put("/serverban", new CServerban());
        commands.put("/serverunban", new CServerunban());
        commands.put("/serverpassword", new CServerpassword());
        commands.put("/serverdesc", new CServerdesc());
        commands.put("/servernotice", new CServernotice());
        commands.put("/servermotd", new CServermotd());
    }
    
    /**
     * Main method. 
     * @param args Launch parameters. Not in use.
     */
    public static void main(String[] args) {
        initServer();
        ChatServer cs = new ChatServer();
        cs.listen();
    }
    /**
     * Create server and listen.
     */
    public void listen() {
        ServerSocket server = null;
        try {
            server = new ServerSocket(serverSettings.getPort());
            System.out.format("Server listening on port %d %n", ChatServer.serverSettings.getPort());
            while(true) {
                Socket clientSocket = server.accept();
                new ChatServerThread(clientSocket).start();
            }
        } catch (Exception e) {
            System.out.println(e);
        } finally {

        }
    }
    /**
     * Thread for the connected user. 
     */
    public class ChatServerThread extends Thread {
        public User user;

        public ChatServerThread(Socket socket) {
            user = new User(socket);
        }

        public void run() {
            try {
                // Check if user can join
                authenticate();
                // Send server info to user
                sendMessageToUser("** "+ "Server name: " + ChatServer.serverSettings.getName());
                sendMessageToUser("** Server description: "+ChatServer.serverSettings.getDescription());
                sendMessageToUser("** Server rules: "+ChatServer.serverSettings.getRules());
                sendMessageToUser("** Server message of the day: " + serverSettings.getMotd());
                sendMessageToUser("Type /help to see available commands.");
                sendMessageToUser(" ");

                System.out.println(user.getName()+":"+user.getSocket().getInetAddress().getHostAddress()+" joined the server");   

                // Start communication loop
                while(true) {
                    if(user.getSocket().isClosed()) {
                        break;
                    }

                    String msg = user.getCommunication().receiveMessage();
                    if(msg.isEmpty() || msg.equals("\n")) {
                        continue;
                    }
                    //Handle possible command
                    ICommand cmd = null;
                    String msgbody = "";
                  
                    if(msg.charAt(0) == '/') {                     
                        String[] splittedMsg = msg.split(" ");
                        cmd = commands.get(splittedMsg[0]);  
                        int index = msg.indexOf(' ');
                        if(index != -1) {
                            msgbody = msg.substring(index+1);    
                        }
                        if(cmd == null) {
                            sendMessageToUser("Invalid command!");
                            continue;
                        }               
                    } else {
                        msgbody = msg;
                    }
                    // if msg was command, execute it       
                    if(cmd!=null) {
                        cmd.execute(this,msgbody);
                        continue;
                    }
                    // Cut message if it is too long
                    if(msgbody.length() > ChatServer.serverSettings.getMaxMessageLength()) {
                        msgbody = msgbody.substring(0, ChatServer.serverSettings.getMaxMessageLength());
                    }

                    // If msg was not command, it is message for other users.
                    sendMessageToCurrentRoom(msgbody, user.getName());
                }
            } catch (Exception e) {
                System.out.println(e);
            } finally {
                Room r = user.getCurrentRoom();
                if(r != null) {
                    r.users.remove(user);
                    user.setCurrentRoom(null);
                }
                users.remove(user);
                System.out.println(user.getName()+":"+user.getSocket().getInetAddress().getHostAddress() + " left the server!");
            }
        }
        /**
         * Check if user is allowed to join the server.
         * Order: userlimit, username, admin, password, banned
         */
        private void authenticate() {       	
            try {
                //Check if server is full
                if(ChatServer.users.size() >= ChatServer.serverSettings.getMaxUsers()) {
                    user.getCommunication().sendMessage("Server is full!");
                    user.getSocket().close();
                }
                //Username
                user.getCommunication().sendMessage("Please give your nickname and the server password (if any)");
                String msg = user.getCommunication().receiveMessage();
                if(msg.isEmpty()) {
                    user.getCommunication().sendMessage("Error");
                    user.getSocket().close();
                }
                
                String[] s_msg = msg.split(" ");
                user.setName(s_msg[0]);
                if(user.getName().equals("SERVER")) {
                    user.getCommunication().sendMessage("Error");
                    user.getSocket().close();
                }
                if(isIllegalName(user.getName())) {
                    user.getCommunication().sendMessage("Your username contains invalid characters!");
                    user.getSocket().close();
                }

                //Check admin
                if(s_msg.length > 1) {
                    String pass = s_msg[1];
                    if(pass.equals(serverSettings.getServerAdminPassword())) {
                        System.out.println("Admin user");
                        sendMessageToUser("You are server admin");
                        user.setMode(3);
                    }
                } 

                //Server password if any. Don't check password if user has given admin password
                if(serverSettings.getServerPassword() != "" && user.getMode() < 3) {
                    if(s_msg.length > 1) {
                        String pass = s_msg[1];
                        if(!pass.equals(serverSettings.getServerPassword())) {
                            user.getCommunication().sendMessage("Wrong password");
                            user.getSocket().close();
                        }
                    } else {
                        user.getCommunication().sendMessage("This server is password protected!");
                        user.getSocket().close();
                    }
                }
          
                // Check ban
                if(isBanned(user.getSocket().getInetAddress().getHostAddress())) {
                    if(user.getMode() < 3) {
                        user.getCommunication().sendMessage("You are banned from the server!");
                        user.getSocket().close();
                    }
                }
                //Success, welcome
                users.add(user);             
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        /**
         * Send message to every user in the room.
         * @param msg Message to be sent
         * @param MsgSender Who sent the message
         */
        public void sendMessageToCurrentRoom(String msg, String MsgSender) {
            Room room = user.getCurrentRoom();
            if(room == null) {
                sendMessageToUser("You are not in room! Use /joinroom to join a room!");
                return;
            }
            try {
                if ((MsgSender.equals("SERVER"))) {
                    System.out.println("Message to be sent: "+msg);
                    for(User u : room.users) {
                        u.getCommunication().sendMessage("** " + msg + " **");
                    }
                } else {
                    // Check if user is muted
                    if(room.isMuted(user.getSocket().getInetAddress().getHostAddress(), user.getName())) {
                        // Can't mute room moderator and up
                        if(user.getMode()<1) {
                            sendMessageToUser("You are muted");
                            return;
                        }
                    }
                    LocalDateTime date = new Timestamp(System.currentTimeMillis()).toLocalDateTime();
                    String timestr =    "["+String.format("%02d",date.getHour()) +
                                        ":" + String.format("%02d",date.getMinute()) +
                                        ":"+ String.format("%02d",date.getSecond())+"] ";
                    //System.out.println("Message to be sent: "+msg);
                    for(User u : room.users) {
                        u.getCommunication().sendMessage(timestr + MsgSender + ": " + msg);
                    }
                }
            } catch (Exception e) {
                System.out.println("sendMessageToRoom: " + e);
            }

        }
        /**
         * Send message directly to the user.
         * @param msg Message to be sent
         */
        public void sendMessageToUser(String msg) {
            try {
                user.getCommunication().sendMessage(msg);
            } catch (Exception e) {
                //TODO: handle exception
            }       	
        }
        /**
         * Send message to everyone with mode minmode in the room.
         * @param msg Message to be sent
         * @param minMode Mode that someone must have to receive the message
         */
        public void sendMessageToModeRoom(String msg, int minMode) {
            try {
                for(User u : user.getCurrentRoom().users) {
                    if(u.getMode() >= minMode)
                        u.getCommunication().sendMessage(msg);
                }                
            } catch (Exception e) {
                //TODO: handle exception
            } 
        }
        /**
         * Send message to everyone with mode minmode in the server.
         * @param msg Message to be sent
         * @param minMode Mode that someone must have to receive the message
         */
        public void sendMessageToModeServer(String msg, int minMode) {
            try {
                for(User u : ChatServer.users) {
                    if(u.getMode() >= minMode)
                        u.getCommunication().sendMessage(msg);
                }                
            } catch (Exception e) {
                //TODO: handle exception
            } 
        }
        public void sendMessageToAll(String msg) {
            try {
                for(User u : ChatServer.users) {
                    u.getCommunication().sendMessage(msg);
                }
            } catch (Exception e) {
                //TODO: handle exception
            }
        }
        /**
         * Check if address is banned.
         * address:(username):(reason)
         * @param address ip address
         * @return was the address banned
         */
        public boolean isBanned(String address) {
            for(String ba : ChatServer.serverSettings.getBannedAddresses()) {
                String[] splitted = ba.split(":");
                if(splitted[0].equals(address)) {
                    return true;
                }
            }
            return false;
        }
        /**
         * Check if username does not contain illegal characters.
         * @param name Name to be checked for illegal characters
         * @return was the name illegal
         */
        public boolean isIllegalName(String name) {
            String invalidChars = " .:=/\\\'\"";
            for(int i=0;i<name.length();i++) {
                char c = name.charAt(i);
                int index = invalidChars.indexOf(c);
                if(index != -1) {
                    return true;
                }
            }
            return false;
        }
    }

}