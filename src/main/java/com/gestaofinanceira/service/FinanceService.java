package com.gestaofinanceira.service;

import com.gestaofinanceira.model.Transaction;
import com.gestaofinanceira.repository.TransactionDAO;

import java.util.List;

public class FinanceService {
    private final TransactionDAO transactionDAO;

    public FinanceService() {
        this.transactionDAO = new TransactionDAO();
    }

    public void saveTransaction(Transaction transaction) {
        if (transaction.getAmount() <= 0) {
            throw new IllegalArgumentException("O valor da movimentação deve ser maior que zero.");
        }
        if (transaction.getType() == null || (!transaction.getType().equals("ENTRADA") && !transaction.getType().equals("SAÍDA") && !transaction.getType().equals("INVESTIMENTO"))) {
            throw new IllegalArgumentException("O tipo de movimentação deve ser ENTRADA, SAÍDA ou INVESTIMENTO.");
        }
        if (transaction.getCategory() == null) {
            throw new IllegalArgumentException("A categoria é obrigatória.");
        }
        if (transaction.getDate() == null) {
            throw new IllegalArgumentException("A data é obrigatória.");
        }

        if (transaction.getId() == null) {
            transactionDAO.insert(transaction);
        } else {
            transactionDAO.update(transaction);
        }
    }

    public void deleteTransaction(int id) {
        transactionDAO.delete(id);
    }

    public List<Transaction> getTransactions(int month, int year) {
        return transactionDAO.getByFilter(month, year);
    }

    public List<Transaction> getAllTransactions() {
        return transactionDAO.getAll();
    }

    public double calculateTotalIncome(List<Transaction> transactions) {
        return transactions.stream()
                .filter(t -> "ENTRADA".equals(t.getType()))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double calculateTotalExpenses(List<Transaction> transactions) {
        return transactions.stream()
                .filter(t -> "SAÍDA".equals(t.getType()))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double calculateTotalInvested(List<Transaction> transactions) {
        return transactions.stream()
                .filter(t -> "INVESTIMENTO".equals(t.getType()))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double calculateBalance(List<Transaction> transactions) {
        return calculateTotalIncome(transactions) - calculateTotalExpenses(transactions) - calculateTotalInvested(transactions);
    }
}
