import com.mysql.cj.jdbc.MysqlDataSource;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Scanner;

public class GradingSystemDAO {
    private DataSource ds;

    GradingSystemDAO(){
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

    public User loginUser(String username, String password) {
        User user = null;
        try (Connection conn = ds.getConnection()) {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, username);
            pStmt.setString(2, password);
            ResultSet rs = pStmt.executeQuery();
            if (rs.next()) {
//                System.out.println("Login successful!");
                user = new User(username, password, Role.valueOf(rs.getString("role")));
            } else {
//                System.out.println("Invalid username or password.");
            }
        }
        catch (SQLException e) {
            System.out.println("Error during login: " + e.getMessage());
        }
        return user;
    }


    public  void removeItem(int itemId) throws SQLException {
        Connection conn = ds.getConnection();
        System.out.println("Remove Item");
        System.out.println("Which item would you like to remove?");
        //String itemId = sc.nextLine();


            String sql = "DELETE FROM todo WHERE id = ?";
            PreparedStatement pStmt = conn.prepareCall(sql);
            pStmt.setString(1, itemId + "");
            pStmt.executeUpdate();
            System.out.println("Remove Complete");

    }

    public void displayList() throws SQLException {
        try ( Connection conn = ds.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM todo");
            while (rs.next()) {
                System.out.printf("%s: %s -- %s -- %s\n",
                        rs.getString("id"),
                        rs.getString("todo"),
                        rs.getString("note"),
                        rs.getBoolean("finished"));
            }
            System.out.println("");
        }
    }

    public  void addItem(String task, String note) throws SQLException {


        try (Connection conn = ds.getConnection()) {
            String sql = "INSERT INTO todo(todo, note) VALUES(?,?)";
            PreparedStatement pStmt = conn.prepareCall(sql);
            pStmt.setString(1, task);
            pStmt.setString(2, note);
            pStmt.executeUpdate();
            System.out.println("Add Complete");
        }
    }

    public  void updateItem(Scanner sc) throws SQLException {
        System.out.println("Update Item");
        System.out.println("Which item do you want to update?");
        String itemId = sc.nextLine();
        try( Connection conn = ds.getConnection()) {
            String sql = "SELECT * FROM todo WHERE id = ?";
            PreparedStatement pStmt = conn.prepareCall(sql);
            pStmt.setString(1, itemId);
            ResultSet rs = pStmt.executeQuery();
            rs.next();
            ToDo td = new ToDo();
            td.setId(rs.getInt("id"));
            td.setTodo(rs.getString("todo"));
            td.setNote(rs.getString("note"));
            td.setFinished(rs.getBoolean("finished"));

            System.out.println("1. ToDo - " + td.getTodo());
            System.out.println("2. Note - " + td.getNote());
            System.out.println("3. Finished - " + td.isFinished());
            System.out.println("What would you like to change?");

            String choice = sc.nextLine();
            switch(choice) {
                case "1":
                    System.out.println("Enter new ToDo:");
                    String todo = sc.nextLine();
                    td.setTodo(todo);
                    break;
                case "2":
                    System.out.println("Enter new Note:");
                    String note = sc.nextLine();
                    td.setNote(note);
                    break;
                case "3":
                    System.out.println("Toggling Finished to " + !td.isFinished());
                    td.setFinished(!td.isFinished());
                    break;
                default:
                    System.out.println("No change made");
                    return;
            }


            String updateSql = "UPDATE todo SET todo = ?, note = ?, finished = ? WHERE id = ?";
            PreparedStatement updatePStmt = conn.prepareCall(updateSql);
            updatePStmt.setString(1, td.getTodo());
            updatePStmt.setString(2, td.getNote());
            updatePStmt.setBoolean(3, td.isFinished());
            updatePStmt.setInt(4, td.getId());
            updatePStmt.executeUpdate();
            System.out.println("Update Complete");
        }
    }


}
