package com.gestaofinanceira.service;

import com.gestaofinanceira.model.Transacao;
import com.gestaofinanceira.repository.TransacaoDAO;

import java.util.List;

public class TransacaoService {
    private final TransacaoDAO transacaoDAO;

    public TransacaoService() {
        this.transacaoDAO = new TransacaoDAO();
    }

    public void salvar(Transacao transacao) {
        if (transacao.getValor() <= 0) {
            throw new IllegalArgumentException("O valor da movimentação deve ser maior que zero.");
        }
        if (transacao.getTipo() == null || (!transacao.getTipo().equals("ENTRADA") && !transacao.getTipo().equals("SAÍDA") && !transacao.getTipo().equals("INVESTIMENTO"))) {
            throw new IllegalArgumentException("O tipo de movimentação deve ser ENTRADA, SAÍDA ou INVESTIMENTO.");
        }
        if (transacao.getCategoria() == null) {
            throw new IllegalArgumentException("A categoria é obrigatória.");
        }
        if (transacao.getDate() == null) {
            throw new IllegalArgumentException("A data é obrigatória.");
        }

        transacaoDAO.salvar(transacao);
    }

    public List<Transacao> listar() {
        return transacaoDAO.listar();
    }

    public double calcularSaldo(List<Transacao> transacoes) {
        return transacoes.stream()
                .mapToDouble(Transacao::aplicarValor)
                .sum();
    }

    public List<Transacao> getByFilter(int month, int year) {
        return transacaoDAO.getByFilter(month, year);
    }

    public void deletar(int id) {
        transacaoDAO.delete(id);
    }
}
