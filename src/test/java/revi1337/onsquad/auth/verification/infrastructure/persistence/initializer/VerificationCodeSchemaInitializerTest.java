package revi1337.onsquad.auth.verification.infrastructure.persistence.initializer;

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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import revi1337.onsquad.common.container.MySqlTestContainerInitializer;

@TestPropertySource(properties = {
        "spring.sql.init.mode=always",
        "spring.jpa.hibernate.ddl-auto=create"
})
@Import(VerificationCodeSchemaInitializer.class)
@ImportAutoConfiguration({DataSourceAutoConfiguration.class, JdbcTemplateAutoConfiguration.class})
@ContextConfiguration(initializers = MySqlTestContainerInitializer.class)
@ExtendWith(SpringExtension.class)
class VerificationCodeSchemaInitializerTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private VerificationCodeSchemaInitializer schemaInitializer;

    @Test
    @DisplayName("설정 조건(init=always 또는 ddl=create)에 부합하면 빈으로 등록되어야 한다")
    void registeredBean() {
        assertThat(applicationContext.getBean(VerificationCodeSchemaInitializer.class)).isNotNull();
    }

    @Test
    @DisplayName("DDL 스크립트를 실행하여 데이터베이스에 인증 코드 테이블을 생성해야 한다")
    void run() {
        schemaInitializer.run();

        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM verification_code", Integer.class))
                .as("verification_code 테이블이 만들어져 있어야 함.")
                .isEqualTo(0);
    }
}
