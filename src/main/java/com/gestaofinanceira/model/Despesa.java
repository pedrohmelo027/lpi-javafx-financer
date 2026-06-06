package com.gestaofinanceira.model;

import java.time.LocalDateTime;

public class Despesa extends Transacao {

    public Despesa() {
        super();
        setTipo("SAÍDA");
    }

    public Despesa(Integer id, double valor, CategoriaFinanceira categoria, LocalDateTime dataTransacao, String descricao) {
        super(id, valor, "SAÍDA", categoria, dataTransacao, descricao);
    }

    @Override
    public double aplicarValor() {
        return -getValor();
    }

    // Method from class diagram
    public double calcularSaida() {
        return getValor();
    }
}
