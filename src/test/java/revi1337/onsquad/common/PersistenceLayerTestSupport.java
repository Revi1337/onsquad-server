package revi1337.onsquad.common;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import revi1337.onsquad.category.domain.entity.Category;
import revi1337.onsquad.category.domain.entity.vo.CategoryType;
import revi1337.onsquad.category.domain.repository.CategoryJpaRepository;
import revi1337.onsquad.common.config.PersistenceLayerConfiguration;

@TestInstance(Lifecycle.PER_CLASS)
@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import(PersistenceLayerConfiguration.class)
@Sql({"/h2-truncate.sql"})
public abstract class PersistenceLayerTestSupport {

    @PersistenceContext
    protected EntityManager entityManager;

    @Autowired
    protected CategoryJpaRepository categoryJpaRepository;

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
