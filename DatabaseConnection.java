package finalProject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;
import java.util.UUID;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/seruputteh"; //seruputteh nama db nya
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    } //method utk nanti dipanggil tiap kli mau  mysql
    
    

    


}