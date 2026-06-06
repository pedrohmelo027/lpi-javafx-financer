package com.gestaofinanceira.service;

import com.gestaofinanceira.model.Relatorio;
import com.gestaofinanceira.model.Transacao;
import com.gestaofinanceira.repository.TransacaoDAO;

import java.util.List;

public class RelatorioService {
    private final TransacaoDAO transacaoDAO;

    public RelatorioService() {
        this.transacaoDAO = new TransacaoDAO();
    }

    public Relatorio gerarRelatorio(RelatorioFactory factory) {
        List<Transacao> todasTransacoes = transacaoDAO.listar();
        return factory.criarRelatorio(todasTransacoes);
    }

    public double calcularTotalReceitas(List<Transacao> transacoes) {
        return transacoes.stream()
                .filter(t -> "ENTRADA".equals(t.getTipo()))
                .mapToDouble(Transacao::getValor)
                .sum();
    }

    public double calcularTotalDespesas(List<Transacao> transacoes) {
        return transacoes.stream()
                .filter(t -> "SAÍDA".equals(t.getTipo()))
                .mapToDouble(Transacao::getValor)
                .sum();
    }

    public double calcularTotalInvestido(List<Transacao> transacoes) {
        return transacoes.stream()
                .filter(t -> "INVESTIMENTO".equals(t.getTipo()))
                .mapToDouble(Transacao::getValor)
                .sum();
    }
}
