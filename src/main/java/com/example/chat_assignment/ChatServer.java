package com.example.chat_assignment;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {

    //Server-PortNumber as an int
    public static final int SERVER_PORT = 1234;

    // ClientListe as an Array
    private static List<String> clientList = new ArrayList<>();


    public static void main(String[] args) {
        //new Thread for the Datagram

        clientList.add("/127.0.0.1:12345");  // Beispiel-Client 1
        clientList.add("/127.0.0.1:12346");  // Beispiel-Client 2

        new Thread(() -> {
            try {

                DatagramSocket serverSocket = new DatagramSocket(SERVER_PORT);
                byte[] receiveBuffer = new byte[1024];
                System.out.println("Server is running on local Port: " + SERVER_PORT);

                while (true) {
                    DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                    serverSocket.receive(receivePacket);  // waiting for messages of the client
                    System.out.println("Waiting for messages of a client.");

                    // Client address as String
                    String clientAddress = receivePacket.getAddress().toString() + ":" + receivePacket.getPort();
                    if (!clientList.contains(clientAddress)) {
                        clientList.add(clientAddress);
                        System.out.println("New client added to client list: " + clientAddress);
                        System.out.println("Actual client list: " + clientList);
                    }

                    // Nachricht an alle Clients weiterleiten
                    String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    System.out.println("Received message: " + message);

                    String response = "Chatpartner: " + message;
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
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
