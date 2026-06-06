package com.gestaofinanceira.view;

import com.gestaofinanceira.model.CategoriaFinanceira;
import com.gestaofinanceira.model.Transacao;
import com.gestaofinanceira.model.InvestmentEvolution;
import com.gestaofinanceira.controller.TransacaoController;
import javafx.scene.chart.LineChart;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.*;

public class MenuView {

    @FXML private ComboBox<String> cbFilterMonth;
    @FXML private ComboBox<Integer> cbFilterYear;

    @FXML private Label lblTotalIncome;
    @FXML private Label lblTotalExpenses;
    @FXML private Label lblTotalInvested;
    @FXML private Label lblBalance;
    @FXML private VBox cardBalance;

    @FXML private TableView<Transacao> tvTransactions;
    @FXML private TableColumn<Transacao, LocalDate> colDate;
    @FXML private TableColumn<Transacao, CategoriaFinanceira> colCategory;
    @FXML private TableColumn<Transacao, String> colType;
    @FXML private TableColumn<Transacao, Double> colAmount;
    @FXML private TableColumn<Transacao, String> colDescription;
    @FXML private TableColumn<Transacao, Void> colActions;

    @FXML private BorderPane rootPane;
    @FXML private Button btnThemeToggle;
    private boolean isDarkTheme = true;

    @FXML private Label lblFormTitle;
    @FXML private ToggleGroup tgType;
    @FXML private RadioButton rbIncome;
    @FXML private RadioButton rbExpense;
    @FXML private RadioButton rbInvestment;
    @FXML private TextField txtAmount;
    @FXML private DatePicker dpDate;
    @FXML private ComboBox<CategoriaFinanceira> cbCategory;
    @FXML private TextField txtDescription;
    @FXML private Button btnSave;
    @FXML private Button btnCancel;

    @FXML private Button btnViewTable;
    @FXML private Button btnViewCharts;
    @FXML private TabPane paneCharts;

    @FXML private PieChart chartCategories;
    @FXML private BarChart<String, Number> chartMonthlyComparison;
    @FXML private CategoryAxis categoryAxis;
    @FXML private NumberAxis numberAxis;

    @FXML private Label lblSimTotalInvested;
    @FXML private Label lblSimTotalInterest;
    @FXML private Label lblSimFinalBalance;
    @FXML private Button btnSimViewChart;
    @FXML private Button btnSimViewTable;
    @FXML private LineChart<String, Number> chartSimEvolution;
    @FXML private CategoryAxis axisSimPeriod;
    @FXML private NumberAxis axisSimAmount;
    @FXML private TableView<InvestmentEvolution> tvSimEvolution;
    @FXML private TableColumn<InvestmentEvolution, Integer> colSimPeriod;
    @FXML private TableColumn<InvestmentEvolution, Double> colSimInitial;
    @FXML private TableColumn<InvestmentEvolution, Double> colSimContribution;
    @FXML private TableColumn<InvestmentEvolution, Double> colSimInterest;
    @FXML private TableColumn<InvestmentEvolution, Double> colSimFinal;
    @FXML private TableColumn<InvestmentEvolution, Double> colSimTotalInvested;
    @FXML private TableColumn<InvestmentEvolution, Double> colSimTotalInterest;
    @FXML private TextField txtSimInitialAmount;
    @FXML private TextField txtSimMonthlyContribution;
    @FXML private TextField txtSimInterestRate;
    @FXML private RadioButton rbSimMonthly;
    @FXML private RadioButton rbSimAnnual;
    @FXML private ToggleGroup tgSimInterestType;
    @FXML private TextField txtSimPeriod;
    @FXML private ComboBox<String> cbSimPeriodType;

    private TransacaoView transacaoView;
    private RelatorioView relatorioView;
    private TransacaoController transacaoController;

    private static final String[] MONTHS = {
        "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
        "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
    };

