import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    public static Connection getConnection() throws Exception {

        String url = "jdbc:mysql://localhost:3306/student_db";
        String username = "root";
        String password = "Hariharan@1234";

        return DriverManager.getConnection(url, username, password);
    }
}