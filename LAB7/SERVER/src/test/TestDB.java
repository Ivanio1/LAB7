package test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestDB {
    private static final String DB_DRIVER = "org.postgresql.Driver";
    private static final String DB_CONNECTION = "jdbc:postgresql://pg:5432/studs";

    public static void main(String[] args) {
        TestDB db = new TestDB();
        db.testDatabase();
    }

    private void testDatabase() {

        try {
            Class.forName(DB_DRIVER);
            String login = "s336760";
            String password = "snm543";
            Connection dbConnection = DriverManager.getConnection(DB_CONNECTION, login, password);

            try {
                Statement stmt = dbConnection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM users");
                while (rs.next()) {
                    String str = rs.getString("user_id") + " " + rs.getString("user_name");
                    System.out.println("User:" + str);
                }
                rs.close();
                stmt.close();
            } finally {
                dbConnection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}