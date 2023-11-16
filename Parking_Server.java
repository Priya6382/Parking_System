package ddc_java.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ChatServer {
    private static final int SERVER_PORT = 9876;
    private static final int MAX_PARKING_CAPACITY = 10;
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

                String response = processRequest(request, receivePacket.getAddress().toString());

                byte[] sendData = response.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, receivePacket.getAddress(), receivePacket.getPort());

                serverSocket.send(sendPacket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String processRequest(String request, String clientAddress) {
        // Implement your parking system logic here
        // Parse the request and perform the required actions
        // Return the response to the client

        // Example: Login
        if (request.startsWith("LOGIN")) {
            String[] tokens = request.split(" ");
            if (tokens.length >= 2) {
                String username = tokens[1];
                loggedInUsers.put(clientAddress, username);
                return "Welcome, " + username + "!";
            } else {
                return "Invalid login request.";
            }
        }

        // Example: Check availability
        if (request.equalsIgnoreCase("CHECK_AVAILABILITY")) {
            if (loggedInUsers.containsKey(clientAddress)) {
                if (parkedVehiclesCount < MAX_PARKING_CAPACITY) {
                    return "Parking is available.";
                } else {
                    return "Parking is full.";
                }
            } else {
                return "User not logged in.";
            }
        }

        // Example: Park vehicle
        if (request.equalsIgnoreCase("PARK")) {
            if (loggedInUsers.containsKey(clientAddress)) {
                if (parkedVehiclesCount < MAX_PARKING_CAPACITY) {
                    // Generate a random ticket number
                    String ticketNumber = generateTicketNumber();
                    parkedVehiclesCount++;
                    return "Vehicle parked. Ticket number: " + ticketNumber;
                } else {
                    return "Parking is full.";
                }
            } else {
                return "User not logged in.";
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
                    return "Vehicle exited. Parking fee: $" + parkingFee;
                } else {
                    return "Invalid ticket number. Cannot exit vehicle.";
                }
            } else {
                return "User not logged in.";
            }
        }

        // Example: Logout
        if (request.equalsIgnoreCase("LOGOUT")) {
            if (loggedInUsers.containsKey(clientAddress)) {
                loggedInUsers.remove(clientAddress);
                return "Logged out successfully.";
            } else {
                return "User not logged in.";
            }
        }

        // Invalid request
        return "Invalid request.";
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
}
