package com.maia.controllers;

import com.maia.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;

public class MenuController {

    public void openLoginScreen() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/views/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 600);
        Main.getCentralStage().setTitle("LOG OUT");
        Main.getCentralStage().setScene(scene);
        Main.getCentralStage().show();
    }

    public void openGetAllMoviesScreen() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/views/get-movies.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 600);
        Main.getCentralStage().setTitle("GET MOVIES");
        Main.getCentralStage().setScene(scene);
        Main.getCentralStage().show();
    }


    public void openPostMovieScreen() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/views/add-movie.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 600);
        Main.getCentralStage().setTitle("ADD MOVIE");
        Main.getCentralStage().setScene(scene);
        Main.getCentralStage().show();
    }

    public void openDatabaseScreen() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/views/database.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 600);
        Main.getCentralStage().setTitle("DATABASE");
        Main.getCentralStage().setScene(scene);
        Main.getCentralStage().show();
    }



}
