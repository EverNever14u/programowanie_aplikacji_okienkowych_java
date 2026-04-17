package konie.zaj3ui3;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Kontroler zarządzający nawigacją między widokami logowania, admina i użytkownika
 */
public class HelloController {
    private Stage stage;
    private Scene scene;
    private Parent root;

    /**
     * Przełącza na widok logowania
     */
    @FXML
    public void switchToLogin(ActionEvent actionEvent) {
        try {
            root = FXMLLoader.load(getClass().getResource("login.fxml"));
            stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Horse Manager - Logowanie");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Nie można załadować ekranu logowania");
        }
    }

    /**
     * Przełącza na widok użytkownika (klienta)
     */
    @FXML
    public void switchToUser(ActionEvent actionEvent) {
        try {
            root = FXMLLoader.load(getClass().getResource("user.fxml"));
            stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Horse Manager - Panel Klienta");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Nie można załadować panelu klienta");
        }
    }

    /**
     * Przełącza na widok administratora
     */
    @FXML
    public void switchToAdmin(ActionEvent actionEvent) {
        try {
            root = FXMLLoader.load(getClass().getResource("admin.fxml"));
            stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Horse Manager - Panel Administratora");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Nie można załadować panelu administratora");
        }
    }

    /**
     * Wyświetla komunikat o błędzie
     */
    private void showError(String message) {
        System.err.println("Błąd: " + message);
        // Możesz dodać tutaj Alert jeśli chcesz
    }
}