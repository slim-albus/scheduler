package app.scheduler.repositories.impl;

import app.scheduler.models.Section;
import app.scheduler.repositories.SectionRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JdbcSectionRepository implements SectionRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcSectionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Section> rowMapper = (rs, rowNum) -> {
        Section obj = new Section();
        obj.setId(rs.getString("id"));
        obj.setName(rs.getString("name"));
        obj.setBatchId(rs.getString("batch_id"));
        obj.setLabGroup(rs.getInt("lab_group"));
        obj.setStudentCount(rs.getInt("student_count"));
        obj.setSemesterId(rs.getString("semester_id"));
        obj.setActive(rs.getBoolean("is_active"));
        return obj;
    };

    @Override
    public Section save(Section entity) {
        if (entity.getId() == null || entity.getId().isEmpty()) {
            entity.setId(UUID.randomUUID().toString());
        }
        jdbcTemplate.update(
            "INSERT INTO section (id, name, batch_id, lab_group, student_count, semester_id, is_active) VALUES (?, ?, ?, ?, ?, ?, ?)",
            entity.getId(), entity.getName(), entity.getBatchId(), entity.getLabGroup(), entity.getStudentCount(), entity.getSemesterId(), entity.isActive()
        );
        return entity;
    }

    @Override
    public Optional<Section> findById(String id) {
        List<Section> results = jdbcTemplate.query("SELECT * FROM section WHERE id = ?", rowMapper, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public List<Section> findAll() {
        return jdbcTemplate.query("SELECT * FROM section", rowMapper);
    }

    @Override
    public boolean delete(String id) {
        return jdbcTemplate.update("DELETE FROM section WHERE id = ?", id) > 0;
    }

    @Override
    public boolean update(Section entity) {
        return jdbcTemplate.update(
            "UPDATE section SET name = ?, batch_id = ?, lab_group = ?, student_count = ?, semester_id = ?, is_active = ? WHERE id = ?",
            entity.getName(), entity.getBatchId(), entity.getLabGroup(), entity.getStudentCount(), entity.getSemesterId(), entity.isActive(), entity.getId()
        ) > 0;
    }

    @Override
    public List<Section> findByBatchId(String batchId) {
        return jdbcTemplate.query("SELECT * FROM section WHERE batch_id = ?", rowMapper, batchId);
    }

    @Override
    public List<Section> findBySemesterId(String semesterId) {
        return jdbcTemplate.query("SELECT * FROM section WHERE semester_id = ?", rowMapper, semesterId);
    }

}
