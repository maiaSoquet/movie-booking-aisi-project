package com.maia.controllers;



import com.maia.models.Movie;
import com.maia.models.Token;
import com.maia.services.MovieService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class PostMovieController {

    @FXML
    private TextField name;
    @FXML
    private TextField description;
    @FXML
    private TextField genre;
    @FXML
    private TextField duration;
    @FXML
    private TextField releaseDate;
    @FXML
    private TextField language;

    @FXML
    private Label statusLabel;

    public void sendMovieRequest() {
        statusLabel.setText("");

        Movie newMovie = new Movie();
        newMovie.setName(name.getText());
        newMovie.setDescription(description.getText());
        newMovie.setGenre(genre.getText());
        try {
            newMovie.setDuration(Integer.parseInt(duration.getText()));
        } catch (NumberFormatException e) {
            statusLabel.setText("the duration must be an integer");
        }
        try {
            newMovie.setReleaseDate(LocalDate.parse(releaseDate.getText()));
        } catch (Exception e) {
            statusLabel.setText("the date must be a valid date (yyyy-mm-dd)");
        }
        newMovie.setLanguage(language.getText());

        MovieService movieService = new MovieService();
        try {

            String MovieArrayResponse = movieService.createMovie(newMovie);
            statusLabel.setText("movie created successfully");
            statusLabel.setStyle("-fx-text-fill: green;");

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("error while creating movie" );
            alert.setContentText(e.getMessage());

            alert.showAndWait();
        }


    }

}


