package com.maia.services; // Adapte à ton dossier exact de services

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maia.models.Token;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class BackupService {

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public void triggerDatabaseBackup() throws Exception {
        Token.checkAndRefreshToken();

        String token = Token.getToken();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/backup/create"))
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
        } else if (response.statusCode() == 403) {
            throw new RuntimeException("Access Denied: you do not have the permission needed.");
        } else {
            throw new RuntimeException("Backup failed with status code: " + response.statusCode());
        }
    }

    public List<String> getBackupFiles() throws Exception {
        Token.checkAndRefreshToken();
        String token = Token.getToken();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/restore/files"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return mapper.readValue(response.body(), mapper.getTypeFactory().constructCollectionType(List.class, String.class));
        } else {
            throw new RuntimeException("Failed to fetch backup files.");
        }
    }

    public void triggerRestore(String fileName) throws Exception {
        Token.checkAndRefreshToken();
        String token = Token.getToken();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/restore/load?fileName=" + fileName))
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            if (response.statusCode() == 403) {
                throw new RuntimeException("Access Denied: Only administrators can restore the database.");
            }
            throw new RuntimeException("Restore failed with status code: " + response.statusCode());
        }
    }
}