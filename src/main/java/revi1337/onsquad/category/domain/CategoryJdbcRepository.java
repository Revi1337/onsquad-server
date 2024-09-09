package revi1337.onsquad.squad.domain.category;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

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
