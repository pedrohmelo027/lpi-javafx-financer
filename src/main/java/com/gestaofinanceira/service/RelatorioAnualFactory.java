package com.gestaofinanceira.service;

import com.gestaofinanceira.model.Relatorio;
import com.gestaofinanceira.model.Transacao;

import java.util.List;
import java.util.stream.Collectors;

public class RelatorioAnualFactory implements RelatorioFactory {
    private final int ano;

    public RelatorioAnualFactory(int ano) {
        this.ano = ano;
    }

    @Override
    public Relatorio criarRelatorio(List<Transacao> todasTransacoes) {
        List<Transacao> filtradas = todasTransacoes.stream()
                .filter(t -> t.getDate() != null && t.getDate().getYear() == ano)
                .collect(Collectors.toList());

        double totalReceitas = filtradas.stream()
                .filter(t -> "ENTRADA".equals(t.getTipo()))
                .mapToDouble(Transacao::getValor)
                .sum();

        double totalDespesas = filtradas.stream()
                .filter(t -> "SAÍDA".equals(t.getTipo()))
                .mapToDouble(Transacao::getValor)
                .sum();

        double totalInvestido = filtradas.stream()
                .filter(t -> "INVESTIMENTO".equals(t.getTipo()))
                .mapToDouble(Transacao::getValor)
                .sum();

        double saldo = totalReceitas - totalDespesas - totalInvestido;

        String titulo = String.format("Relatório Anual - %d", ano);

        return new Relatorio(titulo, filtradas, totalReceitas, totalDespesas, totalInvestido, saldo);
    }
}
