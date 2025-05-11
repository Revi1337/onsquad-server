package revi1337.onsquad.common;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.common.config.ApplicationLayerConfiguration;
import revi1337.onsquad.inrastructure.file.support.RecycleBinLifeCycleManager;
import revi1337.onsquad.inrastructure.mail.support.VerificationCacheLifeCycleManager;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@RecordApplicationEvents
@Transactional
@Import({ApplicationLayerConfiguration.class})
@Sql({"/h2-truncate.sql"})
public abstract class ApplicationLayerTestSupport {

    @MockBean
    protected RecycleBinLifeCycleManager recycleBinLifeCycleManager;

    @MockBean
    protected VerificationCacheLifeCycleManager verificationCacheLifeCycleManager;

    @PersistenceContext
    protected EntityManager entityManager;

    protected void clearPersistenceContext() {
        entityManager.flush();
        entityManager.clear();
    }
}
