import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private static final String HOST = "localhost";
    private static final int PORT = 5000;

    public static void main(String[] args) {
        try (
                Socket socket = new Socket(HOST, PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                Scanner userInput = new Scanner(System.in)
        ) {
            System.out.println("Connected to server at " + HOST + ":" + PORT);

            // Start a thread to receive server responses
            Thread responseThread = new Thread(() -> {
                try {
                    String serverResponse;
                    while ((serverResponse = in.readLine()) != null) {
                        System.out.println("Server response: " + serverResponse);
                    }
                } catch (IOException e) {
                    System.err.println("Error receiving server response: " + e.getMessage());
                }
            });
            responseThread.start();

            // Main thread for sending messages
            while (true) {
                System.out.print("Enter command (or 'exit' to quit): ");
                String command = userInput.nextLine();

                if ("exit".equalsIgnoreCase(command)) {
                    break;
                }

                out.println(command);
            }
        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
        }
    }
}