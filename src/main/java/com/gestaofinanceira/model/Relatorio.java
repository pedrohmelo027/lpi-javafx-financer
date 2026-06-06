package com.gestaofinanceira.model;

import java.util.List;

public class Relatorio {
    private String titulo;
    private List<Transacao> transacoes;
    private double totalReceitas;
    private double totalDespesas;
    private double totalInvestido;
    private double saldo;

    public Relatorio() {}

    public Relatorio(String titulo, List<Transacao> transacoes, double totalReceitas, double totalDespesas, double totalInvestido, double saldo) {
        this.titulo = titulo;
        this.transacoes = transacoes;
        this.totalReceitas = totalReceitas;
        this.totalDespesas = totalDespesas;
        this.totalInvestido = totalInvestido;
        this.saldo = saldo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public List<Transacao> getTransacoes() {
        return transacoes;
    }

    public void setTransacoes(List<Transacao> transacoes) {
        this.transacoes = transacoes;
    }

    public double getTotalReceitas() {
        return totalReceitas;
    }

    public void setTotalReceitas(double totalReceitas) {
        this.totalReceitas = totalReceitas;
    }

    public double getTotalDespesas() {
        return totalDespesas;
    }

    public void setTotalDespesas(double totalDespesas) {
        this.totalDespesas = totalDespesas;
    }

    public double getTotalInvestido() {
        return totalInvestido;
    }

    public void setTotalInvestido(double totalInvestido) {
        this.totalInvestido = totalInvestido;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }
}
