package revi1337.onsquad.auth.verification.infrastructure.persistence.initializer;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Profile({"local", "default"})
@ConditionalOnExpression("#{'${spring.sql.init.mode:never}'.equals('always') and '${spring.jpa.hibernate.ddl-auto:none}'.equals('create')}")
@Component
public class VerificationCodeSchemaInitializer implements CommandLineRunner {

    private static final String SCHEMA_INIT_PATH = "db/mysql/verification_code_ddl.sql";
    private final DataSource dataSource;

    public VerificationCodeSchemaInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) {
        try (Connection connection = dataSource.getConnection()) {
            ClassPathResource resource = new ClassPathResource(SCHEMA_INIT_PATH);
            log.info("Starting to initializing Verification Code Table with {}", resource.getFilename());
            ScriptUtils.executeSqlScript(connection, resource);
        } catch (SQLException e) {
            log.error("Failed to initialize Verification Code Table");
        }
    }
}
