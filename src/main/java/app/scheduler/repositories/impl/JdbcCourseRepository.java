package app.scheduler.repositories.impl;

import app.scheduler.models.Course;
import app.scheduler.repositories.CourseRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import app.scheduler.utils.SQLQueries;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JdbcCourseRepository implements CourseRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcCourseRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Course> rowMapper = (rs, rowNum) -> {
        Course obj = new Course();
        obj.setId(rs.getString("id"));
        obj.setCode(rs.getString("code"));
        obj.setName(rs.getString("name"));
        obj.setHasLab(rs.getBoolean("has_lab"));
        obj.setCredits(rs.getInt("credits"));
        obj.setDepartment(rs.getString("department"));
        return obj;
    };

    @Override
    public Course save(Course entity) {
        if (entity.getId() == null || entity.getId().isEmpty()) {
            entity.setId(UUID.randomUUID().toString());
        }
        jdbcTemplate.update(
            SQLQueries.COURSE_INSERT,
            entity.getId(), entity.getCode(), entity.getName(), entity.isHasLab(), entity.getCredits(), 
            entity.getDepartment()
        );
        return entity;
    }

    @Override
    public Optional<Course> findById(String id) {
        List<Course> results = jdbcTemplate.query(SQLQueries.COURSE_FIND_BY_ID, rowMapper, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public List<Course> findAll() {
        return jdbcTemplate.query(SQLQueries.COURSE_FIND_ALL, rowMapper);
    }

    @Override
    public boolean delete(String id) {
        jdbcTemplate.update("DELETE FROM events WHERE course_id = ?", id);
        jdbcTemplate.update("DELETE FROM batch_course_mappings WHERE course_id = ?", id);
        return jdbcTemplate.update(SQLQueries.COURSE_DELETE, id) > 0;
    }

    @Override
    public boolean update(Course entity) {
        return jdbcTemplate.update(
            SQLQueries.COURSE_UPDATE,
            entity.getCode(), entity.getName(), entity.isHasLab(), entity.getCredits(), 
            entity.getDepartment(), entity.getId()
        ) > 0;
    }

}
