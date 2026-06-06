package com.gestaofinanceira.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public abstract class Transacao {
    private Integer id;
    private String descricao;
    private double valor;
    private String tipo; // "ENTRADA", "SAÍDA", "INVESTIMENTO"
    private LocalDateTime dataTransacao;
    private CategoriaFinanceira categoria;

    public Transacao() {}

    public Transacao(Integer id, double valor, String tipo, CategoriaFinanceira categoria, LocalDateTime dataTransacao, String descricao) {
        this.id = id;
        this.valor = valor;
        this.tipo = tipo;
        this.categoria = categoria;
        this.dataTransacao = dataTransacao;
        this.descricao = descricao;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public LocalDateTime getDataTransacao() {
        return dataTransacao;
    }

    public void setDataTransacao(LocalDateTime dataTransacao) {
        this.dataTransacao = dataTransacao;
    }

    public CategoriaFinanceira getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaFinanceira categoria) {
        this.categoria = categoria;
    }

    // Compatibility helpers for existing TableView columns and property bindings
    public LocalDate getDate() {
        return dataTransacao != null ? dataTransacao.toLocalDate() : null;
    }

    public double getAmount() {
        return valor;
    }

    public String getDescription() {
        return descricao;
    }

    public CategoriaFinanceira getCategory() {
        return categoria;
    }

    public String getType() {
        return tipo;
    }

    // Abstract method from class diagram
    public abstract double aplicarValor();
}
