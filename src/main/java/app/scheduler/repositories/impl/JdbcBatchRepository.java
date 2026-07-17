package app.scheduler.repositories.impl;

import app.scheduler.models.Batch;
import app.scheduler.repositories.BatchRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
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
        obj.setSemesterId(rs.getString("semester_id"));
        obj.setActive(rs.getBoolean("is_active"));
        return obj;
    };

    @Override
    public Batch save(Batch entity) {
        if (entity.getId() == null || entity.getId().isEmpty()) {
            entity.setId(UUID.randomUUID().toString());
        }
        jdbcTemplate.update(
            "INSERT INTO batch (id, name, program, year, semester_id, is_active) VALUES (?, ?, ?, ?, ?, ?)",
            entity.getId(), entity.getName(), entity.getProgram(), entity.getYear(), entity.getSemesterId(), entity.isActive()
        );
        return entity;
    }

    @Override
    public Optional<Batch> findById(String id) {
        List<Batch> results = jdbcTemplate.query("SELECT * FROM batch WHERE id = ?", rowMapper, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public List<Batch> findAll() {
        return jdbcTemplate.query("SELECT * FROM batch", rowMapper);
    }

    @Override
    public boolean delete(String id) {
        return jdbcTemplate.update("DELETE FROM batch WHERE id = ?", id) > 0;
    }

    @Override
    public boolean update(Batch entity) {
        return jdbcTemplate.update(
            "UPDATE batch SET name = ?, program = ?, year = ?, semester_id = ?, is_active = ? WHERE id = ?",
            entity.getName(), entity.getProgram(), entity.getYear(), entity.getSemesterId(), entity.isActive(), entity.getId()
        ) > 0;
    }

    @Override
    public List<Batch> findBySemesterId(String semesterId) {
        return jdbcTemplate.query("SELECT * FROM batch WHERE semester_id = ?", rowMapper, semesterId);
    }

    @Override
    public List<Batch> findByProgram(String program) {
        return jdbcTemplate.query("SELECT * FROM batch WHERE program = ?", rowMapper, program);
    }

}
