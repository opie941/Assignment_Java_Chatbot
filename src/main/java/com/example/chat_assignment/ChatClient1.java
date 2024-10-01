package com.example.chat_assignment;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.application.Platform;


//Class for a chat client which extends application
public class ChatClient1 extends Application {

    //Defines a datagram-socket
    private DatagramSocket SOCKET;

    //Hard coded name of the chat client
    public String CLIENT_NAME = "Mueller";

    //Hard coded port of the chat client #exampleport
    private static final int CLIENT_PORT = 12345;

	//Set the height and the width of the gui
    private final int WIDTH = 300;
    private final int HEIGHT = 200;

    //Initiates elements of the GUI: Textfield for entering text, buttons the send and delete messages
    private TextField TEXTFIELD = new TextField();
    private Button CHAT_BUTTON = new Button("Send Message...");
    private Button DELETE_BUTTON = new Button("Delete Chat");


    // Creates new elements for the chatbox and chatmessages
    public ListView<String> CHATBOX = new ListView<>();
    public static ObservableList<String> CHAT_MESSAGES = FXCollections.observableArrayList();


    //Start method to set up the stage
    @Override
    public void start(Stage primaryStage) {
        try {
            SOCKET = new DatagramSocket(CLIENT_PORT);  // create new socket for starting
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Sets the title to the stage
        primaryStage.setTitle(CLIENT_NAME);

        //Calls the function setupChatWindow with the elements in the variables
        setupChatWindow(primaryStage, TEXTFIELD, CHAT_BUTTON, DELETE_BUTTON, CHATBOX, CHAT_MESSAGES, "Chat");

        //Calls the function: startReceivingMessages to receive messages back from the server
        startReceivingMessages();
    }

    //This function is used to setup the GUI
    private void setupChatWindow(Stage stage, TextField textField, Button sendButton, Button deleteButton, ListView<String> chatBox, ObservableList<String> chatMessages, String title) {
        chatBox.setItems(chatMessages);

        //Event handler for the sending button of the GUI to send messages via click -> calls send_message_via_click function by clicking
        sendButton.setOnAction(event -> {
            send_message_via_click(textField.getText());
            textField.clear(); // clears text field after
        });

        //This event handler makes it possible to send messages by a keyboard enter
        textField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                send_message_via_click(textField.getText());
                textField.clear();
            }
        });

        //This event handler clears the whole chat by clicking the delete button
        deleteButton.setOnAction(event -> {
            chatMessages.clear();
            textField.clear();
            System.out.println("Chat has beeen deleted...");
        });

        //Enables scrolling if the chat window gets to small for the messages
        ScrollPane scrollPane = new ScrollPane(chatBox);
        scrollPane.setFitToWidth(true);

        //Creates VBox for the GUI
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(scrollPane, textField, sendButton, deleteButton);

        //Sets up scene for the GUI -> sets width and height
        Scene scene = new Scene(layout, WIDTH, HEIGHT);
        stage.setScene(scene);
        stage.show();
    }

    //Function is called to send messages to the server
    public void send_message_via_click(String message) {
        if (!message.isEmpty()) {
            //Creates a new thread
            new Thread(() -> {
                try {
                    //Creates a needed buffer
                    byte[] buffer = message.getBytes();

                    // To find out the server ip the getServerIp Function is called
                    String serverIp = getServerIp();
                    InetAddress serverAddress = InetAddress.getByName(serverIp);
                    //stores the server port in the variable port
                    int port = ChatServer.SERVER_PORT;

                    DatagramPacket pack = new DatagramPacket(buffer, buffer.length, serverAddress, port);
                    SOCKET.send(pack);
                    System.out.println(CLIENT_NAME + ": " + message);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    // This function gives back the server ip-adress, in this use case its just localhost
    private String getServerIp() {
        return "localhost";
    }


    //This function is used to receive decoded messages back from the server and to update the chat field of the GUI
    private void startReceivingMessages() {
        // Starts a new thread
        new Thread(() -> {
            try {
                //Creates a buffer
                byte[] buffer = new byte[1024];
                while (true) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    SOCKET.receive(packet); // Receiving messages back from the server

                    //Encodes the data to string
                    String message = new String(packet.getData(), 0, packet.getLength());
                    //Updates the GUI
                    Platform.runLater(() -> CHAT_MESSAGES.add(message)); // Updates GUI
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start(); //Starts the thread
    }

    //Launches the application
    public static void main(String[] args) {
        launch(args);
    }
}
