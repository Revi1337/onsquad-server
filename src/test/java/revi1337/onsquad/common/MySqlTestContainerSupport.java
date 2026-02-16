package revi1337.onsquad.common;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

public interface MySqlTestContainerSupport {

    MySQLContainer<?> MYSQL = createAndStartMySql();

    private static MySQLContainer<?> createAndStartMySql() {
        MySQLContainer<?> container = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
                .withUsername("revi1337")
                .withPassword("1337")
                .withDatabaseName("onsquad")
                .withCommand("--lower_case_table_names=1");
        container.start();
        return container;
    }

    @DynamicPropertySource
    static void configureMySql(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> MYSQL.getJdbcUrl() + "?rewriteBatchedStatements=true");
        registry.add("spring.datasource.username", MYSQL::getUsername);
        registry.add("spring.datasource.password", MYSQL::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
    }
}
