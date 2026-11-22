package ua.edu.ukma.unischeduler.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;

/**
 * Automatically applies SQL migrations on application startup without requiring manual psql execution.
 */
@Slf4j
@Component
@Order(1)
@NullMarked
@RequiredArgsConstructor
public class DatabaseMigrationRunner implements ApplicationRunner {
    private final DataSource dataSource;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Checking and applying automatic database migrations...");
        Resource migrationScript = new ClassPathResource("db/migration/2026_schedule_import.sql");
        if (!migrationScript.exists()) {
            log.debug("No migration script found at db/migration/2026_schedule_import.sql");
            return;
        }

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = migrationScript.getContentAsString(StandardCharsets.UTF_8);
            stmt.execute(sql);
            log.info("Automatic database migrations applied successfully.");
        } catch (Exception ex) {
            log.warn("Migration notice (schema may already be up to date): {}", ex.getMessage());
        }
    }
}
