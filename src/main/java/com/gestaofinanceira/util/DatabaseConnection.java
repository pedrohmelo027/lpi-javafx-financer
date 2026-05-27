package com.gestaofinanceira.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:finance.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void initializeDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS transactions ("
                   + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                   + "amount DOUBLE NOT NULL,"
                   + "type TEXT NOT NULL,"
                   + "category TEXT NOT NULL,"
                   + "date TEXT NOT NULL,"
                   + "description TEXT"
                   + ");";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tabela transactions criada ou já existente.");
        } catch (SQLException e) {
            System.err.println("Erro ao inicializar o banco de dados: " + e.getMessage());
        }
    }
}
