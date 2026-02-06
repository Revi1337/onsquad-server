package revi1337.onsquad.common;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import revi1337.onsquad.common.config.PersistenceLayerConfiguration;

@Sql({"/h2-truncate.sql"})
@Import(PersistenceLayerConfiguration.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@DataJpaTest(showSql = false)
public abstract class PersistenceLayerTestSupport {

    @PersistenceContext
    protected EntityManager entityManager;

    protected void clearPersistenceContext() {
        entityManager.flush();
        entityManager.clear();
    }
}
