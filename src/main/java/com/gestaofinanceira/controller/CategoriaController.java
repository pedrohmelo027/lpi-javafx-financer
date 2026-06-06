package com.gestaofinanceira.controller;

import com.gestaofinanceira.model.CategoriaFinanceira;
import com.gestaofinanceira.service.CategoriaService;

import java.util.List;

public class CategoriaController {
    private final CategoriaService categoriaService;

    public CategoriaController() {
        this.categoriaService = new CategoriaService();
    }

    public void salvarCategoria(String nome) {
        categoriaService.salvar(nome);
    }

    public List<CategoriaFinanceira> listarCategorias() {
        return categoriaService.listar();
    }
}
