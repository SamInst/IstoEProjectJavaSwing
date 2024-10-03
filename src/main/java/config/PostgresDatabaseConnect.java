package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgresDatabaseConnect {
    public static Connection connect() {
        String url = "jdbc:postgresql://localhost:5432/isto_e_pousada";
        String user = "postgres";
        String password = "1234";
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to PostgreSQL database!");
        } catch (SQLException e) {
            System.out.println("Connection to PostgreSQL failed!");
            e.printStackTrace();
        }
        return connection;
    }
}

