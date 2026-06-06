package com.gestaofinanceira.controller;

import com.gestaofinanceira.model.Relatorio;
import com.gestaofinanceira.model.Transacao;
import com.gestaofinanceira.service.RelatorioService;
import com.gestaofinanceira.service.RelatorioFactory;
import com.gestaofinanceira.service.TransacaoService;

import java.util.List;

public class RelatorioController {
    private final RelatorioService relatorioService;
    private final TransacaoService transacaoService;

    public RelatorioController() {
        this.relatorioService = new RelatorioService();
        this.transacaoService = new TransacaoService();
    }

    public Relatorio processarRelatorio(RelatorioFactory factory) {
        return relatorioService.gerarRelatorio(factory);
    }

    public double calcularSaldo(List<Transacao> transacoes) {
        return transacaoService.calcularSaldo(transacoes);
    }

    public double calcularTotalReceitas(List<Transacao> transacoes) {
        return relatorioService.calcularTotalReceitas(transacoes);
    }

    public double calcularTotalDespesas(List<Transacao> transacoes) {
        return relatorioService.calcularTotalDespesas(transacoes);
    }

    public double calcularTotalInvestido(List<Transacao> transacoes) {
        return relatorioService.calcularTotalInvestido(transacoes);
    }
}
