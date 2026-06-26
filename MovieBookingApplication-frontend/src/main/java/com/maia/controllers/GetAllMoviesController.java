package com.maia.controllers;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maia.Main;
import com.maia.models.Login;
import com.maia.models.Movie;
import com.maia.models.Token;
import com.maia.services.AuthService;
import com.maia.services.BackupService;
import com.maia.services.MovieService;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class GetAllMoviesController {
    @FXML
    private Label welcomeText;

    @FXML
    private TableView<Movie> movieTable;

    @FXML
    private TableColumn<Movie, Long> idColumn;

    @FXML
    private TableColumn<Movie, String> nameColumn;

    @FXML
    private TableColumn<Movie, String> descriptionColumn;

    @FXML
    private TableColumn<Movie, String> genreColumn;

    @FXML
    private TableColumn <Movie, Integer> durationColumn;

    @FXML
    private TableColumn<Movie, LocalDate> releaseDateColumn;

    @FXML
    private TableColumn<Movie, String> languageColumn;



    @FXML
    private void onHelloButtonClick(ActionEvent event) {
        welcomeText.setText("Welcome to JavaFX Application!");

        try {

            MovieService movieService = new MovieService();
            List<Movie> movies = movieService.getAllMovies();

            ObservableList<Movie> moviesList =
                    FXCollections.observableArrayList(movies);

            movieTable.setItems(moviesList);

        } catch (Exception e) {
            e.printStackTrace();
            welcomeText.setText("Error fetching posts data!");
        }
    }




    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        genreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));
        releaseDateColumn.setCellValueFactory(new PropertyValueFactory<>("releaseDate"));
        languageColumn.setCellValueFactory(new PropertyValueFactory<>("language"));



        movieTable.setRowFactory(
                new Callback<TableView<Movie>, TableRow<Movie>>() {
                    @Override
                    public TableRow<Movie> call(TableView<Movie> tableView) {
                        final TableRow<Movie> row = new TableRow<>();
                        final ContextMenu rowMenu = new ContextMenu();
                        MenuItem editItem = new MenuItem("EDIT");
                        editItem.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {

                                FXMLLoader loader = new FXMLLoader();
                                loader.setLocation(Main.class.getResource("/views/edit-movie.fxml"));

                                Movie movie = row.getItem();

                                try {
                                    Parent parent = (AnchorPane) loader.load();
                                    Scene scene = new Scene(parent);
                                    Main.getCentralStage().setScene(scene);
                                    PutMovieController controller = loader.getController();
                                    controller.setMovie(movie);
                                    Main.getCentralStage().show();
                                    controller.fillMovieFields();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        });



                        MenuItem removeItem = new MenuItem("DELETE");
                        removeItem.setOnAction(new EventHandler<ActionEvent>() {


                            @Override
                            public void handle(ActionEvent event) {

                                Movie movie = row.getItem();

                                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                                confirm.setTitle("delete confirmation");
                                confirm.setHeaderText("you are about to delete: " + movie.getName());
                                confirm.setContentText("are you sure you want to delete this record?");

                                confirm.showAndWait().ifPresent(response -> {
                                    if (response == ButtonType.OK) {
                                        try {

                                            MovieService movieService = new MovieService();


                                            movieService.deletePost(movie);
                                            movieTable.getItems().remove(movie);
                                          welcomeText.setText("movie deleted successfully!");

                                        } catch (Exception e) {
                                            Alert alert = new Alert(Alert.AlertType.ERROR);
                                            alert.setTitle("error while deleting movie");
                                            alert.setContentText(e.getMessage());
                                            alert.showAndWait();
                                        }
                                    }
                                });
                            }
                        });
                        rowMenu.getItems().addAll(editItem, removeItem);


                        row.contextMenuProperty().bind(
                                Bindings.when(row.emptyProperty())
                                        .then((ContextMenu) null)
                                        .otherwise(rowMenu));
                        return row;
                    }
                });
    }



}


