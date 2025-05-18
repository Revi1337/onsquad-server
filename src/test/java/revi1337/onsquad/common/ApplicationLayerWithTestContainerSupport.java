package revi1337.onsquad.common;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.test.mock.mockito.MockBean;
import revi1337.onsquad.inrastructure.file.support.RecycleBinLifeCycleManager;
import revi1337.onsquad.inrastructure.mail.support.VerificationCacheLifeCycleManager;

public abstract class ApplicationLayerWithTestContainerSupport extends TestContainerSupport {

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
