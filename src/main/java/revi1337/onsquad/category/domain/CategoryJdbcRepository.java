package revi1337.onsquad.category.domain;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class CategoryJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public void insertBulkCategories(List<Category> categories) {
        String sql = "INSERT INTO category (category_type) VALUES (?)";
        jdbcTemplate.batchUpdate(
                sql,
                categories,
                categories.size(),
                (ps, category) -> ps.setString(1, category.getCategoryType().toString())

        );
    }
}
