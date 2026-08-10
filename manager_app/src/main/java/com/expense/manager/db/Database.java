package com.expense.manager.db;

import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

@Component
public class Database {
    private static final String DB_NAME = "expenses_system_db.db";

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + getDatabasePath());
    }

    private static String getDatabasePath() {
        Map<String, String> env = System.getenv();
        if (env.get("CONTAINER") != null && env.get("CONTAINER").equals("DOCKER")) {
            return env.get("DATABASE_URL");
        }
        Path projectRoot = findProjectRoot();
        if (projectRoot != null) {
            return projectRoot.resolve(DB_NAME).toString();
        }

        return Paths.get(DB_NAME).toAbsolutePath().toString();
    }

    private static Path findProjectRoot() {
        Path path = Paths.get("").toAbsolutePath();
        while (path != null) {
            if (Files.exists(path.resolve(DB_NAME))) {
                return path;
            }
            path = path.getParent();
        }
        return null;
    }
}