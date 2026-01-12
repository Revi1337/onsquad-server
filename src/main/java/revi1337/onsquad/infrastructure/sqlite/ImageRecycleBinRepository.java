package revi1337.onsquad.infrastructure.sqlite;

import java.util.List;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
public class ImageRecycleBinRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public ImageRecycleBinRepository(@Qualifier("sqliteDataSource") DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public void insert(String path) {
        String sql = "INSERT INTO image_recycle_bin (path) VALUES (:path)";
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("path", path);

        jdbcTemplate.update(sql, sqlParameterSource);
    }

    public void insertBatch(List<String> paths) {
        String sql = "INSERT INTO image_recycle_bin (path) VALUES (:path)";
        SqlParameterSource[] batchArgs = paths.stream()
                .map(path -> new MapSqlParameterSource("path", path))
                .toArray(SqlParameterSource[]::new);

        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    public List<DeletedImage> findAll() {
        String sql = "SELECT id, path, retry_count FROM image_recycle_bin";

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new DeletedImage(
                        rs.getLong("id"),
                        rs.getString("path"),
                        rs.getInt("retry_count")
                )
        );
    }

    public List<DeletedImage> findByRetryCountLargerThan(int retryCount) {
        String sql = "SELECT id, path, retry_count FROM image_recycle_bin WHERE retry_count >= (:retryCount)";
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource("retryCount", retryCount);

        return jdbcTemplate.query(
                sql,
                sqlParameterSource,
                (rs, rowNum) -> new DeletedImage(
                        rs.getLong("id"),
                        rs.getString("path"),
                        rs.getInt("retry_count")
                )
        );
    }

    public int incrementRetryCount(List<Long> ids) {
        String sql = "UPDATE image_recycle_bin SET retry_count = retry_count + 1 WHERE id IN (:ids)";
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource("ids", ids);

        return jdbcTemplate.update(sql, sqlParameterSource);
    }

    public int deleteByIdIn(List<Long> ids) {
        String sql = "DELETE FROM image_recycle_bin WHERE id IN (:ids)";
        SqlParameterSource param = new MapSqlParameterSource("ids", ids);

        return jdbcTemplate.update(sql, param);
    }

    public void deleteAllInBatch() {
        jdbcTemplate.getJdbcOperations().execute("DELETE FROM image_recycle_bin");
        jdbcTemplate.getJdbcOperations().execute("UPDATE SQLITE_SEQUENCE SET seq = 0 WHERE name = 'image_recycle_bin'");
    }
}
