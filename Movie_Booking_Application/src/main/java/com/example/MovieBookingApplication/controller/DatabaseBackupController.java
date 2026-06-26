package com.example.MovieBookingApplication.controller;

import com.example.MovieBookingApplication.service.DatabaseBackupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/backup")
public class DatabaseBackupController {

    @Autowired
    private DatabaseBackupService backupService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<?> triggerBackup() {
        String fileName = backupService.executeBackup();

        Map<String, String> response = new HashMap<>();
        response.put("message", "Database backup created successfully!");
        response.put("fileName", fileName);

        return ResponseEntity.ok(response);
    }
}