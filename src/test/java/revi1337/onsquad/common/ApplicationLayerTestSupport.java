package revi1337.onsquad.common;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.auth.verification.VerificationBackupProcessor;
import revi1337.onsquad.category.domain.entity.Category;
import revi1337.onsquad.category.domain.entity.vo.CategoryType;
import revi1337.onsquad.category.domain.repository.CategoryJpaRepository;
import revi1337.onsquad.common.aspect.RedisCacheAspect;
import revi1337.onsquad.common.aspect.ThrottlingAspect;
import revi1337.onsquad.common.config.ApplicationLayerConfiguration;
import revi1337.onsquad.infrastructure.recyclebin.RecycleBinLifeCycleManager;

@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@RecordApplicationEvents
@Transactional
@Import({ApplicationLayerConfiguration.class})
@Sql({"/h2-truncate.sql"})
public abstract class ApplicationLayerTestSupport {

    @MockBean
    protected RecycleBinLifeCycleManager recycleBinLifeCycleManager;

    @MockBean
    protected VerificationBackupProcessor verificationBackupProcessor;

    @MockBean
    protected ThrottlingAspect throttlingAspect;

    @MockBean
    protected RedisCacheAspect redisCacheAspect;

    @PersistenceContext
    protected EntityManager entityManager;

    @Autowired
    private CategoryJpaRepository categoryJpaRepository;

    @BeforeAll
    void initCategory() {
        if (categoryJpaRepository.count() == 0) {
            categoryJpaRepository.saveAll(CategoryType.unmodifiableList().stream()
                    .map(Category::new)
                    .toList());
        }
    }

    protected void clearPersistenceContext() {
        entityManager.flush();
        entityManager.clear();
    }
}
