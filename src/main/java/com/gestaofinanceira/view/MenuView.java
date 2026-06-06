package com.gestaofinanceira.view;

import com.gestaofinanceira.model.CategoriaFinanceira;
import com.gestaofinanceira.model.Transacao;

import com.gestaofinanceira.controller.TransacaoController;
import com.gestaofinanceira.controller.CategoriaController;
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
    @FXML private TextArea txtDescription;
    @FXML private Button btnSave;
    @FXML private Button btnCancel;

    @FXML private Button btnViewTable;
    @FXML private Button btnViewCharts;
    @FXML private TabPane paneCharts;

    @FXML private PieChart chartCategories;
    @FXML private BarChart<String, Number> chartMonthlyComparison;
    @FXML private CategoryAxis categoryAxis;
    @FXML private NumberAxis numberAxis;



    private TransacaoView transacaoView;
    private RelatorioView relatorioView;
    private TransacaoController transacaoController;
    private CategoriaController categoriaController;

    private static final String[] MONTHS = {
        "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
        "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
    };

    @FXML
    public void initialize() {
        transacaoController = new TransacaoController();
        categoriaController = new CategoriaController();

        cbFilterMonth.getItems().addAll(MONTHS);
        int currentYear = LocalDate.now().getYear();
        for (int y = currentYear - 3; y <= currentYear + 2; y++) {
            cbFilterYear.getItems().add(y);
        }

        cbFilterMonth.setValue(MONTHS[LocalDate.now().getMonthValue() - 1]);
        cbFilterYear.setValue(currentYear);

        loadCategories();
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
    private void handleNewIncome() {
        transacaoView.registrarReceita();
    }

    @FXML
    private void handleNewExpense() {
        transacaoView.registrarDespesa();
    }

    @FXML
    private void handleNewInvestment() {
        transacaoView.registrarInvestimento();
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



    private void loadCategories() {
        List<CategoriaFinanceira> list = categoriaController.listarCategorias();
        CategoriaFinanceira.setCategories(list);

        CategoriaFinanceira currentSel = cbCategory.getValue();

        cbCategory.getItems().clear();
        cbCategory.getItems().addAll(list);

        if (currentSel != null && list.contains(currentSel)) {
            cbCategory.setValue(currentSel);
        }
    }

    @FXML
    private void handleNewCategory() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nova Categoria");
        dialog.setHeaderText("Cadastrar Nova Categoria");
        dialog.setContentText("Nome da categoria:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            String trimmed = name.trim();
            if (!trimmed.isEmpty()) {
                try {
                    categoriaController.salvarCategoria(trimmed);
                    loadCategories();

                    for (CategoriaFinanceira c : cbCategory.getItems()) {
                        if (c.getNome().equalsIgnoreCase(trimmed)) {
                            cbCategory.setValue(c);
                            break;
                        }
                    }
                } catch (IllegalArgumentException e) {
                    showFeedback("Erro ao Salvar", e.getMessage(), Alert.AlertType.WARNING);
                }
            }
        });
    }

    private void showFeedback(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
