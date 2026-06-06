package com.gestaofinanceira.controller;

import com.gestaofinanceira.model.Transacao;
import com.gestaofinanceira.service.TransacaoService;

import java.util.List;

public class TransacaoController {
    private final TransacaoService transacaoService;

    public TransacaoController() {
        this.transacaoService = new TransacaoService();
    }

    public void salvarTransacao(Transacao transacao) {
        transacaoService.salvar(transacao);
    }

    public List<Transacao> listarTransacoes() {
        return transacaoService.listar();
    }

    public List<Transacao> listarTransacoes(int month, int year) {
        return transacaoService.getByFilter(month, year);
    }

    public void deletarTransacao(int id) {
        transacaoService.deletar(id);
    }
}
