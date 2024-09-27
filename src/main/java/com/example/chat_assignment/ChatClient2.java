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

public class ChatClient2 extends Application {

    private DatagramSocket socket; // socket for sending and receiving

    public String nameClient = "Huber";
    private static final int CLIENT_PORT = 12346;  // Hard coded port for Client1

    //Size of window of GUI
    private final int WIDTH = 300;
    private final int HEIGHT = 200;

    private TextField textfield = new TextField();
    private Button chat_button = new Button("Send Message...");
    private Button delete_button = new Button("Delete Chat");

    // ListView und ObservableList for the Chatmessages
    public ListView<String> chatBox = new ListView<>();
    public static ObservableList<String> chatMessages = FXCollections.observableArrayList();

    @Override
    public void start(Stage primaryStage) {
        try {
            socket = new DatagramSocket(CLIENT_PORT);  // create new socket for starting
        } catch (Exception e) {
            e.printStackTrace();
        }

        primaryStage.setTitle(nameClient);
        setupChatWindow(primaryStage, textfield, chat_button, delete_button, chatBox, chatMessages, "MyChat");

        startReceivingMessages(); // starts receiving messages back from the server
    }

    private void setupChatWindow(Stage stage, TextField textField, Button sendButton, Button deleteButton, ListView<String> chatBox, ObservableList<String> chatMessages, String title) {
        chatBox.setItems(chatMessages);

        sendButton.setOnAction(event -> {
            send_message_via_click(textField.getText()); // with this fuction , messages will be sent back
            textField.clear(); // textfield will be cleared after sending.
        });


        textField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                send_message_via_click(textField.getText()); // Nachricht senden
                textField.clear(); // Nach dem Senden Textfeld leeren
            }
        });


        deleteButton.setOnAction(event -> {
            chatMessages.clear(); // all messages in textfield will be closed
            textField.clear();
        });

        ScrollPane scrollPane = new ScrollPane(chatBox);
        scrollPane.setFitToWidth(true);

        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(scrollPane, textField, sendButton, deleteButton);

        Scene scene = new Scene(layout, WIDTH, HEIGHT);
        stage.setScene(scene);
        stage.show();
    }

    public void send_message_via_click(String message) {
        if (!message.isEmpty()) {
            new Thread(() -> {
                try {
                    byte[] buffer = message.getBytes();

                    // Server-IP-Adresse and Port
                    String serverIp = getServerIp();
                    InetAddress serverAddress = InetAddress.getByName(serverIp);
                    int port = com.example.chat_assignment.ChatServer_old.SERVER_PORT; // Server-Port

                    DatagramPacket pack = new DatagramPacket(buffer, buffer.length, serverAddress, port);
                    socket.send(pack);
                    System.out.println(nameClient + ": " + message);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    // asks for server ip
    private String getServerIp() {
        return "localhost"; // should be different in another network especially if its not local
    }

    // Starts a mew Thread, which is waiting for sent back messages of the server -> client
    private void startReceivingMessages() {

        new Thread(() -> {
            try {
                byte[] buffer = new byte[1024];
                System.out.println(nameClient + " is ready to receive messages...");

                while (true) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet); // Message receriving from server


                    String message = new String(packet.getData(), 0, packet.getLength());
                    Platform.runLater(() -> chatMessages.add(message)); // Updates GUI
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
