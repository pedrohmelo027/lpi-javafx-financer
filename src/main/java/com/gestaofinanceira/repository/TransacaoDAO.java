package com.gestaofinanceira.repository;

import com.gestaofinanceira.model.*;
import com.gestaofinanceira.util.ConnectionFactory;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TransacaoDAO {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void salvar(Transacao transacao) {
        if (transacao.getId() == null) {
            insert(transacao);
        } else {
            update(transacao);
        }
    }

    private void insert(Transacao transacao) {
        String sql = "INSERT INTO transactions(amount, type, category, date, description) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setDouble(1, transacao.getValor());
            pstmt.setString(2, transacao.getTipo());
            pstmt.setString(3, transacao.getCategoria().name());
            pstmt.setString(4, transacao.getDate().format(DATE_FORMATTER));
            pstmt.setString(5, transacao.getDescricao());
            
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    transacao.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao inserir transação: " + e.getMessage());
        }
    }

    private void update(Transacao transacao) {
        String sql = "UPDATE transactions SET amount = ?, type = ?, category = ?, date = ?, description = ? WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, transacao.getValor());
            pstmt.setString(2, transacao.getTipo());
            pstmt.setString(3, transacao.getCategoria().name());
            pstmt.setString(4, transacao.getDate().format(DATE_FORMATTER));
            pstmt.setString(5, transacao.getDescricao());
            pstmt.setInt(6, transacao.getId());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar transação: " + e.getMessage());
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM transactions WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao excluir transação: " + e.getMessage());
        }
    }

    public List<Transacao> listar() {
        List<Transacao> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions ORDER BY date DESC, id DESC";
        try (Connection conn = ConnectionFactory.getConnection();
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

    public List<Transacao> getByFilter(int month, int year) {
        List<Transacao> list = new ArrayList<>();
        String monthStr = String.format("%02d", month);
        String yearStr = String.valueOf(year);
        String pattern = yearStr + "-" + monthStr + "-%";
        
        String sql = "SELECT * FROM transactions WHERE date LIKE ? ORDER BY date DESC, id DESC";
        try (Connection conn = ConnectionFactory.getConnection();
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

    private Transacao extractFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        double amount = rs.getDouble("amount");
        String type = rs.getString("type");
        CategoriaFinanceira category = CategoriaFinanceira.fromString(rs.getString("category"));
        LocalDate date = LocalDate.parse(rs.getString("date"), DATE_FORMATTER);
        LocalDateTime dateTime = LocalDateTime.of(date, LocalTime.MIDNIGHT);
        String description = rs.getString("description");
        
        if ("ENTRADA".equals(type)) {
            return new Receita(id, amount, category, dateTime, description);
        } else if ("INVESTIMENTO".equals(type)) {
            return new Investimento(id, amount, category, dateTime, description);
        } else {
            return new Despesa(id, amount, category, dateTime, description);
        }
    }
}
