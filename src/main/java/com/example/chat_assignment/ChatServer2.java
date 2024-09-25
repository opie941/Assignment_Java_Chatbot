package com.example.chat_assignment;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class ChatServer2 {

    public static final int SERVER_PORT = 1234;

    // Liste der verbundenen Clients (IP-Adresse und Port)
    private static List<InetSocketAddress> clientList = new ArrayList<>();

    public static void main(String[] args) {
        new Thread(() -> {
            try {
                DatagramSocket serverSocket = new DatagramSocket(SERVER_PORT);
                byte[] receiveBuffer = new byte[1024];
                System.out.println("UDP Server is active on port: " + SERVER_PORT);

                while (true) {
                    DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                    serverSocket.receive(receivePacket);  // Nachricht vom Client empfangen

                    // Nachricht in String umwandeln
                    String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    InetSocketAddress clientAddress = new InetSocketAddress(receivePacket.getAddress(), receivePacket.getPort());

                    System.out.println("Message from client: " + message);

                    // Adresse des Clients speichern, falls nicht schon vorhanden
                    if (!clientList.contains(clientAddress)) {
                        clientList.add(clientAddress);
                    }

                    // Nachricht an alle Clients weiterleiten
                    String response = "Client " + clientAddress + ": " + message;
                    byte[] responseData = response.getBytes();

                    for (InetSocketAddress client : clientList) {
                        DatagramPacket sendPacket = new DatagramPacket(
                                responseData,
                                responseData.length,
                                client.getAddress(),
                                client.getPort()
                        );
                        serverSocket.send(sendPacket);  // Nachricht an alle Clients senden
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
