package com.game.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class DatabaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);
    private static HikariDataSource dataSource;

    static {
        try {
            logger.info("Подключение к бд");

            HikariConfig config = new HikariConfig();


            String dbHost = Optional.ofNullable(System.getenv("DB_HOST")).orElse("postgres");
            String dbPort = Optional.ofNullable(System.getenv("DB_PORT")).orElse("5432");
            String dbName = Optional.ofNullable(System.getenv("DB_NAME")).orElse("game");
            String dbUser = Optional.ofNullable(System.getenv("DB_USER")).orElse("postgres");
            String dbPassword = Optional.ofNullable(System.getenv("DB_PASSWORD")).orElse("root");

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