package com.erlon.segura;

import java.sql.Connection;
import java.sql.DriverManager;
public class Database {
    private static final String URL = "jdbc:postgresql://localhost:5432/erlonsegura";
    private static final String USER = "postgres";
    private static final String PASS = "123456";
    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(URL, USER, PASS);
    }
    public static void checkConnection() {
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("Conexão OK");
            } else {
                System.out.println("Falha na conexão");
            }
        } catch (Exception e) {
            System.out.println("Erro");
            e.printStackTrace();
        }
    }
}
