package com.example.chat_assignment;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
	
	//Server-PortNumber as int 
    public static final int SERVER_PORT = 1234;

    // ClientListe as an Array 
    private static List<String> clientList = new ArrayList<>();

    public static void main(String[] args) {
		//new Thread for the Datagram
        new Thread(() -> {
            try {
                DatagramSocket serverSocket = new DatagramSocket(SERVER_PORT);
                byte[] receiveBuffer = new byte[1024];
                System.out.println("Server is running on Port: " + SERVER_PORT);

                while (true) {
                    DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                    serverSocket.receive(receivePacket);  // waiting for messenges of the client

                    // Client adresses as String 
                    String clientAddress = receivePacket.getAddress().toString() + ":" + receivePacket.getPort();
                    System.out.println("Message was received from a client : " + clientAddress);

                    // Adresse des Clients speichern, falls nicht schon vorhanden
                    if (!clientList.contains(clientAddress)) {
                        clientList.add(clientAddress);
                        System.out.println("New client added to clientlist: " + clientAddress);
                    }

                    // Nachricht an alle Clients weiterleiten
                    String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    String response = "Client " + clientAddress + ": " + message;
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
