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
 * Class containing settings for the server
 */
public class ServerSettings {
    //From config.ini
    private String name;
    private String description;
    private String serverPassword;
    private String serverAdminPassword;
    private String rules;
    private String motd;

    private String[] roomNames;

    private int maxUsers;
    private int port;
    private int maxMessageLength;
    
    private boolean logging;

    //From bans.txt
    private List<String> bannedAddresses;

    private String configFilePath = "bin/server/config.ini";
    private String banPath = "bin/server/bans.txt";

    public ServerSettings() {

    }

    public void load() {
        File configFile = new File(configFilePath);
        File banFile = new File(banPath);

        Scanner reader = null;
        HashMap<String,String> settings = new HashMap();
        bannedAddresses = Collections.synchronizedList(new ArrayList<String>());

        try {
            configFile.createNewFile();
            banFile.createNewFile();
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
                    //System.out.println(splitted[0]+"="+"");
                    settings.put(splitted[0],"");   
                } else {
                    //System.out.println(splitted[0]+"="+splitted[1]);
                    settings.put(splitted[0],splitted[1]);   
                }          
            }
            System.out.println("");
            reader.close(); // Is this safe?
            reader = new Scanner(banFile);
            while(reader.hasNextLine()) {
                String line = reader.nextLine().trim();
                if(line.isEmpty()) break;
                bannedAddresses.add(line);
                System.out.println(line);
            }
        } catch (Exception e) {
            System.out.println("Exception occurred: "+e);
        } finally {
            if(reader != null) {
                reader.close();
            }
            name = settings.getOrDefault("name", "DefaultServerName");
            description = settings.getOrDefault("description", "DefaultDescription");
            serverPassword = settings.getOrDefault("serverPassword", "");
            serverAdminPassword = settings.getOrDefault("serverAdminPassword", "admin");
            rules = settings.getOrDefault("rules", "No rules");
            motd = settings.getOrDefault("motd", "Welcome");
        
            roomNames = settings.getOrDefault("roomNames", "room1").split(",");
        
            maxUsers = Integer.parseInt(settings.getOrDefault("maxUsers", "256"));
            port = Integer.parseInt(settings.getOrDefault("port", "8000"));
            maxMessageLength = Integer.parseInt(settings.getOrDefault("maxMessageLength", "2000"));
            
            logging = Boolean.parseBoolean(settings.getOrDefault("logging", "true"));

            // Save
            saveSettings();
        }
        System.out.println("ServerSettings loaded!");
    }

    synchronized public void saveSettings() {
        File configFile = new File(configFilePath);
        File banFile = new File(banPath);
        PrintWriter pw = null;
        // Awful
        try {
            pw = new PrintWriter(configFile);
            pw.println("name"+"="+this.name);
            pw.println("description"+"="+this.description);
            pw.println("serverPassword"+"="+this.serverPassword);
            pw.println("serverAdminPassword"+"="+this.serverAdminPassword);
            pw.println("rules"+"="+this.rules);
            pw.println("motd"+"="+this.motd);
            pw.println("roomNames"+"="+arrayToString(roomNames, ","));
            pw.println("maxUsers"+"="+this.maxUsers);
            pw.println("port"+"="+this.port);
            pw.println("maxMessageLength"+"="+this.maxMessageLength);
            pw.println("logging"+"="+this.logging);

            pw.close();
            pw = new PrintWriter(banFile);
            for(String addr : bannedAddresses) {
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

    String arrayToString(String[] array,String end) {
        String out = "";
        for(String s : array) {
            out += s+end;
        }
        if(end != "") {
            return out.substring(0, out.length()-1);
        } else {
            return out;
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

    synchronized public String getServerPassword() {
        return this.serverPassword;
    }
    synchronized public void setServerPassword(String pass) {
        this.serverPassword = pass;
        saveSettings();
    }

    synchronized public String getServerAdminPassword() {
        return this.serverAdminPassword;
    }
    synchronized public void setServerAdminPassword(String pass) {
        this.serverAdminPassword = pass;
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
    
    synchronized public String[] getRoomNames() {
        return this.roomNames;
    } // No setter

    synchronized public int getMaxUsers() {
        return this.maxUsers;
    }
    synchronized public void setMaxUsers(int max) {
        this.maxUsers = max;
        saveSettings();
    }

    synchronized public int getPort() {
        return this.port;
    }

    synchronized public int getMaxMessageLength() {
        return this.maxMessageLength;
    }

    synchronized public void setMaxMessageLength(int max) {
        this.maxMessageLength = max;
        saveSettings();
    }
  
    synchronized public List<String> getBannedAddresses() {
        return this.bannedAddresses;
    }
}