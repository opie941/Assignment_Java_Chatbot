package com.example.chat_assignment;

import javafx.application.Platform;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ChatServer extends Chat {

    public static final int SERVER_PORT = 1234;

    public static void main(String[] args) {
        new Thread(() -> {
            try {
                DatagramSocket serverSocket = new DatagramSocket(SERVER_PORT);
                byte[] receiveBuffer = new byte[1024];
                System.out.println("UDP Server is active on port:  " + SERVER_PORT);

                while (true) {
                    DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                    serverSocket.receive(receivePacket);

                    String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    System.out.println("Message sent from one of the clients: " + message);

                    Platform.runLater(() -> {
                        chatMessages1.add(message); // Fügt zur ersten Chatbox hinzu
                        chatMessages2.add(message); // Fügt zur zweiten Chatbox hinzu
                    });

                    String response = "message have been received: " + message;
                    byte[] responseData = response.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(
                            responseData,
                            responseData.length,
                            receivePacket.getAddress(),
                            receivePacket.getPort()
                    );
                    serverSocket.send(sendPacket);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
