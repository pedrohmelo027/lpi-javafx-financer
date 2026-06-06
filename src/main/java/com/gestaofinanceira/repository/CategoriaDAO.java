package com.gestaofinanceira.repository;

import com.gestaofinanceira.model.CategoriaFinanceira;
import com.gestaofinanceira.util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    public void salvar(String nome) {
        String sql = "INSERT INTO categories(name) VALUES(?) ON DUPLICATE KEY UPDATE name=name";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nome);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao salvar categoria: " + e.getMessage());
        }
    }

    public List<CategoriaFinanceira> listar() {
        List<CategoriaFinanceira> list = new ArrayList<>();
        String sql = "SELECT * FROM categories ORDER BY name ASC";
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new CategoriaFinanceira(rs.getInt("id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar categorias: " + e.getMessage());
        }
        return list;
    }
}
