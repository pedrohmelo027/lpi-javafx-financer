package com.gestaofinanceira.view;

import com.gestaofinanceira.model.*;
import com.gestaofinanceira.controller.TransacaoController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class TransacaoView {
    private final TransacaoController transacaoController;
    private final Runnable loadDataCallback;

    private final TableView<Transacao> tvTransactions;
    private final TableColumn<Transacao, LocalDate> colDate;
    private final TableColumn<Transacao, CategoriaFinanceira> colCategory;
    private final TableColumn<Transacao, String> colType;
    private final TableColumn<Transacao, Double> colAmount;
    private final TableColumn<Transacao, String> colDescription;
    private final TableColumn<Transacao, Void> colActions;

    private final Label lblFormTitle;
    private final ToggleGroup tgType;
    private final RadioButton rbIncome;
    private final RadioButton rbExpense;
    private final RadioButton rbInvestment;
    private final TextField txtAmount;
    private final DatePicker dpDate;
    private final ComboBox<CategoriaFinanceira> cbCategory;
    private final TextField txtDescription;
    private final Button btnSave;
    private final Button btnCancel;

    private Transacao editingTransacao = null;

    public TransacaoView(
            Runnable loadDataCallback,
            TableView<Transacao> tvTransactions,
            TableColumn<Transacao, LocalDate> colDate,
            TableColumn<Transacao, CategoriaFinanceira> colCategory,
            TableColumn<Transacao, String> colType,
            TableColumn<Transacao, Double> colAmount,
            TableColumn<Transacao, String> colDescription,
            TableColumn<Transacao, Void> colActions,
            Label lblFormTitle,
            ToggleGroup tgType,
            RadioButton rbIncome,
            RadioButton rbExpense,
            RadioButton rbInvestment,
            TextField txtAmount,
            DatePicker dpDate,
            ComboBox<CategoriaFinanceira> cbCategory,
            TextField txtDescription,
            Button btnSave,
            Button btnCancel
    ) {
        this.transacaoController = new TransacaoController();
        this.loadDataCallback = loadDataCallback;
        this.tvTransactions = tvTransactions;
        this.colDate = colDate;
        this.colCategory = colCategory;
        this.colType = colType;
        this.colAmount = colAmount;
        this.colDescription = colDescription;
        this.colActions = colActions;
        this.lblFormTitle = lblFormTitle;
        this.tgType = tgType;
        this.rbIncome = rbIncome;
        this.rbExpense = rbExpense;
        this.rbInvestment = rbInvestment;
        this.txtAmount = txtAmount;
        this.dpDate = dpDate;
        this.cbCategory = cbCategory;
        this.txtDescription = txtDescription;
        this.btnSave = btnSave;
        this.btnCancel = btnCancel;

        setupTableColumns();
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
            protected void updateItem(CategoriaFinanceira item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNome());
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
                    Transacao t = getTableView().getItems().get(getIndex());
                    setText(String.format("R$ %,.2f", item));
                    getStyleClass().removeAll("row-income", "row-expense", "row-investment");
                    if ("ENTRADA".equals(t.getTipo())) {
                        getStyleClass().add("row-income");
                    } else if ("SAÍDA".equals(t.getTipo())) {
                        getStyleClass().add("row-expense");
                    } else if ("INVESTIMENTO".equals(t.getTipo())) {
                        getStyleClass().add("row-investment");
                    }
                }
            }
        });

        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));

        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit = new Button("Editar");
            private final Button btnDelete = new Button("Excluir");
            private final HBox container = new HBox(8, btnEdit, btnDelete);

            {
                btnEdit.getStyleClass().add("btn-action-edit");
                btnDelete.getStyleClass().add("btn-action-delete");
                container.setAlignment(javafx.geometry.Pos.CENTER);

                btnEdit.setOnAction(event -> {
                    Transacao t = getTableView().getItems().get(getIndex());
                    initiateEdit(t);
                });
                btnDelete.setOnAction(event -> {
                    Transacao t = getTableView().getItems().get(getIndex());
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

    public void registrarReceita() {
        resetForm();
        rbIncome.setSelected(true);
        txtAmount.requestFocus();
    }

    public void registrarDespesa() {
        resetForm();
        rbExpense.setSelected(true);
        txtAmount.requestFocus();
    }

    public void listarTransacoes(int month, int year) {
        List<Transacao> transactions = transacaoController.listarTransacoes(month, year);
        ObservableList<Transacao> observableList = FXCollections.observableArrayList(transactions);
        tvTransactions.setItems(observableList);
    }

    public void handleSave() {
        try {
            String amountStr = txtAmount.getText().trim();
            if (amountStr.isEmpty()) {
                throw new IllegalArgumentException("O valor é obrigatório.");
            }

            amountStr = amountStr.replace(",", ".");
            double amount = Double.parseDouble(amountStr);

            String tipo = rbIncome.isSelected() ? "ENTRADA" : (rbExpense.isSelected() ? "SAÍDA" : "INVESTIMENTO");
            CategoriaFinanceira category = cbCategory.getValue();
            LocalDate date = dpDate.getValue();
            String description = txtDescription.getText().trim();

            Transacao transacao;
            if (editingTransacao == null) {
                if ("ENTRADA".equals(tipo)) {
                    transacao = new Receita();
                } else if ("SAÍDA".equals(tipo)) {
                    transacao = new Despesa();
                } else {
                    transacao = new Investimento();
                }
            } else {
                transacao = editingTransacao;
            }

            transacao.setValor(amount);
            transacao.setTipo(tipo);
            transacao.setCategoria(category);
            transacao.setDataTransacao(LocalDateTime.of(date, LocalTime.MIDNIGHT));
            transacao.setDescricao(description);

            transacaoController.salvarTransacao(transacao);

            resetForm();
            loadDataCallback.run();
            showFeedback("Sucesso", "Movimentação salva com sucesso!", Alert.AlertType.INFORMATION);

        } catch (NumberFormatException e) {
            showFeedback("Erro de Validação", "Insira um valor numérico válido.", Alert.AlertType.WARNING);
        } catch (IllegalArgumentException e) {
            showFeedback("Erro de Validação", e.getMessage(), Alert.AlertType.WARNING);
        }
    }

    public void handleCancel() {
        resetForm();
    }

    public void resetForm() {
        editingTransacao = null;
        lblFormTitle.setText("Nova Movimentação");

        txtAmount.clear();
        rbIncome.setSelected(true);
        cbCategory.setValue(null);
        dpDate.setValue(LocalDate.now());
        txtDescription.clear();

        btnCancel.setVisible(false);
        btnSave.setText("Salvar Movimentação");
    }

    private void initiateEdit(Transacao t) {
        editingTransacao = t;
        lblFormTitle.setText("Editar Movimentação");

        txtAmount.setText(String.format(Locale.US, "%.2f", t.getValor()));
        rbIncome.setSelected("ENTRADA".equals(t.getTipo()));
        rbExpense.setSelected("SAÍDA".equals(t.getTipo()));
        rbInvestment.setSelected("INVESTIMENTO".equals(t.getTipo()));
        cbCategory.setValue(t.getCategoria());
        dpDate.setValue(t.getDate());
        txtDescription.setText(t.getDescricao());

        btnCancel.setVisible(true);
        btnSave.setText("Salvar Alterações");
    }

    private void confirmDelete(Transacao t) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Exclusão");
        alert.setHeaderText("Excluir movimentação?");
        alert.setContentText(String.format("Deseja realmente excluir a transação '%s' no valor de R$ %,.2f?", 
                t.getDescricao() == null || t.getDescricao().isEmpty() ? t.getCategoria().getNome() : t.getDescricao(),
                t.getValor()));

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            transacaoController.deletarTransacao(t.getId());
            loadDataCallback.run();
            showFeedback("Sucesso", "Movimentação excluída com sucesso!", Alert.AlertType.INFORMATION);
            if (editingTransacao != null && editingTransacao.getId().equals(t.getId())) {
                resetForm();
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
