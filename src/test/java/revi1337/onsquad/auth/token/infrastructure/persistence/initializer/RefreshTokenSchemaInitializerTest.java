package revi1337.onsquad.auth.token.infrastructure.persistence.initializer;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import revi1337.onsquad.common.container.MySqlTestContainerInitializer;

@ActiveProfiles("local")
@TestPropertySource(properties = {
        "spring.sql.init.mode=always",
        "spring.jpa.hibernate.ddl-auto=create"
})
@Import(RefreshTokenSchemaInitializer.class)
@ImportAutoConfiguration({DataSourceAutoConfiguration.class, JdbcTemplateAutoConfiguration.class})
@ContextConfiguration(initializers = MySqlTestContainerInitializer.class)
@ExtendWith(SpringExtension.class)
class RefreshTokenSchemaInitializerTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RefreshTokenSchemaInitializer schemaInitializer;

    @Test
    @DisplayName("설정 조건(init=always 및 ddl=create)에 부합하면 빈으로 등록되어야 한다")
    void registeredBean() {
        RefreshTokenSchemaInitializer bean = applicationContext.getBean(RefreshTokenSchemaInitializer.class);

        assertThat(bean).isNotNull();
    }

    @Test
    @DisplayName("DDL 스크립트를 실행하여 데이터베이스에 리프레시 토큰 테이블을 생성해야 한다")
    void run() {
        jdbcTemplate.execute("DROP TABLE IF EXISTS refresh_token");

        schemaInitializer.run();

        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM refresh_token", Integer.class))
                .as("refresh_token 테이블이 생성되어 있어야 하며 데이터는 0개여야 함")
                .isEqualTo(0);
    }
}
