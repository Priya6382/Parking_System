package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ChatClient {
    private static final int SERVER_PORT = 9876;
    private static final int MAX_PARKING_CAPACITY = 2;
    private static int parkedVehiclesCount = 0;
    private static Map<String, String> loggedInUsers = new HashMap<>();

    public static void main(String[] args) {
        try {
            DatagramSocket serverSocket = new DatagramSocket(SERVER_PORT);
            System.out.println("Server started on port " + SERVER_PORT);

            while (true) {
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

                serverSocket.receive(receivePacket);

                String request = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("Received request: " + request);

                String clientAddress = receivePacket.getAddress().getHostAddress();
                String clientDetails = "Client Address: " + clientAddress + "\n"
                        + "Client Port: " + receivePacket.getPort() + "\n"
                        + "Checksum: " + calculateChecksum(receivePacket.getData());

                String response = processRequest(request, clientAddress, clientDetails);

                byte[] sendData = response.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, receivePacket.getAddress(), receivePacket.getPort());

                serverSocket.send(sendPacket);
                System.out.println("Sent response to client: " + response + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String processRequest(String request, String clientAddress, String clientDetails) {
        // Implement your parking system logic here
        // Parse the request and perform the required actions
        // Return the response to the client

        // Example: Login
        if (request.startsWith("LOGIN")) {
            String[] tokens = request.split(" ");
            if (tokens.length >= 2) {
                String username = tokens[1];
                loggedInUsers.put(clientAddress, username);
                return "Welcome, " + username + "!\n\n" + clientDetails;
            } else {
                return "Invalid login request.\n\n" + clientDetails;
            }
        }

        // Example: Check availability
        if (request.equalsIgnoreCase("CHECK_AVAILABILITY")) {
            if (loggedInUsers.containsKey(clientAddress)) {
                if (parkedVehiclesCount < MAX_PARKING_CAPACITY) {
                    return "Parking is available.\n\n" + clientDetails;
                } else {
                    return "Parking is full.\n\n" + clientDetails;
                }
            } else {
                return "User not logged in.\n\n" + clientDetails;
            }
        }

        // Example: Park vehicle
        if (request.equalsIgnoreCase("PARK")) {
            if (loggedInUsers.containsKey(clientAddress)) {
                if (parkedVehiclesCount < MAX_PARKING_CAPACITY) {
                    // Generate a random ticket number
                    String ticketNumber = generateTicketNumber();
                    parkedVehiclesCount++;
                    return "Vehicle parked. \nTicket number: " + ticketNumber + "\n\n" + clientDetails;
                } else {
                    return "Parking is full.\n\n" + clientDetails;
                }
            } else {
                return "User not logged in.\n\n" + clientDetails;
            }
        }

        // Example: Exit vehicle
        if (request.startsWith("EXIT")) {
            // Extract the ticket number from the request
            String ticketNumber = request.substring(5).trim();
            if (loggedInUsers.containsKey(clientAddress)) {
                // Process the exit and calculate the parking fee
                double parkingFee = exitVehicle(ticketNumber);
                if (parkingFee >= 0) {
                    return "Vehicle exited. \nParking fee: Rs." + parkingFee + "\n\n" + clientDetails;
                } else {
                    return "Invalid ticket number. Cannot exit vehicle.\n\n" + clientDetails;
                }
            } else {
                return "User not logged in.\n\n" + clientDetails;
            }
        }

        // Example: Logout
        if (request.equalsIgnoreCase("LOGOUT")) {
            if (loggedInUsers.containsKey(clientAddress)) {
                loggedInUsers.remove(clientAddress);
                return "Logged out successfully.\n\n" + clientDetails;
            } else {
                return "User not logged in.\n\n" + clientDetails;
            }
        }

        // Invalid request
        return "Invalid request.\n\n" + clientDetails;
    }

    private static String generateTicketNumber() {
        // Generate a random ticket number
        Random random = new Random();
        int ticketNumber = random.nextInt(9000) + 1000;
        return String.valueOf(ticketNumber);
    }

    private static double exitVehicle(String ticketNumber) {
        // Implement the logic to process the vehicle exit and calculate the parking fee
        // ...

        // Example: Calculate parking fee based on ticket number
        int parsedTicketNumber;
        try {
            parsedTicketNumber = Integer.parseInt(ticketNumber);
        } catch (NumberFormatException e) {
            return -1; // Invalid ticket number
        }

        if (parsedTicketNumber < 1000 || parsedTicketNumber > 9999) {
            return -1; // Invalid ticket number
        }

        // Process the exit and calculate the parking fee
        parkedVehiclesCount--;
        double parkingFee = parsedTicketNumber * 0.001; // Assuming ticket number represents milliseconds
        return parkingFee;
    }

    private static int calculateChecksum(byte[] data) {
        int checksum = 0;
        for (byte b : data) {
            checksum += (b & 0xFF);
        }
        return checksum;
    }
}