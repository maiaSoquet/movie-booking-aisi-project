package com.maia.models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Token {

    private static String token;
    private static String refreshToken;
    private static LocalDateTime creationTime;

    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        Token.token = token;
        Token.creationTime = LocalDateTime.now();
    }

    public static String getRefreshToken() {
        return refreshToken;
    }

    public static void setRefreshToken(String refreshToken) {
        Token.refreshToken = refreshToken;
    }

    public static boolean isExpired() {
        if (creationTime == null) {
            return true;
        }
        return LocalDateTime.now().isAfter(creationTime.plusMinutes(59));
    }

    public static void checkAndRefreshToken() {
        if (!isExpired()) {
            return;
        }

        try {
            Map<String, String> bodyMap = new HashMap<>();
            bodyMap.put("refreshToken", getRefreshToken());
            String requestBody = mapper.writeValueAsString(bodyMap);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/auth/refreshtoken"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode jsonNode = mapper.readTree(response.body());
                String newAccessToken = jsonNode.get("accessToken").asText();
                setToken(newAccessToken);
            } else {
                throw new RuntimeException("Your session has expired, please log in again");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("refreshing token has failed", e);
        }
    }
}