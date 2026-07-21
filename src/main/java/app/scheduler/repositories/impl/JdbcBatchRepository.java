package app.scheduler.repositories.impl;

import app.scheduler.models.Batch;
import app.scheduler.repositories.BatchRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import app.scheduler.utils.SQLQueries;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JdbcBatchRepository implements BatchRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcBatchRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Batch> rowMapper = (rs, rowNum) -> {
        Batch obj = new Batch();
        obj.setId(rs.getString("id"));
        obj.setName(rs.getString("name"));
        obj.setProgram(rs.getString("program"));
        obj.setYear(rs.getString("year"));
        obj.setActive(rs.getBoolean("is_active"));
        return obj;
    };

    @Override
    public Batch save(Batch entity) {
        if (entity.getId() == null || entity.getId().isEmpty()) {
            entity.setId(UUID.randomUUID().toString());
        }
        jdbcTemplate.update(
            "INSERT INTO batches (id, name, program, year, is_active) VALUES (?, ?, ?, ?, ?)",
            entity.getId(), entity.getName(), entity.getProgram(), entity.getYear(), entity.isActive()
        );
        return entity;
    }

    @Override
    public Optional<Batch> findById(String id) {
        List<Batch> results = jdbcTemplate.query(SQLQueries.BATCH_FIND_BY_ID, rowMapper, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public List<Batch> findAll() {
        return jdbcTemplate.query(SQLQueries.BATCH_FIND_ALL, rowMapper);
    }

    @Override
    public boolean delete(String id) {
        jdbcTemplate.update("DELETE FROM events WHERE batch_id = ?", id);
        jdbcTemplate.update("DELETE FROM sections WHERE batch_id = ?", id);
        jdbcTemplate.update("DELETE FROM batch_course_mappings WHERE batch_id = ?", id);
        jdbcTemplate.update("UPDATE students SET batch_id = NULL WHERE batch_id = ?", id);
        return jdbcTemplate.update(SQLQueries.BATCH_DELETE, id) > 0;
    }

    @Override
    public boolean update(Batch entity) {
        return jdbcTemplate.update(
            "UPDATE batches SET name = ?, program = ?, year = ?, is_active = ? WHERE id = ?",
            entity.getName(), entity.getProgram(), entity.getYear(), entity.isActive(), entity.getId()
        ) > 0;
    }

    @Override
    public List<Batch> findBySemesterId(String semesterId) {
        return jdbcTemplate.query(SQLQueries.BATCH_FIND_BY_SEMESTER_ID, rowMapper, semesterId);
    }

    @Override
    public List<Batch> findByProgram(String program) {
        return jdbcTemplate.query(SQLQueries.BATCH_FIND_BY_PROGRAM, rowMapper, program);
    }

}
