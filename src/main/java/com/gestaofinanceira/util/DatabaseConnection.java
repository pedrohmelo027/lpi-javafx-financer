package com.gestaofinanceira.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    public static Connection getConnection() throws SQLException {
        return ConnectionFactory.getConnection();
    }

    private static void bootstrapDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Driver MySQL não encontrado: " + e.getMessage());
        }
        String urlWithoutDb = "jdbc:mysql://localhost:3306/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        try (Connection conn = DriverManager.getConnection(urlWithoutDb, "root", "Esc@159753");
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE DATABASE IF NOT EXISTS finance");
        } catch (SQLException e) {
            System.err.println("Aviso ao inicializar o servidor de banco de dados MySQL: " + e.getMessage());
            System.err.println("Certifique-se de que o servidor MySQL está rodando no localhost:3306 para o usuário root.");
        }
    }

    public static void initializeDatabase() {
        bootstrapDatabase();

        String sqlTransactions = "CREATE TABLE IF NOT EXISTS transactions ("
                   + "id INT PRIMARY KEY AUTO_INCREMENT,"
                   + "amount DOUBLE NOT NULL,"
                   + "type VARCHAR(50) NOT NULL,"
                   + "category VARCHAR(50) NOT NULL,"
                   + "date VARCHAR(50) NOT NULL,"
                   + "description TEXT"
                   + ");";

        String sqlCategories = "CREATE TABLE IF NOT EXISTS categories ("
                   + "id INT PRIMARY KEY AUTO_INCREMENT,"
                   + "name VARCHAR(100) NOT NULL UNIQUE"
                   + ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlTransactions);
            stmt.execute(sqlCategories);

            // Popula com categorias padrão se a tabela estiver vazia
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM categories")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    stmt.execute("INSERT INTO categories(name) VALUES('Comida'), ('Transporte'), ('Casa'), ('Lazer'), ('Salário')");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao inicializar tabelas no banco de dados MySQL: " + e.getMessage());
        }
    }
}
