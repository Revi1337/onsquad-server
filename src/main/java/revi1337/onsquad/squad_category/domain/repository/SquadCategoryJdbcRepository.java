package revi1337.onsquad.squad_category.domain.repository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.squad_category.domain.entity.SquadCategory;

@Repository
@RequiredArgsConstructor
public class SquadCategoryJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public int insertBatch(List<SquadCategory> squadCategories) {
        String sql = "INSERT INTO squad_category(squad_id, category_id) VALUES (?, ?)";
        int[][] influenced = jdbcTemplate.batchUpdate(
                sql,
                squadCategories,
                squadCategories.size(),
                (ps, squadCategory) -> {
                    ps.setLong(1, squadCategory.getSquad().getId());
                    ps.setLong(2, squadCategory.getCategory().getId());
                }
        );

        return Arrays.stream(influenced)
                .flatMapToInt(IntStream::of)
                .sum();
    }
}
