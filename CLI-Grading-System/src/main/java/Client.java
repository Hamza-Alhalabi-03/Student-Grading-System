import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private static final String HOST = "localhost";
    private static final int PORT = 5000;

    public static void main(String[] args) {
        Socket socket = null;
        PrintWriter out = null;
        BufferedReader in = null;
        Scanner userInput = null;
        Thread responseThread = null;

        try {
            socket = new Socket(HOST, PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            userInput = new Scanner(System.in);

            System.out.println("Connected to server at " + HOST + ":" + PORT);

            BufferedReader finalIn = in;

            // Thread to receive server responses
            responseThread = new Thread(() -> {
                try {
                    String serverResponse;
                    while ((serverResponse = finalIn.readLine()) != null) {
                        System.out.println("Server response: " + serverResponse);
                    }
                } catch (IOException e) {
                    if (!Thread.currentThread().isInterrupted()) {
                        System.err.println("Error receiving server response: " + e.getMessage());
                    }
                }
            });
            responseThread.start();

            // Main thread for sending messages
            while (true) {
                System.out.println("*******************");
                System.out.println("Enter command (or 'exit' to quit): ");
                String command = userInput.nextLine();

                if ("exit".equalsIgnoreCase(command)) {
                    out.println(command); // Notify server about exit
                    responseThread.interrupt();
                    break;
                }

                out.println(command);
            }
        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
        } finally {
            try {
                if (responseThread != null) responseThread.interrupt();
                if (in != null) in.close();
                if (out != null) out.close();
                if (socket != null) socket.close();
                if (userInput != null) userInput.close();
            } catch (IOException e) {
                System.err.println("Error during cleanup: " + e.getMessage());
            }
            System.exit(0);
        }
    }
}