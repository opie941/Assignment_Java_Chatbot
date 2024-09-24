package com.example.chat_assignment;

import javafx.application.Platform;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ChatServer2 extends Chat2 {

    public static final int SERVER_PORT = 1234;

    public static void main(String[] args) {
        new Thread(() -> {
            try {
                DatagramSocket serverSocket = new DatagramSocket(SERVER_PORT);
                byte[] receiveBuffer = new byte[1024];
                System.out.println("UDP Server is active on port: " + SERVER_PORT);

                while (true) {
                    DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                    serverSocket.receive(receivePacket);

                    String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    System.out.println("Message from client: " + message);

                    String response = "Message received: " + message;
                    byte[] responseData = response.getBytes();



                    DatagramPacket sendPacket = new DatagramPacket(
                            responseData,
                            responseData.length,
                            receivePacket.getAddress(),
                            receivePacket.getPort()
                    );
                    serverSocket.send(sendPacket);

                    // Nachricht in der Chat-Box anzeigen
                    Platform.runLater(() -> chatMessages.add(message));

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
