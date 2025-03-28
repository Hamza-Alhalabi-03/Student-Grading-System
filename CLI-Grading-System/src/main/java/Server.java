import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Map;
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
                do {
                    while (user == null){
                        loginMenu(in, out);
                    }
                    switch (user.getRole()) {
                        case STUDENT:
                            out.println("Welcome " + user.getUsername() + ",");
                            studentMenu(in, out);
                            break;
                        case INSTRUCTOR:
                            out.println("Welcome " + user.getUsername() + ",");
                            instructorMenu(in, out);
                            break;
                        case ADMIN:
                            out.println("Welcome " + user.getUsername() + ",");
                            adminMenu(in, out);
                            break;
                    }

                    String inputLine = in.readLine();
                    if (inputLine == null || "exit".equalsIgnoreCase(inputLine)) {
                        System.out.println("Client disconnected: " + clientSocket.getInetAddress());
                        break;
                    }
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

        private void loginMenu(BufferedReader in, PrintWriter out) throws IOException {
            out.println("Welcome to the Student Grading System. Please log in.");
            out.println("Please enter your username:");
            String username = in.readLine();
            out.println("Please enter your password:");
            String password = in.readLine();
            user = dao.getUser(username, password);
            if (user == null) {
                out.println("Invalid username or password. Please try again.");
                return;
            }
            System.out.println("User logged in: " + user.getUsername());
        }

        private void logoutMenu(BufferedReader in, PrintWriter out) throws IOException {
            out.println("Logging out...\n");
            String username = user.getUsername();
            user = null;
            System.out.println("User " + username + " logged out.");
            out.println("You have been logged out.\n");
        }

        private void studentMenu(BufferedReader in, PrintWriter out) throws IOException {
            while (true) {
                out.println("Student Grading System Menu:");
                out.println("1. View Courses");
                out.println("2. View Grades");
                out.println("3. Logout");
                out.println("Please select an option:");
                String inputLine = in.readLine();
                switch (inputLine) {
                    case "1":
                        Map<String, String> coursesWithInstructors = dao.getStudentCourses(user.getUsername());
                        if (coursesWithInstructors.isEmpty()) {
                            out.println("No courses found.");
                        }
                        else {
                            out.println("\nStudent Courses:");
                            out.println("+--------------------------------+-----------------------+");
                            out.println("| Course Name                    | Instructor           |");
                            out.println("+--------------------------------+-----------------------+");

                            for (Map.Entry<String, String> entry : coursesWithInstructors.entrySet()) {
                                out.printf("| %-30s | %-20s |\n",
                                        truncateString(entry.getKey(), 30),
                                        truncateString(entry.getValue(), 20));
                            }
                            out.println("+--------------------------------+-----------------------+");
                        }
                        break;
                    case "2":
                        Map<String, String> coursesWithGrades = dao.getStudentGrades(user.getUsername());
                        if (coursesWithGrades.isEmpty()) {
                            out.println("No courses or grades found.");
                        }
                        else {
                            out.println("\nStudent Grades:");
                            out.println("+--------------------------------+-----------------------+");
                            out.println("| Course Name                    | Grade                |");
                            out.println("+--------------------------------+-----------------------+");

                            for (Map.Entry<String, String> entry : coursesWithGrades.entrySet()) {
                                out.printf("| %-30s | %-20s |\n",
                                        truncateString(entry.getKey(), 30),
                                        truncateString(entry.getValue(), 20));
                            }
                            out.println("+--------------------------------+-----------------------+");
                        }
                        break;
                    case "3":
                        logoutMenu(in, out);
                        return;
                    default:
                        out.println("Invalid option. Please try again.");
                }
            }
        }

        private String truncateString(String str, int length) {
            return str.length() > length ? str.substring(0, length - 3) + "..." : str;
        }

        private void instructorMenu(BufferedReader in, PrintWriter out) throws IOException {
            while (true) {
                out.println("Instructor Grading System Menu:");
                out.println("1. View Courses");
                out.println("2. View Course Grades");
                out.println("3. Edit Student Grade");
                out.println("4. Logout");
                out.println("Please select an option:");
                String inputLine = in.readLine();
                switch (inputLine) {
                    case "1":
                        List<String> instructorCourses = dao.getInstructorCourses(user.getUsername());
                        if (instructorCourses.isEmpty()) {
                            out.println("No courses found.");
                        }
                        else {
                            out.println("\nInstructor Courses:");
                            out.println("+--------------------------------+");
                            out.println("| Course Name                    |");
                            out.println("+--------------------------------+");

                            for (String course : instructorCourses) {
                                out.printf("| %-30s |\n",
                                        truncateString(course, 30));
                            }
                            out.println("+--------------------------------+");
                        }
                        break;
                    case "2":
                        out.println("Please enter the course name:");
                        String courseName = in.readLine();
                        Map<String, String> courseGrades = dao.getCourseGrades(courseName);
                        if (courseGrades.isEmpty()) {
                            out.println("No students or grades found.");
                        }
                        else {
                            out.println("\nCourse Grades:");
                            out.println("+----------------------+-----------------------+");
                            out.println("| Student Name         | Grade                |");
                            out.println("+----------------------+-----------------------+");

                            for (Map.Entry<String, String> entry : courseGrades.entrySet()) {
                                out.printf("| %-20s | %-20s |\n",
                                        truncateString(entry.getKey(), 20),
                                        truncateString(entry.getValue(), 20));
                            }
                            out.println("+----------------------+-----------------------+");
                        }
                        break;
                    case "3":
                        out.println("Please enter the course name:");
                        String courseNameForGrade = in.readLine();
                        out.println("Please enter the student username:");
                        String studentUsername = in.readLine();
                        out.println("Please enter the new grade:");
                        String newGrade = in.readLine();
                        boolean isUpdated = dao.updateStudentGrade(courseNameForGrade, studentUsername, newGrade);
                        if (isUpdated){
                            out.println("Grade updated successfully.");
                        } else {
                            out.println("Failed to update grade. Please check the course name and student username.");
                        }
                        break;
                    case "4":
                        logoutMenu(in, out);
                        return;
                    default:
                        out.println("Invalid option. Please try again.");
                }
            }
        }

        private void adminMenu(BufferedReader in, PrintWriter out) throws IOException {
            while (true) {
                out.println("Admin Grading System Menu:");
                out.println("1. Add Student");
                out.println("2. Delete Student");
                out.println("3. Add Instructor");
                out.println("4. Delete Instructor");
                out.println("5. Add Course");
                out.println("6. View All Users");
                out.println("7. View All Courses");
                out.println("8. Logout");
                out.println("Please select an option:");
                String inputLine = in.readLine();
                switch (inputLine) {
                    case "1" :
                        out.println("Please enter the student name:");
                        String studentName = in.readLine();
                        out.println("Please enter the password:");
                        String studentPassword = in.readLine();
                        boolean isUserAdded = dao.addUser(studentName, studentPassword, Role.STUDENT);
                        if (isUserAdded) {
                            out.println("Student added successfully.");
                        } else {
                            out.println("Failed to add student. Please try again.");
                        }
                        break;
                    case "2":
                        out.println("Please enter the student name:");
                        String studentUsername = in.readLine();
                        boolean isDeleted = dao.deleteUser(studentUsername);
                        if (isDeleted) {
                            out.println("Student deleted successfully.");
                        } else {
                            out.println("Failed to delete student. Please try again.");
                        }
                        break;
                    case "3":
                        out.println("Please enter the instructor name:");
                        String instructorName = in.readLine();
                        out.println("Please enter the password:");
                        String instructorPassword = in.readLine();
                        boolean isInstructorAdded = dao.addUser(instructorName, instructorPassword, Role.INSTRUCTOR);
                        if (isInstructorAdded) {
                            out.println("Instructor added successfully.");
                        } else {
                            out.println("Failed to add instructor. Please try again.");
                        }
                        break;
                    case "4":
                        out.println("Please enter the instructor name:");
                        String instructorUsername = in.readLine();
                        boolean isRemoved = dao.deleteUser(instructorUsername);
                        if (isRemoved) {
                            out.println("Instructor deleted successfully.");
                        } else {
                            out.println("Failed to delete instructor. Please try again.");
                        }
                        break;
                    case "5":
                        out.println("Please enter the course name:");
                        String courseName = in.readLine();
                        out.println("Please enter the instructor username:");
                        String courseInstructorUsername = in.readLine();
                        boolean isCourseAdded = dao.addCourse(courseName, courseInstructorUsername);
                        if (isCourseAdded) {
                            out.println("Course added successfully.");
                        } else {
                            out.println("Failed to add course. Please try again.");
                        }
                        break;
                    case "6":
                        Map<String, String> usersWithRoles = dao.getUsers();
                        if (usersWithRoles.isEmpty()) {
                            out.println("No users found.");
                        }
                        else {
                            out.println("\nSystem Users:");
                            out.println("+----------------------+-----------------------+");
                            out.println("| User Name            | Role                  |");
                            out.println("+----------------------+-----------------------+");

                            for (Map.Entry<String, String> entry : usersWithRoles.entrySet()) {
                                out.printf("| %-20s | %-20s |\n",
                                        truncateString(entry.getKey(), 20),
                                        truncateString(entry.getValue(), 20));
                            }
                            out.println("+----------------------+-----------------------+");
                        }
                        break;
                    case "7":
                        Map<String, String> allCourses = dao.getCourses();
                        if (allCourses.isEmpty()) {
                            out.println("No courses found.");
                        }
                        else {
                            out.println("\nSystem Courses:");
                            out.println("+--------------------------------+-----------------------+");
                            out.println("| Course Name                    | Instructor            |");
                            out.println("+--------------------------------+-----------------------+");

                            for (Map.Entry<String, String> entry : allCourses.entrySet()) {
                                out.printf("| %-30s | %-20s |\n",
                                        truncateString(entry.getKey(), 30),
                                        truncateString(entry.getValue(), 20));
                            }
                            out.println("+----------------------+-----------------------+");
                        }
                        break;
                    case "8":
                        logoutMenu(in, out);
                        return;
                    default:
                        out.println("Invalid option. Please try again.");
                }
            }
        }
    }
}