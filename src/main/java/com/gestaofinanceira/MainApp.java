package com.gestaofinanceira;

import com.gestaofinanceira.util.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Inicializa a conexão e as tabelas do banco de dados SQLite local
            DatabaseConnection.initializeDatabase();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gestaofinanceira/main.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Financer - Controle Financeiro Pessoal");
            primaryStage.setMinWidth(1100);
            primaryStage.setMinHeight(700);
            primaryStage.show();
        } catch (IOException e) {
            System.err.println("Erro ao carregar o arquivo FXML da interface: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
