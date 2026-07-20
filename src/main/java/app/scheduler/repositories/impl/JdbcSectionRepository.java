package app.scheduler.repositories.impl;

import app.scheduler.models.Section;
import app.scheduler.repositories.SectionRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import app.scheduler.utils.SQLQueries;
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
        obj.setStudentCount(rs.getInt("student_count"));
        obj.setActive(rs.getBoolean("is_active"));
        return obj;
    };

    @Override
    public Section save(Section entity) {
        if (entity.getId() == null || entity.getId().isEmpty()) {
            entity.setId(UUID.randomUUID().toString());
        }
        jdbcTemplate.update(
            "INSERT INTO section (id, name, batch_id, student_count, is_active) VALUES (?, ?, ?, ?, ?)",
            entity.getId(), entity.getName(), entity.getBatchId(), entity.getStudentCount(), entity.isActive()
        );
        return entity;
    }

    @Override
    public Optional<Section> findById(String id) {
        List<Section> results = jdbcTemplate.query(SQLQueries.SECTION_FIND_BY_ID, rowMapper, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public List<Section> findAll() {
        return jdbcTemplate.query(SQLQueries.SECTION_FIND_ALL, rowMapper);
    }

    @Override
    public boolean delete(String id) {
        jdbcTemplate.update("DELETE FROM event WHERE section_id = ?", id);
        return jdbcTemplate.update(SQLQueries.SECTION_DELETE, id) > 0;
    }

    @Override
    public boolean update(Section entity) {
        return jdbcTemplate.update(
            "UPDATE section SET name = ?, batch_id = ?, student_count = ?, is_active = ? WHERE id = ?",
            entity.getName(), entity.getBatchId(), entity.getStudentCount(), entity.isActive(), entity.getId()
        ) > 0;
    }

    @Override
    public List<Section> findByBatchId(String batchId) {
        return jdbcTemplate.query(SQLQueries.SECTION_FIND_BY_BATCH_ID, rowMapper, batchId);
    }

    @Override
    public List<Section> findBySemesterId(String semesterId) {
        return jdbcTemplate.query(SQLQueries.SECTION_FIND_BY_SEMESTER_ID, rowMapper, semesterId);
    }

}
