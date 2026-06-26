package com.example.MovieBookingApplication.controller;

import com.example.MovieBookingApplication.service.DatabaseRestoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/restore")
@PreAuthorize("hasRole('ADMIN')")
public class DatabaseRestoreController {

    @Autowired
    private DatabaseRestoreService restoreService;

    @GetMapping("/files")
    public ResponseEntity<List<String>> listBackupFiles() {
        return ResponseEntity.ok(restoreService.getAvailableBackups());
    }

    @PostMapping("/load")
    public ResponseEntity<?> triggerRestore(@RequestParam String fileName) {
        restoreService.executeRestore(fileName);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Database successfully restored from file: " + fileName);

        return ResponseEntity.ok(response);
    }
}