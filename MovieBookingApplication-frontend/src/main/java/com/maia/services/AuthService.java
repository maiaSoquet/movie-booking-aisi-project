package com.maia.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maia.models.Login;
import com.maia.models.User;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AuthService {

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();


    public String registernormaluser (User userData) throws IOException, InterruptedException {
        String jsonBody = mapper.writeValueAsString(userData);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/auth/registernormaluser"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200 || response.statusCode() == 201) {
            return ("new user created successfully");
        } else {
            throw new RuntimeException("Authentication failed: " + response.statusCode());
        }
    }




    public void login(Login loginData) throws IOException, InterruptedException {
        String jsonBody = mapper.writeValueAsString(loginData);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JsonNode rootNode = mapper.readTree(response.body());

            String jwtToken = rootNode.get("jwtToken").asText();
            String refreshToken = rootNode.get("refreshToken").asText();

            com.maia.models.Token.setToken(jwtToken);
            com.maia.models.Token.setRefreshToken(refreshToken);

        } else {
            throw new RuntimeException("Authentication failed: " + response.statusCode());
        }
    }
}
