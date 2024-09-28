package com.example.chat_assignment;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

//Class for the Chat Server
public class ChatServer {

    //Defines the port number of the Server
    public static final int SERVER_PORT = 1234;

    //This arraylist is used to store all the clients (numbers) which are chatting
    private static List<String> CLIENT_LIST = new ArrayList<>();

    //main-method of the chatserver
    public static void main(String[] args) {

        //Initial client adresses of the two chatpartners are stored hardcoded in the server client list
        CLIENT_LIST.add("/127.0.0.1:12345");  // Chat-Client1
        CLIENT_LIST.add("/127.0.0.1:12346");  // Chat-Client2


        //Starting a new thread for the exchange of messages
        new Thread(() -> {

            //Try/catch block for error-handling
            try {
                //creating a datagram-socket for the server port, also creating a new needed buffer and a console message for debugging and giving infos
                DatagramSocket serverSocket = new DatagramSocket(SERVER_PORT);
                byte[] receiveBuffer = new byte[1024];
                System.out.println("Server is listening on local Port: " + SERVER_PORT);

                while (true) {

                    // Listening in a while loop
                    DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                    serverSocket.receive(receivePacket);

                    //Client address will be encoded
                    String clientAddress = receivePacket.getAddress().toString() + ":" + receivePacket.getPort();

                    // if new client is not in the client list it will be added, also gives a console message
                    if (!CLIENT_LIST.contains(clientAddress)) {
                        CLIENT_LIST.add(clientAddress);
                        System.out.println("New client added to client list: " + clientAddress);
                        System.out.println("Actual client list: " + CLIENT_LIST); // gives out client list
                    }

                    // encoding the message to data and stores it in the variable message, also gives a console message
                    String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    System.out.println("Received message from " + clientAddress + ": " + message);
                    //Presents the client list at the console
                    System.out.println("Current Clientlist: " + CLIENT_LIST);

                    // Stores the message which will later be shown and updated in the GUI in the variable chatMessage
                    String chatMessage = "[" + clientAddress + "]: " + message;

                    //encodes the chatMessage to prepare the sending back to clients
                    byte[] responseData = chatMessage.getBytes();

                    // sends the chatMessage to all clients in the client list
                    for (String client : CLIENT_LIST) { // iterating through the clientList
                        String[] parts = client.split(":"); //splits the list at ":"" and stores it to parts[]
                        InetAddress clientInetAddress = InetAddress.getByName(parts[0].substring(1)); // removes the slash
                        int clientPort = Integer.parseInt(parts[1]); //casts the port numbers

                        //Creates a new package for sending back to clients.
                        DatagramPacket sendPacket = new DatagramPacket(
                                responseData,
                                responseData.length,
                                clientInetAddress,
                                clientPort
                        );
                        //sends the package
                        serverSocket.send(sendPacket);  // Sends the message
                    }
                }
            // Exception will be thrown here
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start(); // Starts threading
    }
}
