package revi1337.onsquad.infrastructure.sqlite;

import java.util.List;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.infrastructure.aws.s3.FilePath;

@Repository
public class RecycleBinRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public RecycleBinRepository(@Qualifier("sqliteDataSource") DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public void insert(String path) {
        String sql = "INSERT INTO recycle_bin (path) VALUES (:path)";
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("path", path);

        jdbcTemplate.update(sql, sqlParameterSource);
    }

    public void insertBatch(List<String> paths) {
        String sql = "INSERT INTO recycle_bin (path) VALUES (:path)";
        SqlParameterSource[] batchArgs = paths.stream()
                .map(path -> new MapSqlParameterSource("path", path))
                .toArray(SqlParameterSource[]::new);

        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    public List<FilePath> findAll() {
        String sql = "SELECT id, path FROM recycle_bin";

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new FilePath(rs.getLong("id"), rs.getString("path"))
        );
    }

    public int deleteByIdIn(List<Long> ids) {
        String sql = "DELETE FROM recycle_bin WHERE id IN (:ids)";
        SqlParameterSource param = new MapSqlParameterSource("ids", ids);

        return jdbcTemplate.update(sql, param);
    }

    public void deleteAllInBatch() {
        jdbcTemplate.getJdbcOperations().execute("DELETE FROM recycle_bin");
        jdbcTemplate.getJdbcOperations().execute("UPDATE SQLITE_SEQUENCE SET seq = 0 WHERE name = 'recycle_bin'");
    }
}
