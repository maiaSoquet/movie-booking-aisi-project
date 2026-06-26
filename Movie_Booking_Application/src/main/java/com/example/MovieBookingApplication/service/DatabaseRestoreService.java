package com.example.MovieBookingApplication.service;

import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DatabaseRestoreService {

    private final String backupDirPath = "backups";
    private final String mysqlPath = "C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysql.exe";

    public List<String> getAvailableBackups() {
        List<String> backupFiles = new ArrayList<>();
        File folder = new File(backupDirPath);
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    backupFiles.add(file.getName());
                }
            }
        }
        return backupFiles;
    }

    public void executeRestore(String fileName) {
        if (fileName == null || fileName.contains("..") || !fileName.endsWith(".sql")) {
            throw new IllegalArgumentException("Invalid backup file name.");
        }

        File backupFile = new File(backupDirPath + File.separator + fileName);
        if (!backupFile.exists()) {
            throw new RuntimeException("Backup file not found: " + fileName);
        }

        List<String> command = new ArrayList<>();
        command.add(mysqlPath);
        command.add("-u" + "root");

        String dbPassword = "password";
        command.add("-p" + dbPassword);
        command.add("moviedb");

        try {
            ProcessBuilder pb = new ProcessBuilder(command);

            pb.redirectInput(backupFile);
            pb.redirectErrorStream(true);

            Process process = pb.start();
            int processComplete = process.waitFor();

            if (processComplete != 0) {
                throw new RuntimeException("mysql restore failed with code : " + processComplete);
            }
            System.out.println("Database successfully restored from: " + fileName);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException("Error during database restoration", e);
        }
    }
}