package com.example.chat_assignment;

import java.net.InetAddress;
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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;

public class Chat extends Application {

    public String nameClient1 = "Chat-Client-1";
    public String nameClient2 = "Chat-Client-2";
    private static final int CLIENT_PORT = 1234; // Anderer Port für den Client

    private final int WIDTH = 300;
    private final int HEIGHT = 200;

    private TextField textfield1 = new TextField();
    private TextField textfield2 = new TextField();

    private Button chat_button1 = new Button("Send Message...");
    private Button chat_button2 = new Button("Send Message...");

    private Button delete_button1 = new Button("Delete Chat");
    private Button delete_button2 = new Button("Delete Chat");

    // ListView und ObservableLists für Chatfenster
    public ListView<String> chatBox1 = new ListView<>();
    public ListView<String> chatBox2 = new ListView<>();
    public static ObservableList<String> chatMessages1 = FXCollections.observableArrayList();
    public static ObservableList<String> chatMessages2 = FXCollections.observableArrayList();

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle(nameClient1);
        setupChatWindow(primaryStage, textfield1, chat_button1, delete_button1, chatBox1, chatMessages1, "Chatfenster 1");

        Stage secondStage = new Stage();
        secondStage.setTitle(nameClient2);
        setupChatWindow(secondStage, textfield2, chat_button2, delete_button2, chatBox2, chatMessages2, "Chatfenster 2");

        startTextMonitoring();
    }

    private void setupChatWindow(Stage stage, TextField textField, Button sendButton, Button deleteButton, ListView<String> chatBox, ObservableList<String> chatMessages, String title) {
        chatBox.setItems(chatMessages);

        sendButton.setOnAction(event -> {
            send_message_via_click(textField.getText()); // Nachricht senden
            textField.clear(); // Nach dem Senden Textfeld leeren
        });

        textField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                send_message_via_click(textField.getText()); // Nachricht senden
                textField.clear(); // Nach dem Senden Textfeld leeren
            }
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
            try {
                DatagramSocket socket = new DatagramSocket();
                byte[] buffer = message.getBytes();
                InetAddress serverAddress = InetAddress.getByName("localhost");
                int port = ChatServer_old.SERVER_PORT; // Port des Servers

                DatagramPacket pack = new DatagramPacket(buffer, buffer.length, serverAddress, port);
                socket.send(pack);
                System.out.println("Message has been sent from Client");



                // Nachricht in beiden Chat-Boxen anzeigen
                chatMessages1.add(message);
                chatMessages2.add(message);


                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void startTextMonitoring() {
        new Thread(() -> {
            boolean wasText1Empty = true;  // Zustand des Textfeldes 1
            boolean wasText2Empty = true;  // Zustand des Textfeldes 2
            String texting_message = "Chatpartner is writing...";

            while (true) {
                String current_text1 = textfield1.getText();
                String current_text2 = textfield2.getText();

                // Überprüfen, ob sich der Zustand von Textfeld 1 geändert hat
                if (current_text1.isEmpty() && !wasText1Empty) {
                    wasText1Empty = true;
                    // UI-Update: Nachricht entfernen, dass der Chatpartner schreibt
                    Platform.runLater(() -> {
                        chatMessages2.remove(texting_message);
                    });
                } else if (!current_text1.isEmpty() && wasText1Empty) {
                    wasText1Empty = false;
                    // UI-Update: Nachricht hinzufügen, dass der Chatpartner schreibt
                    Platform.runLater(() -> {
                        if (!chatMessages2.contains(texting_message)) {
                            chatMessages2.add(texting_message);
                        }
                    });
                }

                // Überprüfen, ob sich der Zustand von Textfeld 2 geändert hat
                if (current_text2.isEmpty() && !wasText2Empty) {
                    wasText2Empty = true;
                    // UI-Update: Nachricht entfernen, dass der Chatpartner schreibt
                    Platform.runLater(() -> {
                        chatMessages1.remove(texting_message);
                    });
                } else if (!current_text2.isEmpty() && wasText2Empty) {
                    wasText2Empty = false;
                    // UI-Update: Nachricht hinzufügen, dass der Chatpartner schreibt
                    Platform.runLater(() -> {
                        if (!chatMessages1.contains(texting_message)) {
                            chatMessages1.add(texting_message);
                        }
                    });
                }

                // CPU schonen - kleine Pause zwischen den Iterationen
                try {
                    Thread.sleep(500); // 500 Millisekunden warten
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start(); // Startet den Thread
    }

    public static void main(String[] args) {
        launch(args);
    }
}
