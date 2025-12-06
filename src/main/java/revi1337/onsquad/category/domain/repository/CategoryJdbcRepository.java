package revi1337.onsquad.category.domain.repository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.category.domain.entity.Category;

@RequiredArgsConstructor
@Repository
public class CategoryJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public int batchInsert(List<Category> categories) {
        String sql = "INSERT INTO category(id, category_type) VALUES (?, ?)";
        int[][] influenced = jdbcTemplate.batchUpdate(
                sql,
                categories,
                categories.size(),
                (ps, category) -> {
                    ps.setLong(1, category.getCategoryType().getPk());
                    ps.setString(2, category.getCategoryType().toString());
                }
        );

        return Arrays.stream(influenced)
                .flatMapToInt(IntStream::of)
                .sum();
    }
}
