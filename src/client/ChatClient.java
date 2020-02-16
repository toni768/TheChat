package client;

import java.net.Socket;
import java.util.Optional;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.FontWeight;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Pair;
import shared.ICommunication;
import shared.DefaultCommunication;
/**
 * ChatClient
 * @author Toni Luukkonen
 */
public class ChatClient extends Application {
	/**
	 * Communication interface
	 */
	public static ICommunication communication = null;
	/**
	 * Text area for receiving messages
	 */
	public static TextArea taMessages;
	/**
	 * Socket
	 */
	static Socket socket;
	/**
	 * Thread for receiving messages into taMessages
	 */
	MsgRec rec;

	public void start(Stage primaryStage) {
		
        MenuBar menu = new MenuBar(); // Toolbar

        Menu menu1 = new Menu("Connect..."); // First menu on toolbar
        menu.getMenus().add(menu1);
        Menu menu2 = new Menu("Help"); // Second menu on toolbar
        menu.getMenus().add(menu2);
        
        MenuItem menuItem = new MenuItem("Quick connect");
        MenuItem menuItem2 = new MenuItem("Connect to a server");
        MenuItem menuItem3 = new MenuItem("Disconnect");
        MenuItem menuItem4 = new MenuItem("Exit");
//        menu1.getItems().add(menuItem); UNCOMMENT FOR FASTER ADMIN TESTING
        menu1.getItems().add(menuItem2);
        menu1.getItems().add(menuItem3);
        menu1.getItems().add(menuItem4);

        MenuItem menu2Item2 = new MenuItem("About");
        menu2.getItems().add(menu2Item2);
        
        // Text area for receiving messages from the server
        taMessages = new TextArea("\nPlease connect to a server to start chatting with other people.");
        taMessages.setEditable(false);
        taMessages.setWrapText(true);
        taMessages.setStyle("-fx-font-size: 14px;");
       
        // Text area for sending messages
        TextArea taInput = new TextArea();
        taInput.setStyle("-fx-font-size: 14px; -fx-padding: 5 3 5 3;");
        taInput.setPrefRowCount(5);
        taInput.setWrapText(true);
        
        BorderPane border = new BorderPane();
        border.setTop(menu);
        border.setCenter(taMessages);
        border.setBottom(taInput);
        
        Scene mainFrame = new Scene(border, 800,600);

        primaryStage.setTitle("Chat");
        primaryStage.setScene(mainFrame);
        primaryStage.centerOnScreen();
        primaryStage.show();
        
        // Quick connect with username "Admin" and admin privilege, and join room1
        // Uncomment line 77 to use
        menuItem.setOnAction(e->{ 
    		String ip = "localhost";
    		int port = 8000;
            try {
            	rec = new MsgRec();
            	socket = new Socket(ip, port);
                System.out.println("Connected to " + ip + " " + port);
                communication = new DefaultCommunication(socket);
                rec.start();
                communication.sendMessage("Admin admin");
                communication.sendMessage("/joinroom room1");
                taMessages.setText("");

            } catch (Exception error) {
            	Alert alert = new Alert(AlertType.INFORMATION);
            	alert.setX(primaryStage.getX() + 260);
            	alert.setY(primaryStage.getY() + 200);
            	alert.setTitle("Error");
            	alert.setHeaderText(null);
            	alert.setContentText("Could not connect to " + ip + " " + port);
            	alert.showAndWait();
            }
        });
        
        // Normal connect
        menuItem2.setOnAction(e->{
	        Dialog<Pair<String, String>> dialog = new Dialog<>();
	        dialog.setTitle("Connect to a server");
        	dialog.setX(primaryStage.getX() + 260);
        	dialog.setY(primaryStage.getY() + 200);
	
	        ButtonType connect = new ButtonType("Connect", ButtonData.OK_DONE);
	        ButtonType cancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
	        dialog.getDialogPane().getButtonTypes().addAll(connect, cancel);
	
	        GridPane gridPane = new GridPane();
	        gridPane.setHgap(10);
	        gridPane.setVgap(10);
	        gridPane.setPadding(new Insets(20, 20, 10, 10));
	

	        TextField ip = new TextField("");
	        TextField port = new TextField("");
	        TextField username = new TextField("");
	        TextField password = new TextField("");
	        // thing, column, row
	        gridPane.add(new Label("You must give server IP, port and username.\nPassword is optional."), 1, 0);
	        gridPane.add(new Label("IP:"), 0, 1);
	        gridPane.add(ip, 1, 1);
	        gridPane.add(new Label("Port:"), 0, 2);
	        gridPane.add(port, 1, 2);
	        gridPane.add(new Label("Username:"), 0, 3);
	        gridPane.add(username, 1, 3);
	        gridPane.add(new Label("Server password:"), 0, 4);
	        gridPane.add(password, 1, 4);
	
	        dialog.getDialogPane().setContent(gridPane);
	        Platform.runLater(() -> ip.requestFocus());
        	Optional<Pair<String, String>> ipport = dialog.showAndWait();
        	
        	if (ipport.isPresent() && !username.getText().isEmpty()) {
	                try {
	                	rec = new MsgRec();
						socket = new Socket(ip.getText(), Integer.parseInt(port.getText()));
						String connectMsg = "Connected to " + ip.getText() + ":" + port.getText();
						System.out.println(connectMsg);

	                    communication = new DefaultCommunication(socket);
	                    communication.sendMessage(username.getText() + " " + password.getText());
	                    rec.start();
						taMessages.setText("");
						taMessages.appendText(connectMsg+"\n");
						taMessages.setScrollTop(Double.MAX_VALUE);
	                } catch (Exception error) {
	                	Alert alert = new Alert(AlertType.INFORMATION);
	                	alert.setX(primaryStage.getX() + 260);
	                	alert.setY(primaryStage.getY() + 200);
	                	alert.setTitle("Error");
	                	alert.setHeaderText(null);
	                	alert.setContentText("Could not connect to " + ip.getText() + " " + port.getText());
	                	alert.showAndWait();
	                }
	         }
        });
        
        // Disconnect
        menuItem3.setOnAction(e-> { 
        	try {
				communication.sendMessage("/quit");
        		socket.close();
        		taMessages.setText("\nPlease connect to a server to start chatting with other people.");
			} catch (Exception e1) {
			}
        });
        
        // Exit
        menuItem4.setOnAction(e-> { 
        	try {
				communication.sendMessage("/quit");
        		socket.close();
			} catch (Exception e1) {
			}
    		System.exit(0);
        });
        
        // About
        menu2Item2.setOnAction(e-> { 
        	Alert alert = new Alert(AlertType.INFORMATION);
        	alert.setX(primaryStage.getX() + 260);
        	alert.setY(primaryStage.getY() + 200);
        	alert.setTitle("About");
        	alert.setHeaderText("The Chat");
        	alert.setContentText("Made by Antti and Toni");
        	alert.showAndWait();
        });
        
        // Input text field
        taInput.setOnKeyPressed(e -> { 
            if(!e.getCode().equals(KeyCode.ENTER)) return;
            if(taInput.getText().isEmpty()) return;
            if(taInput.getLength() > 2000) {
            	Alert alert = new Alert(AlertType.INFORMATION);
            	alert.setTitle("Error");
            	alert.setHeaderText(null);
            	alert.setContentText("Message was too long! Maximum length is 2000 characters.");
            	alert.showAndWait();
                taInput.setText(taInput.getText(0, 2000));
            };
            String msg = taInput.getText();   
            try {
                communication.sendMessage(msg); // Send message to server
            } catch (Exception er) {
            	Alert alert = new Alert(AlertType.INFORMATION);
            	alert.setTitle("Error");
            	alert.setHeaderText(null);
            	alert.setContentText("Could not send a message to server");
            	alert.showAndWait();
            } 
            taInput.setText("");
		});
		
		primaryStage.setOnCloseRequest(event -> {
			try {
				communication.sendMessage("/quit");
				socket.close();
			} catch (Exception e) {
				//TODO: handle exception
			}

		});
	}
    /**
     * Main method.
     * @param args
     */
	public static void main(String[] args) {
		Application.launch(args);
    }
}