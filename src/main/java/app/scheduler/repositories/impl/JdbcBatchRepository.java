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
        return obj;
    };

    @Override
    public Batch save(Batch entity) {
        if (entity.getId() == null || entity.getId().isEmpty()) {
            entity.setId(UUID.randomUUID().toString());
        }
        jdbcTemplate.update(
            SQLQueries.BATCH_INSERT,
            entity.getId(), entity.getName(), entity.getProgram(), entity.getYear()
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
        jdbcTemplate.update(SQLQueries.BATCH_DELETE_EVENTS, id);
        jdbcTemplate.update(SQLQueries.BATCH_DELETE_SECTIONS, id);
        jdbcTemplate.update(SQLQueries.BATCH_DELETE_MAPPINGS, id);
        jdbcTemplate.update(SQLQueries.BATCH_RESET_STUDENTS_BATCH, id);
        return jdbcTemplate.update(SQLQueries.BATCH_DELETE, id) > 0;
    }

    @Override
    public boolean update(Batch entity) {
        return jdbcTemplate.update(
            SQLQueries.BATCH_UPDATE,
            entity.getName(), entity.getProgram(), entity.getYear(), entity.getId()
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

    // --- Section related methods ---

    private final RowMapper<app.scheduler.models.Section> sectionRowMapper = (rs, rowNum) -> {
        app.scheduler.models.Section obj = new app.scheduler.models.Section();
        obj.setId(rs.getString("id"));
        obj.setName(rs.getString("name"));
        obj.setBatchId(rs.getString("batch_id"));
        obj.setStudentCount(rs.getInt("student_count"));
        return obj;
    };

    @Override
    public app.scheduler.models.Section saveSection(app.scheduler.models.Section entity) {
        if (entity.getId() == null || entity.getId().isEmpty()) {
            entity.setId(UUID.randomUUID().toString());
        }
        jdbcTemplate.update(
            SQLQueries.SECTION_INSERT,
            entity.getId(), entity.getName(), entity.getBatchId(), entity.getStudentCount()
        );
        return entity;
    }

    @Override
    public Optional<app.scheduler.models.Section> findSectionById(String id) {
        List<app.scheduler.models.Section> results = jdbcTemplate.query(SQLQueries.SECTION_FIND_BY_ID, sectionRowMapper, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public List<app.scheduler.models.Section> findAllSections() {
        return jdbcTemplate.query(SQLQueries.SECTION_FIND_ALL, sectionRowMapper);
    }

    @Override
    public boolean deleteSection(String id) {
        jdbcTemplate.update(SQLQueries.SECTION_DELETE_EVENTS, id);
        jdbcTemplate.update(SQLQueries.SECTION_RESET_STUDENTS_SECTION, id);
        return jdbcTemplate.update(SQLQueries.SECTION_DELETE, id) > 0;
    }

    @Override
    public boolean updateSection(app.scheduler.models.Section entity) {
        return jdbcTemplate.update(
            SQLQueries.SECTION_UPDATE,
            entity.getName(), entity.getBatchId(), entity.getStudentCount(), entity.getId()
        ) > 0;
    }

    @Override
    public List<app.scheduler.models.Section> findSectionsByBatchId(String batchId) {
        return jdbcTemplate.query(SQLQueries.SECTION_FIND_BY_BATCH_ID, sectionRowMapper, batchId);
    }

    @Override
    public List<app.scheduler.models.Section> findSectionsBySemesterId(String semesterId) {
        return jdbcTemplate.query(SQLQueries.SECTION_FIND_BY_SEMESTER_ID, sectionRowMapper, semesterId);
    }

}
