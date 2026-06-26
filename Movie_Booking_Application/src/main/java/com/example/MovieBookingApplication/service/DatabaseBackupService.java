package com.example.MovieBookingApplication.service;

import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class DatabaseBackupService {

    public String executeBackup() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String backupFileName = "backup_" + timestamp + ".sql";

        File backupDir = new File("backups");
        if (!backupDir.exists()) {
            backupDir.mkdir();
        }

        String outputPath = backupDir.getAbsolutePath() + File.separator + backupFileName;

        String mysqlDumpPath = "C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysqldump.exe";

        String command = String.format("\"%s\" -u%s -p%s %s -r %s",
                mysqlDumpPath, "root", "password", "moviedb", outputPath);

        try {
            Process process = Runtime.getRuntime().exec(command);
            int processComplete = process.waitFor();

            if (processComplete == 0) {
                return backupFileName;
            } else {
                throw new RuntimeException("mysqldump failed with code : " + processComplete);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException("Error during backup", e);
        }
    }
}