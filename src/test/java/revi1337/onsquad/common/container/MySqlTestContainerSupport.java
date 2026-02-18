package revi1337.onsquad.common.container;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.MySQLContainer;

@ContextConfiguration(initializers = MySqlTestContainerSupport.MySqlInitializer.class)
public abstract class MySqlTestContainerSupport {

    public static class MySqlInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            MySQLContainer<?> mysql = TestContainerRegistry.getMysql();
            Map<String, String> properties = new HashMap<>();
            properties.put("spring.datasource.url", mysql.getJdbcUrl() + "?rewriteBatchedStatements=true");
            properties.put("spring.datasource.username", mysql.getUsername());
            properties.put("spring.datasource.password", mysql.getPassword());
            properties.put("spring.datasource.driver-class-name", "com.mysql.cj.jdbc.Driver");
            TestPropertyValues.of(properties).applyTo(applicationContext);
        }
    }
}

//package revi1337.onsquad.common.container;
//
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.testcontainers.containers.MySQLContainer;
//import org.testcontainers.utility.DockerImageName;
//
//public interface MySqlTestContainerSupport {
//
//    MySQLContainer<?> MYSQL = MySqlContainerHolder.getInstance();
//
//    @DynamicPropertySource
//    static void configureMySql(DynamicPropertyRegistry registry) {
//        registry.add("spring.datasource.url", () -> MYSQL.getJdbcUrl() + "?rewriteBatchedStatements=true");
//        registry.add("spring.datasource.username", MYSQL::getUsername);
//        registry.add("spring.datasource.password", MYSQL::getPassword);
//        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
//    }
//
//    class MySqlContainerHolder {
//
//        private static final MySQLContainer<?> INSTANCE = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
//                .withCommand("--lower_case_table_names=1");
//
//        static {
//            INSTANCE.start();
//        }
//
//        public static MySQLContainer<?> getInstance() {
//            return INSTANCE;
//        }
//    }
//}
