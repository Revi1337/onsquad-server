package revi1337.onsquad.squad_category.domain.repository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.category.domain.entity.Category;

@RequiredArgsConstructor
@Repository
public class SquadCategoryJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public int insertBatch(Long squadId, List<Category> categories) {
        String sql = "INSERT INTO squad_category(squad_id, category_id) VALUES (?, ?)";
        int[][] influenced = jdbcTemplate.batchUpdate(
                sql,
                categories,
                categories.size(),
                (ps, category) -> {
                    ps.setLong(1, squadId);
                    ps.setLong(2, category.getId());
                }
        );

        return Arrays.stream(influenced)
                .flatMapToInt(IntStream::of)
                .sum();
    }
}
