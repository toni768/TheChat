package server;

import java.net.Socket;

import shared.ICommunication;
import shared.DefaultCommunication;
/**
 * Class for the user.
 */
public class User {
    private Socket socket;
    private String name;
    private Room currentRoom;
    private ICommunication communication;
    private int mode; // 0=user, 1=room moderator, 2=room admin, 3=server admin

    public User(Socket s) {
        this.socket = s;
        this.name = "Default";
        this.currentRoom = null;
        this.communication = new DefaultCommunication(s);
        this.mode = 0;      
    }
    // Synchronized getters and setters
    synchronized public String getName() {
        return this.name;
    }
    synchronized public void setName(String name) {
        this.name = name;
    }

    synchronized public Socket getSocket() {
        return this.socket;
    }
    synchronized public void setSocket(Socket s) {
        this.socket = s;
    }

    synchronized public Room getCurrentRoom() {
        return this.currentRoom;
    }
    synchronized public void setCurrentRoom(Room room) {
        this.currentRoom = room;
    }

    synchronized public ICommunication getCommunication() {
        return this.communication;
    }

    synchronized public int getMode() {
        return this.mode;
    }
    synchronized public void setMode(int mode) {
        this.mode = mode;
    }
}