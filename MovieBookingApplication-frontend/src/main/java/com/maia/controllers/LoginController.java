package com.maia.controllers;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import com.maia.models.Login;
import com.maia.models.Token;
import com.maia.services.AuthService;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;


    private final AuthService authService = new AuthService();

    public LoginController() {
    }

    @FXML
    void initialize() {
    }

    @FXML
    void handleValidateAction(final ActionEvent event) {
        Login loginData = new Login(usernameField.getText(), passwordField.getText());
        try {
            authService.login(loginData);

            statusLabel.setText("connected successfully");
            statusLabel.setStyle("-fx-text-fill: green;");

            try {
                MenuController menu = new MenuController();
                menu.openGetAllMoviesScreen();
            } catch (IOException e) {
                e.printStackTrace();
                statusLabel.setText("Error loading the next screen!");
            }

        } catch(Exception e) {
            e.printStackTrace();
            statusLabel.setText("wrong username or password");
            statusLabel.setStyle("-fx-text-fill: red;");
        }
    }
}


