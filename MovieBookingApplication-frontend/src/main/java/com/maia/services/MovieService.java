package com.maia.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maia.models.Movie;
import com.maia.models.Token;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class MovieService {


    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public MovieService() {
        mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
    }

    public List<Movie> getAllMovies () throws IOException, InterruptedException {

        Token.checkAndRefreshToken();
        String token = Token.getToken();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/movies/getallmovies"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return mapper.readerForListOf(Movie.class).readValue(response.body());
        } else {
            throw new RuntimeException("no movies found : " + response.statusCode());
        }
    }


    public String createMovie (Movie movie) throws IOException, InterruptedException {
        String jsonBody = mapper.writeValueAsString(movie);
        Token.checkAndRefreshToken();
        String token = Token.getToken();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/movies/addmovie"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200||response.statusCode() == 201) {
            return "movie created successfully";
        } else if (response.statusCode() == 403) {
            throw new RuntimeException("forbidden access, you do not have the permission needed" + response.body());
        } else {
            throw new RuntimeException("movie creation failed : " + response.statusCode()+ " - " + response.body());
        }

    }


    public String updateMovie (Movie movie) throws IOException, InterruptedException {
        String jsonBody = mapper.writeValueAsString(movie);
        Token.checkAndRefreshToken();
        String token = Token.getToken();
        HttpRequest request = HttpRequest.newBuilder()

                .uri(URI.create("http://localhost:8080/api/movies/updatemovie/" + movie.getId().toString()))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200||response.statusCode() == 201) {
            return "movie updated successfully";
        } else if (response.statusCode() == 403) {
            throw new RuntimeException("forbidden access, you do not have the permission needed" + response.body());
        } else {
            throw new RuntimeException("movie update failed : " + response.statusCode()+ " - " + response.body());
        }
    }



    public String deletePost (Movie movie) throws IOException, InterruptedException {
        Token.checkAndRefreshToken();
        String token = Token.getToken();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/movies/deletemovie/"+movie.getId().toString()))
                .header("Authorization", "Bearer " + token)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200||response.statusCode() == 201) {
            return "movie deleted successfully";
        } else if (response.statusCode() == 403) {
            throw new RuntimeException("forbidden access, you do not have the permission needed" + response.body());
        } else {
            throw new RuntimeException("movie deletion failed : " + response.statusCode()+ " - " + response.body());
        }
    }
    }
