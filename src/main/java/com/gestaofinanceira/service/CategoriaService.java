package com.gestaofinanceira.service;

import com.gestaofinanceira.model.CategoriaFinanceira;
import com.gestaofinanceira.repository.CategoriaDAO;

import java.util.List;

public class CategoriaService {
    private final CategoriaDAO categoriaDAO;

    public CategoriaService() {
        this.categoriaDAO = new CategoriaDAO();
    }

    public void salvar(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome da categoria não pode ser vazio.");
        }

        String trimmedName = nome.trim();
        List<CategoriaFinanceira> existentes = categoriaDAO.listar();
        for (CategoriaFinanceira c : existentes) {
            if (c.getNome().equalsIgnoreCase(trimmedName)) {
                throw new IllegalArgumentException("Já existe uma categoria com este nome.");
            }
        }

        categoriaDAO.salvar(trimmedName);
    }

    public List<CategoriaFinanceira> listar() {
        return categoriaDAO.listar();
    }
}
