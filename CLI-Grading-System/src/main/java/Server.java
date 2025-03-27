import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class Server {
    private static final int PORT = 5000;
    private static final ExecutorService threadPool = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());

                // New thread to handle each client connection
                threadPool.submit(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        } finally {
            threadPool.shutdown();
        }
    }

    // Handle individual client connections
    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private final GradingSystemDAO dao = new GradingSystemDAO();
        private User user;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
            ) {
                // Read client request
                do {
                    if (user == null){
                        out.println("Please enter your username:");
                        String username = in.readLine();
                        out.println("Please enter your password:");
                        String password = in.readLine();
                        user = dao.loginUser(username, password); // Validate user credentials from DAO
                        if (user == null) {
                            out.println("Invalid username or password. Please try again.");
                            continue;
                        }
                        System.out.println("User connected: " + user.getUsername());
                    }
                    // the user is logged in, now we can process commands
                    String inputLine = in.readLine();
                    if (inputLine == null || "exit".equalsIgnoreCase(inputLine)) {
                        System.out.println("Client disconnected: " + clientSocket.getInetAddress());
                        break;
                    }
                    System.out.println("Received from client: " + inputLine);

                    // Process the request (you can add your database logic here)
                    String response = processClientRequest(inputLine);

                    // Send response back to client
                    out.println(response);
                } while (true);

            } catch (IOException e) {
                System.err.println("Error handling client: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.err.println("Error closing client socket: " + e.getMessage());
                }
            }
        }

        // Method to process client requests (customize as needed)
        private String processClientRequest(String request) {
            switch (request.trim().toUpperCase()) {
                case "HELLO":
                    return "Welcome to the Database Server!";
                case "STATUS":
                    return "Server is running normally";
                default:
                    return "Unknown command: " + request;
            }
        }
    }
}