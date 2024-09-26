package com.example.chat_assignment;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.application.Platform;

public class Chat2 extends Application {

    private DatagramSocket socket; // Gemeinsamer Socket für Senden und Empfangen

    public String nameClient = "Chat-Client1";
    private static final int CLIENT_PORT = 12345;  // Fester Port für den Client

    private final int WIDTH = 300;
    private final int HEIGHT = 200;

    private TextField textfield = new TextField();
    private Button chat_button = new Button("Send Message...");
    private Button delete_button = new Button("Delete Chat");

    // ListView und ObservableList für das Chatfenster
    public ListView<String> chatBox = new ListView<>();
    public static ObservableList<String> chatMessages = FXCollections.observableArrayList();

    @Override
    public void start(Stage primaryStage) {
        try {
            socket = new DatagramSocket(CLIENT_PORT);  // Socket beim Start erstellen
        } catch (Exception e) {
            e.printStackTrace();
        }

        primaryStage.setTitle(nameClient);
        setupChatWindow(primaryStage, textfield, chat_button, delete_button, chatBox, chatMessages, "MyChat");

        startReceivingMessages(); // Startet das Empfangen der Nachrichten vom Server
    }

    private void setupChatWindow(Stage stage, TextField textField, Button sendButton, Button deleteButton, ListView<String> chatBox, ObservableList<String> chatMessages, String title) {
        chatBox.setItems(chatMessages);

        sendButton.setOnAction(event -> {
            send_message_via_click(textField.getText()); // Nachricht senden
            textField.clear(); // Nach dem Senden Textfeld leeren
        });

        deleteButton.setOnAction(event -> {
            chatMessages.clear(); // Löscht alle Nachrichten aus der Chatbox
            textField.clear();    // Optional: Textfeld auch leeren
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

                    // Server-IP-Adresse und Port
                    String serverIp = getServerIp();
                    InetAddress serverAddress = InetAddress.getByName(serverIp);
                    int port = ChatServer2.SERVER_PORT; // Server-Port

                    DatagramPacket pack = new DatagramPacket(buffer, buffer.length, serverAddress, port);
                    socket.send(pack); // Benutze den gemeinsamen Socket
                    System.out.println("Message sent from client: " + nameClient + ": " + message);

                    // Lokale Nachricht anzeigen, bevor Antwort vom Server kommt
                    Platform.runLater(() -> chatMessages.add("You: " + message));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    // Server IP dynamisch erfragen
    private String getServerIp() {
        return "localhost"; // Ändere dies für die tatsächliche Netzwerkverbindung
    }

    // Startet einen neuen Thread, der auf Nachrichten vom Server wartet
    private void startReceivingMessages() {
        new Thread(() -> {
            try {
                byte[] buffer = new byte[1024];
                System.out.println("Client is ready to receive messages...");

                while (true) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet); // Nachricht vom Server empfangen
                    System.out.println(nameClient + " : Received message back from Server...");

                    String message = new String(packet.getData(), 0, packet.getLength());
                    Platform.runLater(() -> chatMessages.add(message)); // UI-Update
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
