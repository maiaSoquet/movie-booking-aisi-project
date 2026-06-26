package com.maia.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maia.Main;

import com.maia.models.Movie;
import com.maia.models.Token;
import com.maia.services.MovieService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;

public class PutMovieController {


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

    private Movie movieToUpdate;

    public void initialize(){

    }

    public void sendPutRequest(){
        try {
            Movie newMovie = new Movie();
            newMovie.setId(movieToUpdate.getId());
            newMovie.setName(name.getText());
            newMovie.setDescription(description.getText());
            newMovie.setGenre(genre.getText());
            newMovie.setDuration(Integer.parseInt(duration.getText()));
            newMovie.setReleaseDate(LocalDate.parse(releaseDate.getText()));
            newMovie.setLanguage(language.getText());

            MovieService movieService = new MovieService();


            movieService.updateMovie(newMovie);


            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success!");
            alert.setHeaderText("movie successfully updated!");
            alert.setContentText(newMovie.getName() + " successfully updated!");

            alert.showAndWait();

            MenuController controller = new MenuController();
            controller.openGetAllMoviesScreen();
        }
        catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("The movie could not be updated");
            alert.setContentText("Details: " + e.getMessage());
            alert.showAndWait();
        }
    }

    public void setMovie(Movie movie){
        this.movieToUpdate = movie;
    }

    public void fillMovieFields(){
        name.setText(movieToUpdate.getName());
        description.setText(movieToUpdate.getDescription());
        genre.setText(movieToUpdate.getGenre());
        duration.setText(String.valueOf(movieToUpdate.getDuration()));
        releaseDate.setText(String.valueOf(movieToUpdate.getReleaseDate()));
        language.setText(movieToUpdate.getLanguage());
    }


}



