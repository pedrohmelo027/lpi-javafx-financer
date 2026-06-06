package com.gestaofinanceira.model;

import java.time.LocalDateTime;

public class Investimento extends Transacao {

    public Investimento() {
        super();
        setTipo("INVESTIMENTO");
    }

    public Investimento(Integer id, double valor, CategoriaFinanceira categoria, LocalDateTime dataTransacao, String descricao) {
        super(id, valor, "INVESTIMENTO", categoria, dataTransacao, descricao);
    }

    @Override
    public double aplicarValor() {
        return -getValor();
    }

    public double calcularInvestimento() {
        return getValor();
    }
}
