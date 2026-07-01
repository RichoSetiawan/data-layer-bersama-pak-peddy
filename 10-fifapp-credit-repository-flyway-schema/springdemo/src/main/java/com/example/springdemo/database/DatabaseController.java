package com.example.springdemo.database;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DatabaseController {
    private final JdbcTemplate jdbcTemplate;

    @GetMapping("/api/database/ping")
    public DatabaseStatus pingDatabase(){
        String databaseName = jdbcTemplate.queryForObject("select current_database()", String.class);

        return new DatabaseStatus(
            true,
            databaseName,
            "PostgreSQL connection is OK"
        );
    }

    @GetMapping("/api/database/schema")
    public DatabaseSchemaStatus getDatabaseSchema(){
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

}
