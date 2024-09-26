package com.example.chat_assignment;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class ChatServer2 {

    public static final int SERVER_PORT = 1234;

    // Liste als String-Adressen speichern, um Vergleich zu erleichtern
    private static List<String> clientList = new ArrayList<>();

    public static void main(String[] args) {
        new Thread(() -> {
            try {
                DatagramSocket serverSocket = new DatagramSocket(SERVER_PORT);
                byte[] receiveBuffer = new byte[1024];
                System.out.println("UDP Server is active on port: " + SERVER_PORT);

                while (true) {
                    DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                    serverSocket.receive(receivePacket);  // Nachricht vom Client empfangen

                    // Client-Adresse als String
                    String clientAddressString = receivePacket.getAddress().toString() + ":" + receivePacket.getPort();
                    System.out.println("Message from client: " + clientAddressString);

                    // Adresse des Clients speichern, falls nicht schon vorhanden
                    if (!clientList.contains(clientAddressString)) {
                        clientList.add(clientAddressString);
                        System.out.println("New client added: " + clientAddressString);
                    }

                    // Nachricht an alle Clients weiterleiten
                    String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    String response = "Client " + clientAddressString + ": " + message;
                    byte[] responseData = response.getBytes();

                    for (String client : clientList) {
                        String[] parts = client.split(":");
                        InetAddress clientInetAddress = InetAddress.getByName(parts[0].substring(1)); // IP ohne Slash
                        int clientPort = Integer.parseInt(parts[1]);

                        DatagramPacket sendPacket = new DatagramPacket(
                                responseData,
                                responseData.length,
                                clientInetAddress,
                                clientPort
                        );
                        serverSocket.send(sendPacket);  // Nachricht an alle Clients senden
                        System.out.println("Message has been sent back to: " + client);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
