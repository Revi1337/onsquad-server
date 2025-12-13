package revi1337.onsquad.infrastructure.sqlite;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Profile({"local", "default"})
@Component
public class SqliteInitializer implements CommandLineRunner {

    private static final String SQLITE_SCHEMA_INIT_PATH = "db/sqlite/schema-sqlite.sql";
    private final DataSource dataSource;

    public SqliteInitializer(@Qualifier("sqliteDataSource") DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) {
        try (Connection connection = dataSource.getConnection()) {
            ClassPathResource resource = new ClassPathResource(SQLITE_SCHEMA_INIT_PATH);
            log.info("Starting to initializing Sqlite Database with {}", resource.getFilename());
            ScriptUtils.executeSqlScript(connection, resource);
        } catch (SQLException e) {
            log.error("Failed to initialize Sqlite Database");
        }
    }
}
