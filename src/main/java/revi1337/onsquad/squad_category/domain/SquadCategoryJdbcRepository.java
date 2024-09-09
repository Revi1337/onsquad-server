package revi1337.onsquad.squad_category;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.category.domain.Category;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class SquadCategoryJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public void batchInsertSquadCategories(Long squadId, List<Category> categories) {
        String sql = "INSERT INTO squad_category(squad_id, category_id) VALUES (?, ?)";
        jdbcTemplate.batchUpdate(
                sql,
                categories,
                categories.size(),
                (ps, category) -> {
                    ps.setLong(1, squadId);
                    ps.setLong(2, category.getId());
                }
        );
    }
}
