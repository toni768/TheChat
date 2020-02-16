package server;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Integer;
/**
 * Class containing settings for the room
 */
public class RoomSettings {

    //From config.ini
    private String name;
    private String description;
    private String roomPassword;
    private String roomModeratorPassword;
    private String roomAdminPassword;
    private String rules;
    private String motd;

    private int maxUsers;
    
    //From bans.txt
    private List<String> bannedAddresses;
    //From mutes.txt
    private List<String> mutedAddresses;

    private String path = "bin/server/";
    private String configPath;
    private String banPath;
    private String mutePath;
    public RoomSettings() {

    }
    // Load settings from the files or create new files
    public void load(String roomName) {
        path += roomName+"/";
        configPath = path+"config.ini";
        banPath = path+"bans.txt";
        mutePath = path+"mutes.txt";
        File folder = new File(path);
        File configFile = new File(configPath);
        File banFile = new File(banPath);
        File muteFile = new File(mutePath);

        Scanner reader = null;
        HashMap<String,String> settings = new HashMap();
        bannedAddresses = Collections.synchronizedList(new ArrayList<String>());
        mutedAddresses = Collections.synchronizedList(new ArrayList<String>());

        try {
            if(!folder.exists()) {
                folder.mkdir();
            }
            configFile.createNewFile();
            banFile.createNewFile();
            muteFile.createNewFile();
        } catch (Exception e) {
            System.out.println(e);
        }

        try {
            reader = new Scanner(configFile);
            // Note: long config lines eg. rules
            while(reader.hasNextLine()) {
                String line = reader.nextLine().trim();
                if(line.isEmpty()) break;

                String[] splitted = line.split("=");
                if(splitted.length==1) {
                    System.out.println(splitted[0]+"="+"");
                    settings.put(splitted[0],"");   
                } else {
                    System.out.println(splitted[0]+"="+splitted[1]);
                    settings.put(splitted[0],splitted[1]);   
                }          
            }
            System.out.println("");
            reader.close(); // Is this safe?
            // Load bans
            reader = new Scanner(banFile);
            while(reader.hasNextLine()) {
                String line = reader.nextLine().trim();
                if(line.isEmpty()) break;
                bannedAddresses.add(line);
                System.out.println(line);
            }
            System.out.println("");
            reader.close(); // Is this safe?
            // Load muted
            reader = new Scanner(muteFile);
            while(reader.hasNextLine()) {
                String line = reader.nextLine().trim();
                if(line.isEmpty()) break;
                mutedAddresses.add(line);
                System.out.println(line);
            }
        } catch (Exception e) {
            System.out.println("Exception occurred: "+e);
        } finally {
            if(reader != null) {
                reader.close();
            }
            // Put loaded values to variables or put default values if something was not found
            name = settings.getOrDefault("name", roomName);
            description = settings.getOrDefault("description", "DefaultDescription");
            roomPassword = settings.getOrDefault("roomPassword", "");
            roomModeratorPassword = settings.getOrDefault("roomModeratorPassword", "moderator");
            roomAdminPassword = settings.getOrDefault("roomAdminPassword", "admin");
            rules = settings.getOrDefault("rules", "No rules");
            motd = settings.getOrDefault("motd", "Welcome");
        
            maxUsers = Integer.parseInt(settings.getOrDefault("maxUsers", "128"));

            // Save
            saveSettings();
        }       
    }
    /**
     * Save settings
     */
    synchronized public void saveSettings() {
        File configFile = new File(configPath);
        File banFile = new File(banPath);
        File muteFile = new File(mutePath);

        PrintWriter pw = null;
        // Awful
        try {
            pw = new PrintWriter(configFile);
            pw.println("name"+"="+this.name);
            pw.println("description"+"="+this.description);
            pw.println("roomPassword"+"="+this.roomPassword);
            pw.println("roomModeratorPassword"+"="+this.roomModeratorPassword);
            pw.println("roomAdminPassword"+"="+this.roomAdminPassword);
            pw.println("rules"+"="+this.rules);
            pw.println("motd"+"="+this.motd);
            pw.println("maxUsers"+"="+this.maxUsers);


            pw.close();
            pw = new PrintWriter(banFile);
            for(String addr : bannedAddresses) {
                pw.println(addr);
            }
            pw.close();
            pw = new PrintWriter(muteFile);
            for(String addr : mutedAddresses) {
                pw.println(addr);
            }

        } catch (Exception e) {
            System.out.println("Exception in SaveSettings: "+e);
        } finally {
            if(pw!=null) {
                pw.close();
            }
        }
    }
    /**
     * Save banned users.
     */
    synchronized public void saveBannedUsers() {
        try {
            File banFile = new File(banPath);
            PrintWriter pw = new PrintWriter(banFile);
            for(String addr : bannedAddresses) {
                pw.println(addr);
            }
            pw.close();
        } catch (Exception e) {
            //TODO: handle exception
        }
    }
    /**
     * Save muted users.
     */
    synchronized public void saveMutedUsers() {
        try {
            File muteFile = new File(mutePath);
            PrintWriter pw = new PrintWriter(muteFile);
            for(String addr : mutedAddresses) {
                pw.println(addr);
            }
            pw.close();
        } catch (Exception e) {
            //TODO: handle exception
        }
    }   

        // Getters and setters
        synchronized public String getName() {
            return this.name;
        }
        synchronized public void setName(String name) {
            this.name = name;
            saveSettings();
        }   
    
        synchronized public String getDescription() {
            return this.description;
        }
        synchronized public void setDescription(String desc) {
            this.description = desc;
            saveSettings();
        }
    
        synchronized public String getRoomPassword() {
            return this.roomPassword;
        }
        synchronized public void setRoomPassword(String pass) {
            this.roomPassword = pass;
            saveSettings();
        }

        synchronized public String getRoomModeratorPassword() {
            return this.roomModeratorPassword;
        }
        synchronized public void setRoomModeratorPassword(String pass) {
            this.roomModeratorPassword = pass;
            saveSettings();
        }  

        synchronized public String getRoomAdminPassword() {
            return this.roomAdminPassword;
        }
        synchronized public void setRoomAdminPassword(String pass) {
            this.roomAdminPassword = pass;
            saveSettings();
        }
    
        synchronized public String getRules() {
            return this.rules;
        }
        synchronized public void setRules(String text) {
            this.rules = text;
            saveSettings();
        }
    
        synchronized public String getMotd() {
            return this.motd;
        }
        synchronized public void setMotd(String text) {
            this.motd = text;
            saveSettings();
        }
            
        synchronized public int getMaxUsers() {
            return this.maxUsers;
        }
        synchronized public void setMaxUsers(int max) {
            this.maxUsers = max;
            saveSettings();
        }
      
        synchronized public List<String> getBannedAddresses() {
            return this.bannedAddresses;
        }
        synchronized public List<String> getMutedAddresses() {
            return this.mutedAddresses;
        }       

}