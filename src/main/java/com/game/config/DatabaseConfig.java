package com.game.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
public class DatabaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);
    private static HikariDataSource dataSource;

    static {
        try {
            logger.info("Подключение к бд");

            HikariConfig config = new HikariConfig();

            config.setJdbcUrl("jdbc:postgresql://localhost:5432/game");
            String dbHost = System.getenv("DB_HOST") != null ?
                    System.getenv("DB_HOST") : "postgres";
            String dbPort = System.getenv("DB_PORT") != null ?
                    System.getenv("DB_PORT") : "5432";
            String dbName = System.getenv("DB_NAME") != null ?
                    System.getenv("DB_NAME") : "game";
            String dbUser = System.getenv("DB_USER") != null ?
                    System.getenv("DB_USER") : "postgres";
            String dbPassword = System.getenv("DB_PASSWORD") != null ?
                    System.getenv("DB_PASSWORD") : "root";

            String jdbcUrl = String.format(
                    "jdbc:postgresql://%s:%s/%s",
                    dbHost, dbPort, dbName
            );
            config.setJdbcUrl(jdbcUrl);
            config.setUsername(dbUser);
            config.setPassword(dbPassword);

            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            dataSource = new HikariDataSource(config);

            logger.info("Бд подключена");
        } catch (Exception e) {
            logger.error("Бд не подключена", e);
            throw new RuntimeException("DB FAILED", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            logger.info("Обрыв соединения с бд");
            dataSource.close();
        }
    }
}