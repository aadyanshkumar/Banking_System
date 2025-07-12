import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Connection;


public class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/bankdb";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root2005";
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}
