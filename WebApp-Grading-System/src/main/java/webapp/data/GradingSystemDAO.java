package webapp.data;

import com.mysql.cj.jdbc.MysqlDataSource;
import webapp.models.Role;
import webapp.models.User;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

public class GradingSystemDAO {
    private DataSource ds;

    public GradingSystemDAO(){
        try{
            ds = getDataSource();
        }catch (Exception e){
            System.out.println("Error connecting to database");
            System.out.println(e.getMessage());
            System.exit(0);
        }
    }

    private  DataSource getDataSource() throws SQLException {
        MysqlDataSource ds = new MysqlDataSource();
        ds.setServerName("localhost");
        ds.setDatabaseName("grading_system");
        ds.setUser("root");
        ds.setPassword("123456");
        ds.setUseSSL(false);
        ds.setAllowPublicKeyRetrieval(true);

        return ds;
    }

    public User getUser(String username, String password) {
        User user = null;
        try (Connection conn = ds.getConnection()) {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, username);
            pStmt.setString(2, password);
            ResultSet rs = pStmt.executeQuery();
            if (rs.next()) {
                user = new User(username, password, Role.valueOf(rs.getString("role")));
            } else {
            }
        }
        catch (SQLException e) {
            System.out.println("Error during login: " + e.getMessage());
        }
        return user;
    }

    public Map<String, String> getStudentCourses(String username) {
        Map<String, String> coursesWithInstructors = new HashMap<>();
        try (Connection conn = ds.getConnection()) {
            String sql = "SELECT c.course_name, u_instructor.username AS instructor_name " +
                    "FROM users u_student " +
                    "JOIN enrollments e ON u_student.user_id = e.student_id " +
                    "JOIN courses c ON e.course_id = c.course_id " +
                    "JOIN users u_instructor ON c.instructor_id = u_instructor.user_id " +
                    "WHERE u_student.username = ?";

            PreparedStatement pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, username);
            ResultSet rs = pStmt.executeQuery();

            while (rs.next()) {
                String courseName = rs.getString("course_name");
                String instructorName = rs.getString("instructor_name");
                coursesWithInstructors.put(courseName, instructorName);
            }
        }
        catch (SQLException e) {
            System.out.println("Error retrieving student courses: " + e.getMessage());
        }
        return coursesWithInstructors;
    }

    public Map<String, String> getStudentGrades(String username){
        Map<String, String> grades = new HashMap<>();
        try (Connection conn = ds.getConnection()) {
            String sql = "SELECT c.course_name, g.grade " +
                    "FROM users u_student " +
                    "JOIN enrollments e ON u_student.user_id = e.student_id " +
                    "JOIN courses c ON e.course_id = c.course_id " +
                    "JOIN grades g ON e.student_id = g.student_id AND e.course_id = g.course_id " +
                    "WHERE u_student.username = ?";

            PreparedStatement pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, username);
            ResultSet rs = pStmt.executeQuery();

            while (rs.next()) {
                String courseName = rs.getString("course_name");
                String grade = rs.getString("grade");
                grades.put(courseName, grade);
            }
        }
        catch (SQLException e) {
            System.out.println("Error retrieving student grades: " + e.getMessage());
        }
        return grades;
    }

    public List<String> getInstructorCourses(String username) {
        List<String> courses = new ArrayList<>();
        try (Connection conn = ds.getConnection()) {
            String sql = "SELECT c.course_name " +
                    "FROM users u_instructor " +
                    "JOIN courses c ON u_instructor.user_id = c.instructor_id " +
                    "WHERE u_instructor.username = ?";

            PreparedStatement pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, username);
            ResultSet rs = pStmt.executeQuery();

            while (rs.next()) {
                String courseName = rs.getString("course_name");
                courses.add(courseName);
            }
        }
        catch (SQLException e) {
            System.out.println("Error retrieving instructor courses: " + e.getMessage());
        }
        return courses;
    }

    public Map<String, String> getCourseGrades(String courseName) {
        Map<String, String> grades = new HashMap<>();
        try (Connection conn = ds.getConnection()) {
            String sql = "SELECT u_student.username AS student_name, g.grade " +
                    "FROM users u_student " +
                    "JOIN enrollments e ON u_student.user_id = e.student_id " +
                    "JOIN courses c ON e.course_id = c.course_id " +
                    "JOIN grades g ON e.student_id = g.student_id AND e.course_id = g.course_id " +
                    "WHERE c.course_name = ?";

            PreparedStatement pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, courseName);
            ResultSet rs = pStmt.executeQuery();

            while (rs.next()) {
                String studentName = rs.getString("student_name");
                String grade = rs.getString("grade");
                grades.put(studentName, grade);
            }
        }
        catch (SQLException e) {
            System.out.println("Error retrieving course grades: " + e.getMessage());
        }
        return grades;
    }

    public boolean updateStudentGrade(String courseName, String studentName, String newGrade) {
        try (Connection conn = ds.getConnection()) {
            String sql = "UPDATE grades g " +
                    "JOIN users u_student ON g.student_id = u_student.user_id " +
                    "JOIN courses c ON g.course_id = c.course_id " +
                    "SET g.grade = ? " +
                    "WHERE c.course_name = ? AND u_student.username = ?";

            PreparedStatement pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, newGrade);
            pStmt.setString(2, courseName);
            pStmt.setString(3, studentName);
            int rowsUpdated = pStmt.executeUpdate();
            return rowsUpdated > 0;
        }
        catch (SQLException e) {
            System.out.println("Error updating student grade: " + e.getMessage());
            return false;
        }
    }

    public boolean addUser(String username, String password, Role role) {
        try (Connection conn = ds.getConnection()) {
            String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
            PreparedStatement pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, username);
            pStmt.setString(2, password);
            pStmt.setString(3, role.toString());
            int rowsInserted = pStmt.executeUpdate();
            return rowsInserted > 0;
        }
        catch (SQLException e) {
            System.out.println("Error adding user: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteUser(String username) {
        try (Connection conn = ds.getConnection()) {
            String sql = "DELETE FROM users WHERE username = ?";
            PreparedStatement pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, username);
            int rowsDeleted = pStmt.executeUpdate();
            return rowsDeleted > 0;
        }
        catch (SQLException e) {
            System.out.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }

    public boolean addCourse(String courseName, String instructorName) {
        try (Connection conn = ds.getConnection()) {
            String sql = "INSERT INTO courses (course_name, instructor_id) " +
                    "SELECT ?, user_id FROM users WHERE username = ?";
            PreparedStatement pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, courseName);
            pStmt.setString(2, instructorName);
            int rowsInserted = pStmt.executeUpdate();
            return rowsInserted > 0;
        }
        catch (SQLException e) {
            System.out.println("Error adding course: " + e.getMessage());
            return false;
        }
    }

    public Map<String, String> getUsers(){
        Map<String, String> users = new HashMap<>();
        try (Connection conn = ds.getConnection()) {
            String sql = "SELECT username, role FROM users";
            PreparedStatement pStmt = conn.prepareStatement(sql);
            ResultSet rs = pStmt.executeQuery();

            while (rs.next()) {
                String username = rs.getString("username");
                String role = rs.getString("role");
                users.put(username, role);
            }
        }
        catch (SQLException e) {
            System.out.println("Error retrieving users: " + e.getMessage());
        }
        return users;
    }

    public Map<String, String> getCourses(){
        Map<String, String> courses = new HashMap<>();
        try (Connection conn = ds.getConnection()) {
            String sql = "SELECT c.course_name, u.username AS instructor_name " +
                    "FROM courses c " +
                    "JOIN users u ON c.instructor_id = u.user_id";
            PreparedStatement pStmt = conn.prepareStatement(sql);
            ResultSet rs = pStmt.executeQuery();

            while (rs.next()) {
                String courseName = rs.getString("course_name");
                String instructorName = rs.getString("instructor_name");
                courses.put(courseName, instructorName);
            }
        }
        catch (SQLException e) {
            System.out.println("Error retrieving courses: " + e.getMessage());
        }
        return courses;
    }

    public Map<String, String> getCourseStatistics(String courseName) {
        Map<String, String> statistics = new HashMap<>();
        try (Connection conn = ds.getConnection()) {
            String sql = "WITH course_grades AS (" +
                    "SELECT g.grade " +
                    "FROM courses c " +
                    "JOIN enrollments e ON c.course_id = e.course_id " +
                    "JOIN grades g ON e.student_id = g.student_id AND e.course_id = g.course_id " +
                    "WHERE c.course_name = ?" +
                    "), grade_stats AS (" +
                    "SELECT " +
                    "    COUNT(*) AS total_students, " +
                    "    AVG(grade) AS average_grade, " +
                    "    MAX(grade) AS highest_grade, " +
                    "    MIN(grade) AS lowest_grade, " +
                    "    (" +
                    "        SELECT grade " +
                    "        FROM (" +
                    "            SELECT grade, " +
                    "            ROW_NUMBER() OVER (ORDER BY grade) AS row_num, " +
                    "            COUNT(*) OVER () AS total_count " +
                    "            FROM course_grades" +
                    "        ) ranked " +
                    "        WHERE row_num IN (FLOOR((total_count + 1) / 2), CEIL((total_count + 1) / 2))" +
                    "        GROUP BY grade " +
                    "        LIMIT 1" +
                    "    ) AS median_grade " +
                    "FROM course_grades" +
                    ")";

            String countSql = sql + " SELECT * FROM grade_stats";

            PreparedStatement pStmt = conn.prepareStatement(countSql);
            pStmt.setString(1, courseName);
            ResultSet rs = pStmt.executeQuery();

            if (rs.next()) {
                int totalStudents = rs.getInt("total_students");
                double averageGrade = rs.getDouble("average_grade");
                double highestGrade = rs.getDouble("highest_grade");
                double lowestGrade = rs.getDouble("lowest_grade");
                double medianGrade = rs.getDouble("median_grade");

                statistics.put("totalStudents", String.valueOf(totalStudents));
                statistics.put("averageGrade", String.format("%.2f", averageGrade));
                statistics.put("highestGrade", String.format("%.2f", highestGrade));
                statistics.put("lowestGrade", String.format("%.2f", lowestGrade));
                statistics.put("medianGrade", String.format("%.2f", medianGrade));
            }
        }
        catch (SQLException e) {
            System.out.println("Error retrieving course statistics: " + e.getMessage());
            statistics.put("error", e.getMessage());
        }
        return statistics;
    }

    public boolean validateUser(String username, String password){
        User user = getUser(username, password);
        return user != null;
    }

}
