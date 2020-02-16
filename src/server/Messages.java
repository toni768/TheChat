package server;
/**
 * Messages. Messages which are used often.
 * 
 */
public class Messages {

    public static String errorMessage(String error) {
        return "Error occurred: "+error;
    }

    public static String notInRoomMessage() {
        return "You need to be in room in order to use this command!";
    }

    public static String permissionDeniedMessage() {
        return "You do not have permission to use this command!";
    }

    public static String wrongPasswordMessage() {
        return "Wrong password!";
    }    
}