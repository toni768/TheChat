package server;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
/**
 * Room class. 
 */
public class Room {
    public RoomSettings roomSettings;
    public List<User> users;

    public Room() {

    }

    public void initRoom(String roomName) {
        roomSettings = new RoomSettings();
        roomSettings.load(roomName);

        users = Collections.synchronizedList(new ArrayList<User>());
    }
    /**
     * Check if user/address is banned
     * address:username:(reason)
     * @param address ip address
     * @param username user's name
     * @return is user/address banned
     */
    public boolean isBanned(String address, String username) {
        for(String ba : roomSettings.getBannedAddresses()) {
            String[] splitted = ba.split(":");
            if(splitted[0].equals(address)) {
                return true;
            }
            if(splitted[1].equals(username)) {
                return true;
            }
        }
        return false;
    }
    /**
     * Check if address is banned
     * @param address ip address
     * @return is address banned
     */
    public boolean isAddressBanned(String address) {
        for(String ba : roomSettings.getBannedAddresses()) {
            String[] splitted = ba.split(":");
            if(splitted[0].equals(address)) {
                return true;
            }
        }
        return false;
    }
    /**
     * Check if username is banned
     * @param username user's name
     * @return is user banned
     */
    public boolean isUsernameBanned(String username) {
        for(String ba : roomSettings.getBannedAddresses()) {
            String[] splitted = ba.split(":");
            if(splitted[1].equals(username)) {
                return true;
            }
        }
        return false;
    }
    /**
     * Check if user/address is muted
     * @param address ip addres
     * @param username username
     * @return is user muted
     */
    public boolean isMuted(String address, String username) {
        for(String ba : roomSettings.getMutedAddresses()) {
            String[] splitted = ba.split(":");
            if(splitted[0].equals(address)) {
                return true;
            }
            if(splitted[1].equals(username)) {
                return true;
            } 
        }
        return false;
    }
    /**
     * Get user from the room using name.
     * @param name user's name
     * @return User or null
     */
    public User getUser(String name) {
        for(User u : users) {
            if(u.getName().equals(name)) {
                return u;
            }
        }
        return null;
    }
}