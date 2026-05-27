package com.gestaofinanceira.controller;

import com.gestaofinanceira.model.Category;
import com.gestaofinanceira.model.Transaction;
import com.gestaofinanceira.model.InvestmentEvolution;
import com.gestaofinanceira.service.FinanceService;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class MainController {

    // FXML Bindings - Filters
    @FXML private ComboBox<String> cbFilterMonth;
    @FXML private ComboBox<Integer> cbFilterYear;

    // FXML Bindings - Resumo Cards
    @FXML private Label lblTotalIncome;
    @FXML private Label lblTotalExpenses;
    @FXML private Label lblTotalInvested;
    @FXML private Label lblBalance;
    @FXML private VBox cardBalance;

    // FXML Bindings - Table
    @FXML private TableView<Transaction> tvTransactions;
    @FXML private TableColumn<Transaction, LocalDate> colDate;
    @FXML private TableColumn<Transaction, Category> colCategory;
    @FXML private TableColumn<Transaction, String> colType;
    @FXML private TableColumn<Transaction, Double> colAmount;
    @FXML private TableColumn<Transaction, String> colDescription;
    @FXML private TableColumn<Transaction, Void> colActions;

    // Theme elements
    @FXML private BorderPane rootPane;
    @FXML private Button btnThemeToggle;
    private boolean isDarkTheme = true;

    // FXML Bindings - Form
    @FXML private Label lblFormTitle;
    @FXML private ToggleGroup tgType;
    @FXML private RadioButton rbIncome;
    @FXML private RadioButton rbExpense;
    @FXML private RadioButton rbInvestment;
    @FXML private TextField txtAmount;
    @FXML private DatePicker dpDate;
    @FXML private ComboBox<Category> cbCategory;
    @FXML private TextField txtDescription;
    @FXML private Button btnSave;
    @FXML private Button btnCancel;

    // FXML Bindings - View Toggles
    @FXML private Button btnViewTable;
    @FXML private Button btnViewCharts;
    @FXML private TabPane paneCharts;

    // FXML Bindings - Charts
    @FXML private PieChart chartCategories;
    @FXML private BarChart<String, Number> chartMonthlyComparison;
    @FXML private CategoryAxis categoryAxis;
    @FXML private NumberAxis numberAxis;

    // FXML Bindings - Investment Simulator
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


    // Core variables
    private FinanceService financeService;
    private ObservableList<Transaction> transactionList;
    private Transaction editingTransaction = null;

    private static final String[] MONTHS = {
        "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
        "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
    };

    @FXML
    public void initialize() {
        financeService = new FinanceService();
        transactionList = FXCollections.observableArrayList();

        // 1. Setup Filters
        cbFilterMonth.getItems().addAll(MONTHS);
        int currentYear = LocalDate.now().getYear();
        for (int y = currentYear - 3; y <= currentYear + 2; y++) {
            cbFilterYear.getItems().add(y);
        }

        // Set default filter to current month and year
        cbFilterMonth.setValue(MONTHS[LocalDate.now().getMonthValue() - 1]);
        cbFilterYear.setValue(currentYear);

        // 2. Setup Form Defaults
        cbCategory.getItems().addAll(Category.values());
        dpDate.setValue(LocalDate.now());

        // 3. Setup Table Columns
        setupTableColumns();

        // 4. Setup Listeners for Filters
        cbFilterMonth.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> loadData());
        cbFilterYear.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> loadData());

        // 5. Setup Responsive Font Sizing based on window/rootPane width
        rootPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            double width = newVal.doubleValue();
            if (width > 0) {
                // Dynamically scale base font size between 11px and 16px (across standard widths 800px to 1920px)
                double baseSize = 11.0 + (width - 800.0) * (5.0 / 1120.0);
                if (baseSize < 11.0) baseSize = 11.0;
                if (baseSize > 16.0) baseSize = 16.0;
                rootPane.setStyle("-fx-font-size: " + String.format(Locale.US, "%.1f", baseSize) + "px;");
            }
        });

        // 6. Setup Simulator Combo and default calculation
        cbSimPeriodType.getItems().addAll("Meses", "Anos");
        cbSimPeriodType.setValue("Anos");
        setupSimTableColumns();

        // Default simulation values
        txtSimInitialAmount.setText("10000,00");
        txtSimMonthlyContribution.setText("500,00");
        txtSimInterestRate.setText("10,0");
        rbSimAnnual.setSelected(true);
        txtSimPeriod.setText("5");
        handleCalculateSim();

        // Initial Data Load
        loadData();

    }

    private void setupTableColumns() {
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colDate.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                }
            }
        });

        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colCategory.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Category item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getDisplayName());
                }
            }
        });

        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colType.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    getStyleClass().removeAll("row-income", "row-expense", "row-investment");
                } else {
                    if ("ENTRADA".equals(item)) {
                        setText("Receita");
                    } else if ("SAÍDA".equals(item)) {
                        setText("Despesa");
                    } else if ("INVESTIMENTO".equals(item)) {
                        setText("Investimento");
                    } else {
                        setText(item);
                    }
                    getStyleClass().removeAll("row-income", "row-expense", "row-investment");
                    if ("ENTRADA".equals(item)) {
                        getStyleClass().add("row-income");
                    } else if ("SAÍDA".equals(item)) {
                        getStyleClass().add("row-expense");
                    } else if ("INVESTIMENTO".equals(item)) {
                        getStyleClass().add("row-investment");
                    }
                }
            }
        });

        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colAmount.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    getStyleClass().removeAll("row-income", "row-expense", "row-investment");
                } else {
                    Transaction t = getTableView().getItems().get(getIndex());
                    setText(String.format("R$ %,.2f", item));
                    getStyleClass().removeAll("row-income", "row-expense", "row-investment");
                    if ("ENTRADA".equals(t.getType())) {
                        getStyleClass().add("row-income");
                    } else if ("SAÍDA".equals(t.getType())) {
                        getStyleClass().add("row-expense");
                    } else if ("INVESTIMENTO".equals(t.getType())) {
                        getStyleClass().add("row-investment");
                    }
                }
            }
        });

        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Custom cell for inline Action Buttons
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit = new Button("Editar");
            private final Button btnDelete = new Button("Excluir");
            private final HBox container = new HBox(8, btnEdit, btnDelete);

            {
                btnEdit.getStyleClass().add("btn-action-edit");
                btnDelete.getStyleClass().add("btn-action-delete");
                container.setAlignment(javafx.geometry.Pos.CENTER);

                btnEdit.setOnAction(event -> {
                    Transaction t = getTableView().getItems().get(getIndex());
                    initiateEdit(t);
                });
                btnDelete.setOnAction(event -> {
                    Transaction t = getTableView().getItems().get(getIndex());
                    confirmDelete(t);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(container);
                }
            }
        });
    }

    private void loadData() {
        int month = getSelectedMonthValue();
        int year = cbFilterYear.getValue();

        // Load filtered list
        List<Transaction> transactions = financeService.getTransactions(month, year);
        transactionList.setAll(transactions);
        tvTransactions.setItems(transactionList);

        // Update KPIs
        updateKPIs(transactions);

        // Update Charts in real-time
        updateCharts(transactions);
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

    private void updateKPIs(List<Transaction> transactions) {
        double income = financeService.calculateTotalIncome(transactions);
        double expense = financeService.calculateTotalExpenses(transactions);
        double invested = financeService.calculateTotalInvested(transactions);
        double balance = financeService.calculateBalance(transactions);

        lblTotalIncome.setText(String.format("R$ %,.2f", income));
        lblTotalExpenses.setText(String.format("R$ %,.2f", expense));
        lblTotalInvested.setText(String.format("R$ %,.2f", invested));
        lblBalance.setText(String.format("R$ %,.2f", balance));

        // Dynamically style balance card
        cardBalance.getStyleClass().removeAll("kpi-balance-positive", "kpi-balance-negative");
        if (balance >= 0) {
            cardBalance.getStyleClass().add("kpi-balance-positive");
        } else {
            cardBalance.getStyleClass().add("kpi-balance-negative");
        }
    }



    private void initiateEdit(Transaction t) {
        editingTransaction = t;
        lblFormTitle.setText("Editar Movimentação");

        // Populate fields
        txtAmount.setText(String.format(Locale.US, "%.2f", t.getAmount()));
        rbIncome.setSelected("ENTRADA".equals(t.getType()));
        rbExpense.setSelected("SAÍDA".equals(t.getType()));
        rbInvestment.setSelected("INVESTIMENTO".equals(t.getType()));
        cbCategory.setValue(t.getCategory());
        dpDate.setValue(t.getDate());
        txtDescription.setText(t.getDescription());

        btnCancel.setVisible(true);
        btnSave.setText("Salvar Alterações");
    }

    private void confirmDelete(Transaction t) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Exclusão");
        alert.setHeaderText("Excluir movimentação?");
        alert.setContentText(String.format("Deseja realmente excluir a transação '%s' no valor de R$ %,.2f?", 
                t.getDescription() == null || t.getDescription().isEmpty() ? t.getCategory().getDisplayName() : t.getDescription(),
                t.getAmount()));

        // Styling the alert matching dark mode (optional dialog style, but native is safer)
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            financeService.deleteTransaction(t.getId());
            loadData();
            showFeedback("Sucesso", "Movimentação excluída com sucesso!", Alert.AlertType.INFORMATION);
            if (editingTransaction != null && editingTransaction.getId().equals(t.getId())) {
                resetForm();
            }
        }
    }

    @FXML
    private void handleSave() {
        try {
            String amountStr = txtAmount.getText().trim();
            if (amountStr.isEmpty()) {
                throw new IllegalArgumentException("O valor é obrigatório.");
            }

            // Replace comma with dot for validation
            amountStr = amountStr.replace(",", ".");
            double amount = Double.parseDouble(amountStr);

            String type = rbIncome.isSelected() ? "ENTRADA" : (rbExpense.isSelected() ? "SAÍDA" : "INVESTIMENTO");
            Category category = cbCategory.getValue();
            LocalDate date = dpDate.getValue();
            String description = txtDescription.getText().trim();

            Transaction transaction = editingTransaction;
            if (transaction == null) {
                transaction = new Transaction();
            }

            transaction.setAmount(amount);
            transaction.setType(type);
            transaction.setCategory(category);
            transaction.setDate(date);
            transaction.setDescription(description);

            // Service applies business rules and DAO saves/updates
            financeService.saveTransaction(transaction);

            // Reset UI Form
            resetForm();

            // Refresh Table and Graphs
            loadData();

            showFeedback("Sucesso", "Movimentação salva com sucesso!", Alert.AlertType.INFORMATION);

        } catch (NumberFormatException e) {
            showFeedback("Erro de Validação", "Insira um valor numérico válido.", Alert.AlertType.WARNING);
        } catch (IllegalArgumentException e) {
            showFeedback("Erro de Validação", e.getMessage(), Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void handleCancel() {
        resetForm();
    }

    private void resetForm() {
        editingTransaction = null;
        lblFormTitle.setText("Nova Movimentação");

        txtAmount.clear();
        rbIncome.setSelected(true);
        cbCategory.setValue(null);
        dpDate.setValue(LocalDate.now());
        txtDescription.clear();

        btnCancel.setVisible(false);
        btnSave.setText("Salvar Movimentação");
    }

    @FXML
    private void handleExportCSV() {
        if (transactionList.isEmpty()) {
            showFeedback("Aviso", "Não há transações no período selecionado para exportar.", Alert.AlertType.WARNING);
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar Relatório CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arquivos CSV (*.csv)", "*.csv"));
        
        String defaultFileName = String.format("relatorio_financeiro_%s_%d.csv", 
                cbFilterMonth.getValue().toLowerCase(), cbFilterYear.getValue());
        fileChooser.setInitialFileName(defaultFileName);

        Stage stage = (Stage) tvTransactions.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                // BOM header to support Excel UTF-8 display in Windows
                writer.write('\ufeff');
                writer.write("Data;Tipo;Categoria;Valor;Descrição\n");

                for (Transaction t : transactionList) {
                    writer.write(String.format("%s;%s;%s;%.2f;%s\n",
                            t.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            t.getType(),
                            t.getCategory().getDisplayName(),
                            t.getAmount(),
                            t.getDescription() == null ? "" : t.getDescription()
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

    @FXML
    private void handleNewIncome() {
        resetForm();
        rbIncome.setSelected(true);
        txtAmount.requestFocus();
    }

    @FXML
    private void handleNewExpense() {
        resetForm();
        rbExpense.setSelected(true);
        txtAmount.requestFocus();
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
        // Toggle visibility and managed state
        tvTransactions.setVisible(true);
        tvTransactions.setManaged(true);
        paneCharts.setVisible(false);
        paneCharts.setManaged(false);

        // Update button styles to segmented look
        btnViewTable.getStyleClass().add("btn-segmented-active");
        btnViewCharts.getStyleClass().remove("btn-segmented-active");
    }

    @FXML
    private void handleShowCharts() {
        // Toggle visibility and managed state
        tvTransactions.setVisible(false);
        tvTransactions.setManaged(false);
        paneCharts.setVisible(true);
        paneCharts.setManaged(true);

        // Update button styles to segmented look
        btnViewTable.getStyleClass().remove("btn-segmented-active");
        btnViewCharts.getStyleClass().add("btn-segmented-active");

        // Force a recalculation of charts
        loadData();
    }

    private void updateCharts(List<Transaction> currentTransactions) {
        if (chartCategories == null || chartMonthlyComparison == null) return;

        // --- 1. PIE CHART: Expenditures (SAÍDA) by category for current period
        chartCategories.getData().clear();
        
        List<Transaction> expenses = currentTransactions.stream()
                .filter(t -> "SAÍDA".equals(t.getType()))
                .collect(Collectors.toList());

        double totalExpenses = expenses.stream().mapToDouble(Transaction::getAmount).sum();

        if (totalExpenses > 0) {
            Map<Category, Double> expenseByCategory = expenses.stream()
                    .collect(Collectors.groupingBy(Transaction::getCategory, Collectors.summingDouble(Transaction::getAmount)));

            for (Map.Entry<Category, Double> entry : expenseByCategory.entrySet()) {
                double pct = (entry.getValue() / totalExpenses) * 100;
                String label = String.format("%s (%.1f%%)", entry.getKey().getDisplayName(), pct);
                chartCategories.getData().add(new PieChart.Data(label, entry.getValue()));
            }
        }

        // --- 2. BAR CHART: Comparison of income vs expense vs investment over months of the selected year
        chartMonthlyComparison.getData().clear();

        List<Transaction> yearlyTransactions = financeService.getAllTransactions().stream()
                .filter(t -> t.getDate().getYear() == cbFilterYear.getValue())
                .collect(Collectors.toList());

        XYChart.Series<String, Number> incomeSeries = new XYChart.Series<>();
        incomeSeries.setName("Receitas");

        XYChart.Series<String, Number> expenseSeries = new XYChart.Series<>();
        expenseSeries.setName("Despesas");

        XYChart.Series<String, Number> investmentSeries = new XYChart.Series<>();
        investmentSeries.setName("Investimentos");

        Map<Integer, Double> monthlyIncome = new HashMap<>();
        Map<Integer, Double> monthlyExpense = new HashMap<>();
        Map<Integer, Double> monthlyInvestment = new HashMap<>();

        for (Transaction t : yearlyTransactions) {
            int m = t.getDate().getMonthValue();
            if ("ENTRADA".equals(t.getType())) {
                monthlyIncome.put(m, monthlyIncome.getOrDefault(m, 0.0) + t.getAmount());
            } else if ("SAÍDA".equals(t.getType())) {
                monthlyExpense.put(m, monthlyExpense.getOrDefault(m, 0.0) + t.getAmount());
            } else if ("INVESTIMENTO".equals(t.getType())) {
                monthlyInvestment.put(m, monthlyInvestment.getOrDefault(m, 0.0) + t.getAmount());
            }
        }

        // Build chart series in chronological order for all 12 months
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
            // Read and parse starting amount
            String initialStr = txtSimInitialAmount.getText().trim().replace(",", ".");
            if (initialStr.isEmpty()) throw new IllegalArgumentException("O valor inicial é obrigatório.");
            double initialAmount = Double.parseDouble(initialStr);
            if (initialAmount < 0) throw new IllegalArgumentException("O valor inicial não pode ser negativo.");

            // Read and parse monthly contribution (optional, default to 0)
            String contributionStr = txtSimMonthlyContribution.getText().trim().replace(",", ".");
            double monthlyContribution = 0;
            if (!contributionStr.isEmpty()) {
                monthlyContribution = Double.parseDouble(contributionStr);
                if (monthlyContribution < 0) throw new IllegalArgumentException("O aporte mensal não pode ser negativo.");
            }

            // Read and parse interest rate
            String rateStr = txtSimInterestRate.getText().trim().replace(",", ".");
            if (rateStr.isEmpty()) throw new IllegalArgumentException("A taxa de juros é obrigatória.");
            double inputRate = Double.parseDouble(rateStr);
            if (inputRate < 0) throw new IllegalArgumentException("A taxa de juros não pode ser negativa.");

            // Read and parse period
            String periodStr = txtSimPeriod.getText().trim();
            if (periodStr.isEmpty()) throw new IllegalArgumentException("O período é obrigatório.");
            int inputPeriod = Integer.parseInt(periodStr);
            if (inputPeriod <= 0) throw new IllegalArgumentException("O período deve ser maior que zero.");

            // Standardize period to months and interest rate to monthly equivalent
            int totalMonths = inputPeriod;
            String periodType = cbSimPeriodType.getValue();
            if ("Anos".equals(periodType)) {
                totalMonths = inputPeriod * 12;
            }

            // Limit period to a reasonable size to prevent UI freezing (e.g. 50 years / 600 months)
            if (totalMonths > 600) {
                throw new IllegalArgumentException("O período máximo para simulação é de 50 anos (600 meses).");
            }

            double monthlyRate;
            if (rbSimMonthly.isSelected()) {
                monthlyRate = inputRate / 100.0;
            } else {
                // Annual interest to monthly compound equivalent formula: (1 + i_annual)^(1/12) - 1
                double annualRate = inputRate / 100.0;
                monthlyRate = Math.pow(1.0 + annualRate, 1.0 / 12.0) - 1.0;
            }

            // Perform month-by-month calculation
            List<InvestmentEvolution> evolutionList = new ArrayList<>();
            double currentBalance = initialAmount;
            double accumulatedInterest = 0;
            double accumulatedInvested = initialAmount;

            for (int month = 1; month <= totalMonths; month++) {
                double initialBalance = currentBalance;
                
                // Add contribution at start of month (standard financial assumption)
                double baseAmount = initialBalance + monthlyContribution;
                accumulatedInvested += monthlyContribution;

                // Calculate interest on baseAmount
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

            // Update KPI cards
            lblSimTotalInvested.setText(String.format("R$ %,.2f", accumulatedInvested));
            lblSimTotalInterest.setText(String.format("R$ %,.2f", accumulatedInterest));
            lblSimFinalBalance.setText(String.format("R$ %,.2f", currentBalance));

            // Bind to TableView
            ObservableList<InvestmentEvolution> observableEvolution = FXCollections.observableArrayList(evolutionList);
            tvSimEvolution.setItems(observableEvolution);

            // Bind to LineChart
            chartSimEvolution.getData().clear();

            XYChart.Series<String, Number> investedSeries = new XYChart.Series<>();
            investedSeries.setName("Total Investido");

            XYChart.Series<String, Number> accumulatedSeries = new XYChart.Series<>();
            accumulatedSeries.setName("Montante Total");

            // We sample the data points if there are many months (e.g. > 36 months) to keep the chart clean and readable
            int sampleInterval = 1;
            if (totalMonths > 36) {
                sampleInterval = Math.max(1, totalMonths / 20); // max 20 points
            }

            for (int i = 0; i < evolutionList.size(); i++) {
                if (i == 0 || (i + 1) % sampleInterval == 0 || (i + 1) == totalMonths) {
                    InvestmentEvolution step = evolutionList.get(i);
                    String label = "Mês " + step.getPeriod();
                    // If in years, we can label every 12 months as "Ano X"
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
}

