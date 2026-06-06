package com.gestaofinanceira.view;

import com.gestaofinanceira.model.*;
import com.gestaofinanceira.controller.RelatorioController;
import com.gestaofinanceira.controller.TransacaoController;
import com.gestaofinanceira.service.RelatorioMensalFactory;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class RelatorioView {
    private final RelatorioController relatorioController;

    private final Label lblTotalIncome;
    private final Label lblTotalExpenses;
    private final Label lblTotalInvested;
    private final Label lblBalance;
    private final VBox cardBalance;

    private final PieChart chartCategories;
    private final BarChart<String, Number> chartMonthlyComparison;

    private static final String[] MONTHS = {
        "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
        "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
    };

    public RelatorioView(
            Label lblTotalIncome,
            Label lblTotalExpenses,
            Label lblTotalInvested,
            Label lblBalance,
            VBox cardBalance,
            PieChart chartCategories,
            BarChart<String, Number> chartMonthlyComparison
    ) {
        this.relatorioController = new RelatorioController();
        this.lblTotalIncome = lblTotalIncome;
        this.lblTotalExpenses = lblTotalExpenses;
        this.lblTotalInvested = lblTotalInvested;
        this.lblBalance = lblBalance;
        this.cardBalance = cardBalance;
        this.chartCategories = chartCategories;
        this.chartMonthlyComparison = chartMonthlyComparison;
    }

    public void exibirSaldo(List<Transacao> transacoes, int year) {
        double income = relatorioController.calcularTotalReceitas(transacoes);
        double expense = relatorioController.calcularTotalDespesas(transacoes);
        double invested = relatorioController.calcularTotalInvestido(transacoes);
        double balance = relatorioController.calcularSaldo(transacoes);

        lblTotalIncome.setText(String.format("R$ %,.2f", income));
        lblTotalExpenses.setText(String.format("R$ %,.2f", expense));
        lblTotalInvested.setText(String.format("R$ %,.2f", invested));
        lblBalance.setText(String.format("R$ %,.2f", balance));

        cardBalance.getStyleClass().removeAll("kpi-balance-positive", "kpi-balance-negative");
        if (balance >= 0) {
            cardBalance.getStyleClass().add("kpi-balance-positive");
        } else {
            cardBalance.getStyleClass().add("kpi-balance-negative");
        }

        updateCharts(transacoes, year);
    }

    private void updateCharts(List<Transacao> currentTransactions, int year) {
        if (chartCategories == null || chartMonthlyComparison == null) return;

        chartCategories.getData().clear();
        List<Transacao> expenses = currentTransactions.stream()
                .filter(t -> "SAÍDA".equals(t.getTipo()))
                .collect(Collectors.toList());

        double totalExpenses = expenses.stream().mapToDouble(Transacao::getValor).sum();

        if (totalExpenses > 0) {
            Map<CategoriaFinanceira, Double> expenseByCategory = expenses.stream()
                    .collect(Collectors.groupingBy(Transacao::getCategoria, Collectors.summingDouble(Transacao::getValor)));

            for (Map.Entry<CategoriaFinanceira, Double> entry : expenseByCategory.entrySet()) {
                double pct = (entry.getValue() / totalExpenses) * 100;
                String label = String.format("%s (%.1f%%)", entry.getKey().getNome(), pct);
                chartCategories.getData().add(new PieChart.Data(label, entry.getValue()));
            }
        }

        chartMonthlyComparison.getData().clear();

        List<Transacao> yearlyTransactions = transacoesPorAno(year);

        XYChart.Series<String, Number> incomeSeries = new XYChart.Series<>();
        incomeSeries.setName("Receitas");

        XYChart.Series<String, Number> expenseSeries = new XYChart.Series<>();
        expenseSeries.setName("Despesas");

        XYChart.Series<String, Number> investmentSeries = new XYChart.Series<>();
        investmentSeries.setName("Investimentos");

        Map<Integer, Double> monthlyIncome = new HashMap<>();
        Map<Integer, Double> monthlyExpense = new HashMap<>();
        Map<Integer, Double> monthlyInvestment = new HashMap<>();

        for (Transacao t : yearlyTransactions) {
            int m = t.getDate().getMonthValue();
            if ("ENTRADA".equals(t.getTipo())) {
                monthlyIncome.put(m, monthlyIncome.getOrDefault(m, 0.0) + t.getValor());
            } else if ("SAÍDA".equals(t.getTipo())) {
                monthlyExpense.put(m, monthlyExpense.getOrDefault(m, 0.0) + t.getValor());
            } else if ("INVESTIMENTO".equals(t.getTipo())) {
                monthlyInvestment.put(m, monthlyInvestment.getOrDefault(m, 0.0) + t.getValor());
            }
        }

        for (int m = 1; m <= 12; m++) {
            double inc = monthlyIncome.getOrDefault(m, 0.0);
            double exp = monthlyExpense.getOrDefault(m, 0.0);
            double inv = monthlyInvestment.getOrDefault(m, 0.0);
            String monthName = MONTHS[m - 1].substring(0, 3);
            incomeSeries.getData().add(new XYChart.Data<>(monthName, inc));
            expenseSeries.getData().add(new XYChart.Data<>(monthName, exp));
            investmentSeries.getData().add(new XYChart.Data<>(monthName, inv));
        }

        chartMonthlyComparison.getData().addAll(incomeSeries, expenseSeries, investmentSeries);
    }

    private List<Transacao> transacoesPorAno(int year) {
        List<Transacao> all = new TransacaoController().listarTransacoes();
        return all.stream()
                .filter(t -> t.getDate() != null && t.getDate().getYear() == year)
                .collect(Collectors.toList());
    }

    public void solicitarRelatorio(int month, String monthName, int year, List<Transacao> currentPeriodTransactions, Stage stage) {
        if (currentPeriodTransactions.isEmpty()) {
            showFeedback("Aviso", "Não há transações no período selecionado para exportar.", Alert.AlertType.WARNING);
            return;
        }

        RelatorioMensalFactory factory = new RelatorioMensalFactory(month, year);
        Relatorio relatorio = relatorioController.processarRelatorio(factory);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar Relatório CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arquivos CSV (*.csv)", "*.csv"));
        
        String defaultFileName = String.format("relatorio_financeiro_%s_%d.csv", monthName.toLowerCase(), year);
        fileChooser.setInitialFileName(defaultFileName);

        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write('\ufeff');
                writer.write("Título: " + relatorio.getTitulo() + "\n");
                writer.write("Total Receitas: R$ " + String.format("%.2f", relatorio.getTotalReceitas()) + "\n");
                writer.write("Total Despesas: R$ " + String.format("%.2f", relatorio.getTotalDespesas()) + "\n");
                writer.write("Total Investido: R$ " + String.format("%.2f", relatorio.getTotalInvestido()) + "\n");
                writer.write("Saldo do Período: R$ " + String.format("%.2f", relatorio.getSaldo()) + "\n\n");
                
                writer.write("Data;Tipo;Categoria;Valor;Descrição\n");

                for (Transacao t : relatorio.getTransacoes()) {
                    writer.write(String.format("%s;%s;%s;%.2f;%s\n",
                            t.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            t.getTipo(),
                            t.getCategoria().getNome(),
                            t.getValor(),
                            t.getDescricao() == null ? "" : t.getDescricao()
                    ));
                }
                showFeedback("Sucesso", "Dados exportados com sucesso para:\n" + file.getAbsolutePath(), Alert.AlertType.INFORMATION);
            } catch (IOException e) {
                showFeedback("Erro ao Exportar", "Ocorreu um erro ao salvar o arquivo: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void showFeedback(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
