package com.example.demo.data;

import com.example.demo.models.Role;
import com.example.demo.models.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class GradingSystemDAO {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GradingSystemDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public User getUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        List<User> users = jdbcTemplate.query(sql, new UserRowMapper(), username, password);
        return users.isEmpty() ? null : users.get(0);
    }

    public Map<String, String> getStudentCourses(String username) {
        Map<String, String> coursesWithInstructors = new HashMap<>();
        String sql = "SELECT c.course_name, u_instructor.username AS instructor_name " +
                "FROM users u_student " +
                "JOIN enrollments e ON u_student.user_id = e.student_id " +
                "JOIN courses c ON e.course_id = c.course_id " +
                "JOIN users u_instructor ON c.instructor_id = u_instructor.user_id " +
                "WHERE u_student.username = ?";

        jdbcTemplate.query(sql, (rs, rowNum) -> {
            String courseName = rs.getString("course_name");
            String instructorName = rs.getString("instructor_name");
            coursesWithInstructors.put(courseName, instructorName);
            return null;
        }, username);

        return coursesWithInstructors;
    }

    public Map<String, String> getStudentGrades(String username) {
        Map<String, String> grades = new HashMap<>();
        String sql = "SELECT c.course_name, g.grade " +
                "FROM users u_student " +
                "JOIN enrollments e ON u_student.user_id = e.student_id " +
                "JOIN courses c ON e.course_id = c.course_id " +
                "JOIN grades g ON e.student_id = g.student_id AND e.course_id = g.course_id " +
                "WHERE u_student.username = ?";

        jdbcTemplate.query(sql, (rs, rowNum) -> {
            String courseName = rs.getString("course_name");
            String grade = rs.getString("grade");
            grades.put(courseName, grade);
            return null;
        }, username);

        return grades;
    }

    public List<String> getInstructorCourses(String username) {
        String sql = "SELECT c.course_name " +
                "FROM users u_instructor " +
                "JOIN courses c ON u_instructor.user_id = c.instructor_id " +
                "WHERE u_instructor.username = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("course_name"), username);
    }

    public Map<String, String> getCourseGrades(String courseName) {
        Map<String, String> grades = new HashMap<>();
        String sql = "SELECT u_student.username AS student_name, g.grade " +
                "FROM users u_student " +
                "JOIN enrollments e ON u_student.user_id = e.student_id " +
                "JOIN courses c ON e.course_id = c.course_id " +
                "JOIN grades g ON e.student_id = g.student_id AND e.course_id = g.course_id " +
                "WHERE c.course_name = ?";

        jdbcTemplate.query(sql, (rs, rowNum) -> {
            String studentName = rs.getString("student_name");
            String grade = rs.getString("grade");
            grades.put(studentName, grade);
            return null;
        }, courseName);

        return grades;
    }

    public boolean updateStudentGrade(String courseName, String studentName, String newGrade) {
        String sql = "UPDATE grades g " +
                "JOIN users u_student ON g.student_id = u_student.user_id " +
                "JOIN courses c ON g.course_id = c.course_id " +
                "SET g.grade = ? " +
                "WHERE c.course_name = ? AND u_student.username = ?";

        int rowsUpdated = jdbcTemplate.update(sql, newGrade, courseName, studentName);
        return rowsUpdated > 0;
    }

    public boolean addUser(String username, String password, Role role) {
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        int rowsInserted = jdbcTemplate.update(sql, username, password, role.toString());
        return rowsInserted > 0;
    }

    public boolean deleteUser(String username) {
        String sql = "DELETE FROM users WHERE username = ?";
        int rowsDeleted = jdbcTemplate.update(sql, username);
        return rowsDeleted > 0;
    }

    public boolean addCourse(String courseName, String instructorName) {
        String sql = "INSERT INTO courses (course_name, instructor_id) " +
                "SELECT ?, user_id FROM users WHERE username = ?";
        int rowsInserted = jdbcTemplate.update(sql, courseName, instructorName);
        return rowsInserted > 0;
    }

    public Map<String, String> getUsers() {
        Map<String, String> users = new HashMap<>();
        String sql = "SELECT username, role FROM users";

        jdbcTemplate.query(sql, (rs, rowNum) -> {
            String username = rs.getString("username");
            String role = rs.getString("role");
            users.put(username, role);
            return null;
        });

        return users;
    }

    public Map<String, String> getCourses() {
        Map<String, String> courses = new HashMap<>();
        String sql = "SELECT c.course_name, u.username AS instructor_name " +
                "FROM courses c " +
                "JOIN users u ON c.instructor_id = u.user_id";

        jdbcTemplate.query(sql, (rs, rowNum) -> {
            String courseName = rs.getString("course_name");
            String instructorName = rs.getString("instructor_name");
            courses.put(courseName, instructorName);
            return null;
        });

        return courses;
    }

    public Map<String, String> getCourseStatistics(String courseName) {
        Map<String, String> statistics = new HashMap<>();
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

        try {
            return jdbcTemplate.queryForObject(countSql, (rs, rowNum) -> {
                Map<String, String> stats = new HashMap<>();
                int totalStudents = rs.getInt("total_students");
                double averageGrade = rs.getDouble("average_grade");
                double highestGrade = rs.getDouble("highest_grade");
                double lowestGrade = rs.getDouble("lowest_grade");
                double medianGrade = rs.getDouble("median_grade");

                stats.put("totalStudents", String.valueOf(totalStudents));
                stats.put("averageGrade", String.format("%.2f", averageGrade));
                stats.put("highestGrade", String.format("%.2f", highestGrade));
                stats.put("lowestGrade", String.format("%.2f", lowestGrade));
                stats.put("medianGrade", String.format("%.2f", medianGrade));
                return stats;
            }, courseName);
        } catch (Exception e) {
            statistics.put("error", e.getMessage());
            return statistics;
        }
    }

    public boolean validateUser(String username, String password) {
        User user = getUser(username, password);
        return user != null;
    }

    // Row mapper for the User class
    private static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new User(
                rs.getString("username"),
                rs.getString("password"),
                Role.valueOf(rs.getString("role"))
            );
        }
    }
}
