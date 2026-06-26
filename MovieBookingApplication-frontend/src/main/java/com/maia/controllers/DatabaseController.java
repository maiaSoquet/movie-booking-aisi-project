package com.maia.controllers;

import com.maia.services.BackupService;
import javafx.event.ActionEvent; // Correction de l'import ici !
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import java.util.List;

public class DatabaseController {

    @FXML
    private ComboBox<String> backupComboBox;

    private final BackupService backupService = new BackupService();

    @FXML
    void initialize() {
        try {
            List<String> files = backupService.getBackupFiles();
            backupComboBox.getItems().addAll(files);
            if (!files.isEmpty()) {
                backupComboBox.getSelectionModel().selectFirst();
            }
        } catch (Exception e) {
            System.out.println("Could not load backup files: " + e.getMessage());
        }
    }

    @FXML
    void handleBackupAction(ActionEvent event) {
        try {
            backupService.triggerDatabaseBackup();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Backup Success");
            alert.setHeaderText(null);
            alert.setContentText("The database backup was successfully created on the server!");
            alert.showAndWait();

            backupComboBox.getItems().clear();
            backupComboBox.getItems().addAll(backupService.getBackupFiles());

        } catch (Exception e) {
            showError("Backup Error", e.getMessage());
        }
    }

    @FXML
    void handleRestoreAction(ActionEvent event) {
        String selectedFile = backupComboBox.getSelectionModel().getSelectedItem();
        if (selectedFile == null) {
            showError("Restore Error", "Please select a backup file first.");
            return;
        }

        try {
            backupService.triggerRestore(selectedFile);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Restore Success");
            alert.setHeaderText(null);
            alert.setContentText("The database has been successfully restored from: " + selectedFile);
            alert.showAndWait();

        } catch (Exception e) {
            showError("Restore Error", e.getMessage());
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}