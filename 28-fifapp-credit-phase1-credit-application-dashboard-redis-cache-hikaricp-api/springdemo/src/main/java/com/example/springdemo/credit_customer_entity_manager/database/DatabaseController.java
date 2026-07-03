package com.example.springdemo.credit_customer_entity_manager.database;

import com.example.springdemo.credit_customer_entity_manager.database.DatabaseStatus;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/database")
public class DatabaseController {

    private static final Logger log = LoggerFactory.getLogger(DatabaseController.class);

    private final JdbcTemplate jdbcTemplate;
    private final HikariDataSource hikariDataSource;

    public DatabaseController(JdbcTemplate jdbcTemplate, HikariDataSource hikariDataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.hikariDataSource = hikariDataSource;
    }

    @GetMapping("/ping")
    public DatabaseStatus pingDatabase() {
        String databaseName = jdbcTemplate.queryForObject("select current_database()", String.class);

        return new DatabaseStatus(
                true,
                databaseName,
                "PostgreSQL connection is OK"
        );
    }

    @GetMapping("/schema")
    public DatabaseSchemaStatus getDatabaseSchema() {
        List<String> tables = jdbcTemplate.queryForList(
                """
                select table_name
                from information_schema.tables
                where table_schema = 'flyway_training'
                order by table_name
                """,
                String.class
        );

        return new DatabaseSchemaStatus(
                "flyway_training",
                tables,
                "Flyway migration has prepared the database schema"
        );
    }

    @GetMapping("/pool")
    public DatabasePoolStatus getPoolStatus() {
        DatabasePoolStatus status = poolStatus();
        log.info("[HIKARI POOL STATUS] pool={} total={} active={} idle={} pending={}",
                status.poolName(),
                status.totalConnections(),
                status.activeConnections(),
                status.idleConnections(),
                status.threadsAwaitingConnection());
        return status;
    }

    /**
     * Training-only endpoint. It checks out one JDBC connection and keeps it for a bounded
     * duration, so concurrent requests make HikariCP metrics observable.
     */
    @GetMapping("/pool/hold")
    public DatabaseConnectionHoldResponse holdConnection(
            @RequestParam(defaultValue = "5000") long durationMs) {
        if (durationMs < 1 || durationMs > 20_000) {
            throw new IllegalArgumentException("durationMs must be between 1 and 20000");
        }

        try (Connection connection = hikariDataSource.getConnection()) {
            String connectionDescription = connection.toString();
            DatabasePoolStatus statusWhileHeld = poolStatus();
            log.info("[HIKARI CONNECTION ACQUIRED] pool={} connection={} active={} idle={} total={}",
                    statusWhileHeld.poolName(),
                    connectionDescription,
                    statusWhileHeld.activeConnections(),
                    statusWhileHeld.idleConnections(),
                    statusWhileHeld.totalConnections());

            try (var statement = connection.createStatement();
                 var resultSet = statement.executeQuery("select 1")) {
                resultSet.next();
            }
            Thread.sleep(durationMs);

            return new DatabaseConnectionHoldResponse(
                    "A JDBC connection was borrowed from HikariCP for the requested duration and was returned to the pool",
                    durationMs,
                    connectionDescription,
                    statusWhileHeld
            );
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to borrow a JDBC connection from HikariCP", exception);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Connection hold was interrupted", exception);
        } finally {
            DatabasePoolStatus status = poolStatus();
            log.info("[HIKARI CONNECTION RETURNED] pool={} total={} active={} idle={} pending={}",
                    status.poolName(),
                    status.totalConnections(),
                    status.activeConnections(),
                    status.idleConnections(),
                    status.threadsAwaitingConnection());
        }
    }

    private DatabasePoolStatus poolStatus() {
        HikariPoolMXBean poolMxBean = hikariDataSource.getHikariPoolMXBean();
        if (poolMxBean == null) {
            return new DatabasePoolStatus(
                    hikariDataSource.getPoolName(), 0, 0, 0, 0,
                    hikariDataSource.getMaximumPoolSize(),
                    hikariDataSource.getMinimumIdle(),
                    hikariDataSource.getConnectionTimeout(),
                    "not-started"
            );
        }

        return new DatabasePoolStatus(
                hikariDataSource.getPoolName(),
                poolMxBean.getTotalConnections(),
                poolMxBean.getActiveConnections(),
                poolMxBean.getIdleConnections(),
                poolMxBean.getThreadsAwaitingConnection(),
                hikariDataSource.getMaximumPoolSize(),
                hikariDataSource.getMinimumIdle(),
                hikariDataSource.getConnectionTimeout(),
                "running"
        );
    }
}
