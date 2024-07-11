package revi1337.onsquad.support;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import revi1337.onsquad.config.TestBlazeJpaConfig;
import revi1337.onsquad.config.TestJpaAuditingConfig;
import revi1337.onsquad.config.TestQueryDslConfig;

@Import({TestJpaAuditingConfig.class, TestQueryDslConfig.class, TestBlazeJpaConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {"classpath:/h2-truncate.sql"})
@DataJpaTest(showSql = false)
public abstract class PersistenceLayerTestSupport { 
}
