package shared;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
/**
 * This class handles message sending and receiving using
 * BufferedReader and PrintWriter.
 */
public class DefaultCommunication implements ICommunication {
    BufferedReader input;
    PrintWriter output;

    public DefaultCommunication(Socket socket) {
        try {
            output = new PrintWriter(socket.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (Exception e) {
            //TODO: handle exception
        }
    }

    @Override
    public void sendMessage(String msg) throws Exception {
        try {
            output.println(msg);
        } catch (Exception e) {
            throw new Exception("Exception occurred in sendMessage");
        }
    }

    @Override
    public String receiveMessage() throws Exception {
        try {
            return input.readLine();
        } catch (Exception e) {
            throw new Exception("Exception occurred in receiveMessage");
        }
    } 
}