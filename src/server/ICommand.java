package server;
/**
 * ICommand. Interface for commands. Commands should be independent.
 */
public interface ICommand {
    public void execute(ChatServer.ChatServerThread chatServerThread, String msg);
    public String getInfo();
}