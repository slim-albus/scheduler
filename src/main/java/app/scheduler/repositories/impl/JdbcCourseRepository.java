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
        obj.setLectureHoursPerWeek(rs.getInt("lecture_hours_per_week"));
        obj.setLabHoursPerWeek(rs.getInt("lab_hours_per_week"));
        obj.setMidtermDuration(rs.getInt("midterm_duration"));
        obj.setFinalDuration(rs.getInt("final_duration"));
        obj.setDepartment(rs.getString("department"));
        obj.setActive(rs.getBoolean("is_active"));
        return obj;
    };

    @Override
    public Course save(Course entity) {
        if (entity.getId() == null || entity.getId().isEmpty()) {
            entity.setId(UUID.randomUUID().toString());
        }
        jdbcTemplate.update(
            "INSERT INTO course (id, code, name, has_lab, credits, lecture_hours_per_week, lab_hours_per_week, midterm_duration, final_duration, department, is_active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            entity.getId(), entity.getCode(), entity.getName(), entity.isHasLab(), entity.getCredits(), entity.getLectureHoursPerWeek(), entity.getLabHoursPerWeek(), entity.getMidtermDuration(), entity.getFinalDuration(), entity.getDepartment(), entity.isActive()
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
        return jdbcTemplate.update(SQLQueries.COURSE_DELETE, id) > 0;
    }

    @Override
    public boolean update(Course entity) {
        return jdbcTemplate.update(
            "UPDATE course SET code = ?, name = ?, has_lab = ?, credits = ?, lecture_hours_per_week = ?, lab_hours_per_week = ?, midterm_duration = ?, final_duration = ?, department = ?, is_active = ? WHERE id = ?",
            entity.getCode(), entity.getName(), entity.isHasLab(), entity.getCredits(), entity.getLectureHoursPerWeek(), entity.getLabHoursPerWeek(), entity.getMidtermDuration(), entity.getFinalDuration(), entity.getDepartment(), entity.isActive(), entity.getId()
        ) > 0;
    }

}
