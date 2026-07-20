package app.scheduler.repositories.impl;

import app.scheduler.models.Semester;
import app.scheduler.repositories.SemesterRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import app.scheduler.utils.SQLQueries;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import app.scheduler.utils.DateUtils;

@Repository
public class JdbcSemesterRepository implements SemesterRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcSemesterRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Semester> rowMapper = (rs, rowNum) -> {
        Semester obj = new Semester();
        obj.setId(rs.getString("id"));
        obj.setName(rs.getString("name"));
        obj.setCode(rs.getString("code"));
        obj.setStartDate(DateUtils.parseSqliteDate(rs.getString("start_date")));
        obj.setEndDate(DateUtils.parseSqliteDate(rs.getString("end_date")));
        obj.setWeeks(rs.getInt("weeks"));
        obj.setAcademicYear(rs.getString("academic_year"));
        obj.setGenerated(rs.getBoolean("is_generated"));
        obj.setActive(rs.getBoolean("is_active"));
        return obj;
    };

    @Override
    public Semester save(Semester entity) {
        if (entity.getId() == null || entity.getId().isEmpty()) {
            entity.setId(UUID.randomUUID().toString());
        }
        if (entity.isActive()) {
            jdbcTemplate.update("UPDATE semester SET is_active = 0");
        }
        jdbcTemplate.update(
            "INSERT INTO semester (id, name, code, start_date, end_date, weeks, academic_year, is_generated, is_active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
            entity.getId(), entity.getName(), entity.getCode(), 
            DateUtils.formatSqliteDate(entity.getStartDate()), 
            DateUtils.formatSqliteDate(entity.getEndDate()), 
            entity.getWeeks(), entity.getAcademicYear(), entity.isGenerated(), entity.isActive()
        );
        return entity;
    }

    @Override
    public Optional<Semester> findById(String id) {
        List<Semester> results = jdbcTemplate.query(SQLQueries.SEMESTER_FIND_BY_ID, rowMapper, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public List<Semester> findAll() {
        return jdbcTemplate.query(SQLQueries.SEMESTER_FIND_ALL, rowMapper);
    }

    @Override
    public boolean delete(String id) {
        jdbcTemplate.update("DELETE FROM event WHERE semester_id = ?", id);
        jdbcTemplate.update("DELETE FROM batch_course_mapping WHERE semester_id = ?", id);
        return jdbcTemplate.update(SQLQueries.SEMESTER_DELETE, id) > 0;
    }

    @Override
    public boolean update(Semester entity) {
        if (entity.isActive()) {
            jdbcTemplate.update("UPDATE semester SET is_active = 0 WHERE id != ?", entity.getId());
        }
        return jdbcTemplate.update(
            "UPDATE semester SET name = ?, code = ?, start_date = ?, end_date = ?, weeks = ?, academic_year = ?, is_generated = ?, is_active = ? WHERE id = ?",
            entity.getName(), entity.getCode(), 
            DateUtils.formatSqliteDate(entity.getStartDate()), 
            DateUtils.formatSqliteDate(entity.getEndDate()), 
            entity.getWeeks(), entity.getAcademicYear(), entity.isGenerated(), entity.isActive(), entity.getId()
        ) > 0;
    }

    @Override
    public Optional<Semester> findActive() {
        List<Semester> results = jdbcTemplate.query("SELECT * FROM semester WHERE is_active = 1 LIMIT 1", rowMapper);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public boolean setActive(String id) {
        jdbcTemplate.update("UPDATE semester SET is_active = 0");
        return jdbcTemplate.update("UPDATE semester SET is_active = 1 WHERE id = ?", id) > 0;
    }

    @Override
    public Optional<Semester> findByCode(String code) {
        List<Semester> results = jdbcTemplate.query(SQLQueries.SEMESTER_FIND_BY_CODE, rowMapper, code);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public List<Semester> findByYear(String year) {
        return jdbcTemplate.query(SQLQueries.SEMESTER_FIND_BY_ACADEMIC_YEAR, rowMapper, year);
    }

}
