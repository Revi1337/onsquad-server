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
import revi1337.onsquad.category.domain.Category;
import revi1337.onsquad.category.domain.CategoryJpaRepository;
import revi1337.onsquad.category.domain.vo.CategoryType;
import revi1337.onsquad.common.config.ApplicationLayerConfiguration;
import revi1337.onsquad.inrastructure.file.support.RecycleBinLifeCycleManager;
import revi1337.onsquad.inrastructure.mail.support.VerificationCacheLifeCycleManager;

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
    protected VerificationCacheLifeCycleManager verificationCacheLifeCycleManager;

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
