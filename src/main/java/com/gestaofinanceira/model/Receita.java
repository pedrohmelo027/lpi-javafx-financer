package com.gestaofinanceira.model;

import java.time.LocalDateTime;

public class Receita extends Transacao {

    public Receita() {
        super();
        setTipo("ENTRADA");
    }

    public Receita(Integer id, double valor, CategoriaFinanceira categoria, LocalDateTime dataTransacao, String descricao) {
        super(id, valor, "ENTRADA", categoria, dataTransacao, descricao);
    }

    @Override
    public double aplicarValor() {
        return getValor();
    }

    // Method from class diagram
    public double calcularEntrada() {
        return getValor();
    }
}
