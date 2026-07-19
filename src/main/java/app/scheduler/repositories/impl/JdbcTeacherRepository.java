package app.scheduler.repositories.impl;

import app.scheduler.models.Teacher;
import app.scheduler.repositories.TeacherRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import app.scheduler.utils.SQLQueries;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JdbcTeacherRepository implements TeacherRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcTeacherRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Teacher> rowMapper = (rs, rowNum) -> {
        Teacher obj = new Teacher();
        obj.setId(rs.getString("id"));
        obj.setName(rs.getString("name"));
        obj.setEmail(rs.getString("email"));
        obj.setDepartment(rs.getString("department"));
        obj.setType(rs.getString("type"));
        obj.setAvailabilityBitmask(rs.getLong("availability_bitmask"));
        obj.setActive(rs.getBoolean("is_active"));
        return obj;
    };

    @Override
    public Teacher save(Teacher entity) {
        if (entity.getId() == null || entity.getId().isEmpty()) {
            entity.setId(UUID.randomUUID().toString());
        }
        jdbcTemplate.update(
            "INSERT INTO teacher (id, name, email, department, type, availability_bitmask, is_active) VALUES (?, ?, ?, ?, ?, ?, ?)",
            entity.getId(), entity.getName(), entity.getEmail(), entity.getDepartment(), entity.getType(), entity.getAvailabilityBitmask(), entity.isActive()
        );
        return entity;
    }

    @Override
    public Optional<Teacher> findById(String id) {
        List<Teacher> results = jdbcTemplate.query(SQLQueries.TEACHER_FIND_BY_ID, rowMapper, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public List<Teacher> findAll() {
        return jdbcTemplate.query(SQLQueries.TEACHER_FIND_ALL, rowMapper);
    }

    @Override
    public boolean delete(String id) {
        return jdbcTemplate.update(SQLQueries.TEACHER_DELETE, id) > 0;
    }

    @Override
    public boolean update(Teacher entity) {
        return jdbcTemplate.update(
            "UPDATE teacher SET name = ?, email = ?, department = ?, type = ?, availability_bitmask = ?, is_active = ? WHERE id = ?",
            entity.getName(), entity.getEmail(), entity.getDepartment(), entity.getType(), entity.getAvailabilityBitmask(), entity.isActive(), entity.getId()
        ) > 0;
    }

}
