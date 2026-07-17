package app.scheduler.repositories.impl;

import app.scheduler.models.Semester;
import app.scheduler.repositories.SemesterRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
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
        obj.setActive(rs.getBoolean("is_active"));
        return obj;
    };

    @Override
    public Semester save(Semester entity) {
        if (entity.getId() == null || entity.getId().isEmpty()) {
            entity.setId(UUID.randomUUID().toString());
        }
        jdbcTemplate.update(
            "INSERT INTO semester (id, name, code, start_date, end_date, weeks, academic_year, is_active) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
            entity.getId(), entity.getName(), entity.getCode(), 
            DateUtils.formatSqliteDate(entity.getStartDate()), 
            DateUtils.formatSqliteDate(entity.getEndDate()), 
            entity.getWeeks(), entity.getAcademicYear(), entity.isActive()
        );
        return entity;
    }

    @Override
    public Optional<Semester> findById(String id) {
        List<Semester> results = jdbcTemplate.query("SELECT * FROM semester WHERE id = ?", rowMapper, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public List<Semester> findAll() {
        return jdbcTemplate.query("SELECT * FROM semester", rowMapper);
    }

    @Override
    public boolean delete(String id) {
        return jdbcTemplate.update("DELETE FROM semester WHERE id = ?", id) > 0;
    }

    @Override
    public boolean update(Semester entity) {
        return jdbcTemplate.update(
            "UPDATE semester SET name = ?, code = ?, start_date = ?, end_date = ?, weeks = ?, academic_year = ?, is_active = ? WHERE id = ?",
            entity.getName(), entity.getCode(), 
            DateUtils.formatSqliteDate(entity.getStartDate()), 
            DateUtils.formatSqliteDate(entity.getEndDate()), 
            entity.getWeeks(), entity.getAcademicYear(), entity.isActive(), entity.getId()
        ) > 0;
    }

    @Override
    public Optional<Semester> findByCode(String code) {
        List<Semester> results = jdbcTemplate.query("SELECT * FROM semester WHERE code = ?", rowMapper, code);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public List<Semester> findByYear(String year) {
        return jdbcTemplate.query("SELECT * FROM semester WHERE academic_year = ?", rowMapper, year);
    }

}
