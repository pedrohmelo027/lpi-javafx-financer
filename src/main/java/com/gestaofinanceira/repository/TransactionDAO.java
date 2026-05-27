package com.gestaofinanceira.repository;

import com.gestaofinanceira.model.Category;
import com.gestaofinanceira.model.Transaction;
import com.gestaofinanceira.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void insert(Transaction transaction) {
        String sql = "INSERT INTO transactions(amount, type, category, date, description) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setDouble(1, transaction.getAmount());
            pstmt.setString(2, transaction.getType());
            pstmt.setString(3, transaction.getCategory().name());
            pstmt.setString(4, transaction.getDate().format(DATE_FORMATTER));
            pstmt.setString(5, transaction.getDescription());
            
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    transaction.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao inserir transação: " + e.getMessage());
        }
    }

    public void update(Transaction transaction) {
        String sql = "UPDATE transactions SET amount = ?, type = ?, category = ?, date = ?, description = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, transaction.getAmount());
            pstmt.setString(2, transaction.getType());
            pstmt.setString(3, transaction.getCategory().name());
            pstmt.setString(4, transaction.getDate().format(DATE_FORMATTER));
            pstmt.setString(5, transaction.getDescription());
            pstmt.setInt(6, transaction.getId());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar transação: " + e.getMessage());
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM transactions WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao excluir transação: " + e.getMessage());
        }
    }

    public List<Transaction> getAll() {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions ORDER BY date DESC, id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                list.add(extractFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar transações: " + e.getMessage());
        }
        return list;
    }

    public List<Transaction> getByFilter(int month, int year) {
        List<Transaction> list = new ArrayList<>();
        String monthStr = String.format("%02d", month);
        String yearStr = String.valueOf(year);
        String pattern = yearStr + "-" + monthStr + "-%";
        
        String sql = "SELECT * FROM transactions WHERE date LIKE ? ORDER BY date DESC, id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, pattern);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(extractFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao filtrar transações: " + e.getMessage());
        }
        return list;
    }

    private Transaction extractFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        double amount = rs.getDouble("amount");
        String type = rs.getString("type");
        Category category = Category.valueOf(rs.getString("category"));
        LocalDate date = LocalDate.parse(rs.getString("date"), DATE_FORMATTER);
        String description = rs.getString("description");
        
        return new Transaction(id, amount, type, category, date, description);
    }
}
