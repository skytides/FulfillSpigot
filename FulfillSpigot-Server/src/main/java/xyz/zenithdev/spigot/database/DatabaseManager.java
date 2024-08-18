package xyz.zenithdev.spigot.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;

import java.io.File;

public class DatabaseManager {

    @Getter
    private HikariDataSource dataSource;

    public void init(File configFile) {
        DatabaseConfig.init(configFile);
        DatabaseConfig dbConfig = DatabaseConfig.get();

        if (dbConfig.jdbcUrl == null || dbConfig.jdbcUrl.isEmpty()) {
            //handles the case where jdbc url is not provided
            throw new IllegalArgumentException("jdbcUrl is required.");
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dbConfig.jdbcUrl);
        config.setUsername(dbConfig.username);
        config.setPassword(dbConfig.password);
        config.setMaximumPoolSize(dbConfig.maximumPoolSize);
        config.setMinimumIdle(dbConfig.minimumIdle);
        config.setIdleTimeout(dbConfig.idleTimeout);
        config.setMaxLifetime(dbConfig.maxLifetime);
        config.setConnectionTimeout(dbConfig.connectionTimeout);
        config.setAutoCommit(dbConfig.autoCommit);
        config.setLeakDetectionThreshold(dbConfig.leakDetectionThreshold);

        dataSource = new HikariDataSource(config);
    }

    public void shutdown() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}