    @FXML
    public void initialize() {
        transacaoController = new TransacaoController();

        cbFilterMonth.getItems().addAll(MONTHS);
        int currentYear = LocalDate.now().getYear();
        for (int y = currentYear - 3; y <= currentYear + 2; y++) {
            cbFilterYear.getItems().add(y);
        }

        cbFilterMonth.setValue(MONTHS[LocalDate.now().getMonthValue() - 1]);
        cbFilterYear.setValue(currentYear);

        cbCategory.getItems().addAll(CategoriaFinanceira.values());
        dpDate.setValue(LocalDate.now());

        transacaoView = new TransacaoView(
                this::loadData,
                tvTransactions, colDate, colCategory, colType, colAmount, colDescription, colActions,
                lblFormTitle, tgType, rbIncome, rbExpense, rbInvestment, txtAmount, dpDate, cbCategory, txtDescription, btnSave, btnCancel
        );

        relatorioView = new RelatorioView(
                lblTotalIncome, lblTotalExpenses, lblTotalInvested, lblBalance, cardBalance,
                chartCategories, chartMonthlyComparison
        );

        cbFilterMonth.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> loadData());
        cbFilterYear.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> loadData());

        rootPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            double width = newVal.doubleValue();
            if (width > 0) {
                double baseSize = 11.0 + (width - 800.0) * (5.0 / 1120.0);
                if (baseSize < 11.0) baseSize = 11.0;
                if (baseSize > 16.0) baseSize = 16.0;
                rootPane.setStyle("-fx-font-size: " + String.format(Locale.US, "%.1f", baseSize) + "px;");
            }
        });

        cbSimPeriodType.getItems().addAll("Meses", "Anos");
        cbSimPeriodType.setValue("Anos");
        setupSimTableColumns();

        txtSimInitialAmount.setText("10000,00");
        txtSimMonthlyContribution.setText("500,00");
        txtSimInterestRate.setText("10,0");
        rbSimAnnual.setSelected(true);
        txtSimPeriod.setText("5");
        handleCalculateSim();

        exibirMenu();
    }

    public void exibirMenu() {
        loadData();
    }

    private void loadData() {
        int month = getSelectedMonthValue();
        int year = cbFilterYear.getValue();

        transacaoView.listarTransacoes(month, year);

        List<Transacao> currentTransactions = transacaoController.listarTransacoes(month, year);
        relatorioView.exibirSaldo(currentTransactions, year);
    }

    private int getSelectedMonthValue() {
        String selectedMonth = cbFilterMonth.getValue();
        for (int i = 0; i < MONTHS.length; i++) {
            if (MONTHS[i].equals(selectedMonth)) {
                return i + 1;
            }
        }
        return LocalDate.now().getMonthValue();
    }

    @FXML
    private void handleSave() {
        transacaoView.handleSave();
    }

    @FXML
    private void handleCancel() {
        transacaoView.handleCancel();
    }

    @FXML
    private void handleExportCSV() {
        int month = getSelectedMonthValue();
        String monthName = cbFilterMonth.getValue();
        int year = cbFilterYear.getValue();
        List<Transacao> currentTransactions = transacaoController.listarTransacoes(month, year);
        Stage stage = (Stage) tvTransactions.getScene().getWindow();
        
        relatorioView.solicitarRelatorio(month, monthName, year, currentTransactions, stage);
    }

    @FXML
    private void handleNewIncome() {
        transacaoView.registrarReceita();
    }

    @FXML
    private void handleNewExpense() {
        transacaoView.registrarDespesa();
    }

    @FXML
    private void handleThemeToggle() {
        isDarkTheme = !isDarkTheme;
        if (isDarkTheme) {
            rootPane.getStyleClass().remove("light-theme");
            btnThemeToggle.setText("☀️ Tema Claro");
        } else {
            rootPane.getStyleClass().add("light-theme");
            btnThemeToggle.setText("🌙 Tema Escuro");
        }
    }

    @FXML
    private void handleShowTable() {
        tvTransactions.setVisible(true);
        tvTransactions.setManaged(true);
        paneCharts.setVisible(false);
        paneCharts.setManaged(false);

        btnViewTable.getStyleClass().add("btn-segmented-active");
        btnViewCharts.getStyleClass().remove("btn-segmented-active");
    }

    @FXML
    private void handleShowCharts() {
        tvTransactions.setVisible(false);
        tvTransactions.setManaged(false);
        paneCharts.setVisible(true);
        paneCharts.setManaged(true);

        btnViewTable.getStyleClass().remove("btn-segmented-active");
        btnViewCharts.getStyleClass().add("btn-segmented-active");

        loadData();
    }

    private void setupSimTableColumns() {
        colSimPeriod.setCellValueFactory(new PropertyValueFactory<>("period"));
        colSimPeriod.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("Mês %d", item));
                }
            }
        });

        setupSimDoubleColumn(colSimInitial, "initialBalance");
        setupSimDoubleColumn(colSimContribution, "contribution");
        setupSimDoubleColumn(colSimInterest, "interestEarned");
        setupSimDoubleColumn(colSimFinal, "finalBalance");
        setupSimDoubleColumn(colSimTotalInvested, "totalInvested");
        setupSimDoubleColumn(colSimTotalInterest, "cumulativeInterest");
    }

    private void setupSimDoubleColumn(TableColumn<InvestmentEvolution, Double> column, String propertyName) {
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        column.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("R$ %,.2f", item));
                }
            }
        });
    }

    @FXML
    private void handleCalculateSim() {
        try {
            String initialStr = txtSimInitialAmount.getText().trim().replace(",", ".");
            if (initialStr.isEmpty()) throw new IllegalArgumentException("O valor inicial é obrigatório.");
            double initialAmount = Double.parseDouble(initialStr);
            if (initialAmount < 0) throw new IllegalArgumentException("O valor inicial não pode ser negativo.");

            String contributionStr = txtSimMonthlyContribution.getText().trim().replace(",", ".");
            double monthlyContribution = 0;
            if (!contributionStr.isEmpty()) {
                monthlyContribution = Double.parseDouble(contributionStr);
                if (monthlyContribution < 0) throw new IllegalArgumentException("O aporte mensal não pode ser negativo.");
            }

            String rateStr = txtSimInterestRate.getText().trim().replace(",", ".");
            if (rateStr.isEmpty()) throw new IllegalArgumentException("A taxa de juros é obrigatória.");
            double inputRate = Double.parseDouble(rateStr);
            if (inputRate < 0) throw new IllegalArgumentException("A taxa de juros não pode ser negativa.");

            String periodStr = txtSimPeriod.getText().trim();
            if (periodStr.isEmpty()) throw new IllegalArgumentException("O período é obrigatório.");
            int inputPeriod = Integer.parseInt(periodStr);
            if (inputPeriod <= 0) throw new IllegalArgumentException("O período deve ser maior que zero.");

            int totalMonths = inputPeriod;
            String periodType = cbSimPeriodType.getValue();
            if ("Anos".equals(periodType)) {
                totalMonths = inputPeriod * 12;
            }

            if (totalMonths > 600) {
                throw new IllegalArgumentException("O período máximo para simulação é de 50 anos (600 meses).");
            }

            double monthlyRate;
            if (rbSimMonthly.isSelected()) {
                monthlyRate = inputRate / 100.0;
            } else {
                double annualRate = inputRate / 100.0;
                monthlyRate = Math.pow(1.0 + annualRate, 1.0 / 12.0) - 1.0;
            }

            List<InvestmentEvolution> evolutionList = new ArrayList<>();
            double currentBalance = initialAmount;
            double accumulatedInterest = 0;
            double accumulatedInvested = initialAmount;

            for (int month = 1; month <= totalMonths; month++) {
                double initialBalance = currentBalance;
                double baseAmount = initialBalance + monthlyContribution;
                accumulatedInvested += monthlyContribution;

                double interestEarned = baseAmount * monthlyRate;
                accumulatedInterest += interestEarned;

                double finalBalance = baseAmount + interestEarned;
                currentBalance = finalBalance;

                InvestmentEvolution step = new InvestmentEvolution(
                    month,
                    initialBalance,
                    monthlyContribution,
                    interestEarned,
                    finalBalance,
                    accumulatedInterest,
                    accumulatedInvested
                );
                evolutionList.add(step);
            }

            lblSimTotalInvested.setText(String.format("R$ %,.2f", accumulatedInvested));
            lblSimTotalInterest.setText(String.format("R$ %,.2f", accumulatedInterest));
            lblSimFinalBalance.setText(String.format("R$ %,.2f", currentBalance));

            ObservableList<InvestmentEvolution> observableEvolution = FXCollections.observableArrayList(evolutionList);
            tvSimEvolution.setItems(observableEvolution);

            chartSimEvolution.getData().clear();

            XYChart.Series<String, Number> investedSeries = new XYChart.Series<>();
            investedSeries.setName("Total Investido");

            XYChart.Series<String, Number> accumulatedSeries = new XYChart.Series<>();
            accumulatedSeries.setName("Montante Total");

            int sampleInterval = 1;
            if (totalMonths > 36) {
                sampleInterval = Math.max(1, totalMonths / 20);
            }

            for (int i = 0; i < evolutionList.size(); i++) {
                if (i == 0 || (i + 1) % sampleInterval == 0 || (i + 1) == totalMonths) {
                    InvestmentEvolution step = evolutionList.get(i);
                    String label = "Mês " + step.getPeriod();
                    if ("Anos".equals(periodType) && step.getPeriod() % 12 == 0) {
                        label = "Ano " + (step.getPeriod() / 12);
                    }
                    investedSeries.getData().add(new XYChart.Data<>(label, step.getTotalInvested()));
                    accumulatedSeries.getData().add(new XYChart.Data<>(label, step.getFinalBalance()));
                }
            }

            chartSimEvolution.getData().addAll(investedSeries, accumulatedSeries);

        } catch (NumberFormatException e) {
            showFeedback("Erro de Validação", "Por favor, digite apenas números válidos nos campos correspondentes.", Alert.AlertType.WARNING);
        } catch (IllegalArgumentException e) {
            showFeedback("Erro de Validação", e.getMessage(), Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void handleClearSim() {
        txtSimInitialAmount.clear();
        txtSimMonthlyContribution.clear();
        txtSimInterestRate.clear();
        rbSimMonthly.setSelected(true);
        txtSimPeriod.clear();
        cbSimPeriodType.setValue("Anos");

        lblSimTotalInvested.setText("R$ 0,00");
        lblSimTotalInterest.setText("R$ 0,00");
        lblSimFinalBalance.setText("R$ 0,00");

        chartSimEvolution.getData().clear();
        tvSimEvolution.getItems().clear();
    }

    @FXML
    private void handleShowSimChart() {
        chartSimEvolution.setVisible(true);
        chartSimEvolution.setManaged(true);
        tvSimEvolution.setVisible(false);
        tvSimEvolution.setManaged(false);

        btnSimViewChart.getStyleClass().add("btn-segmented-active");
        btnSimViewTable.getStyleClass().remove("btn-segmented-active");
    }

    @FXML
    private void handleShowSimTable() {
        chartSimEvolution.setVisible(false);
        chartSimEvolution.setManaged(false);
        tvSimEvolution.setVisible(true);
        tvSimEvolution.setManaged(true);

        btnSimViewChart.getStyleClass().remove("btn-segmented-active");
        btnSimViewTable.getStyleClass().add("btn-segmented-active");
    }

    private void showFeedback(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
