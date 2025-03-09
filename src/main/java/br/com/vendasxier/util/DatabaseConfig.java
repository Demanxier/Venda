package br.com.vendasxier.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    private static final String URL = "jdbc:postgresql://localhost:5434/vendasxier";
    private static final String USER = "postgres";
    private static final String PASSWORD = "Adm@123";

    public static Connection getConnection() throws SQLException{
        return DriverManager.getConnection(URL, USER, PASSWORD);

        // JDBC: API padrão para conexão com bancos relacionais
    }
}
