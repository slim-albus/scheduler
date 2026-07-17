package app.scheduler.repositories.impl;

import app.scheduler.models.BatchCourseMapping;
import app.scheduler.repositories.BatchCourseMappingRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JdbcBatchCourseMappingRepository implements BatchCourseMappingRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcBatchCourseMappingRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<BatchCourseMapping> rowMapper = (rs, rowNum) -> {
        BatchCourseMapping obj = new BatchCourseMapping();
        obj.setId(rs.getString("id"));
        obj.setBatchId(rs.getString("batch_id"));
        obj.setCourseId(rs.getString("course_id"));
        obj.setLectureTeacherId(rs.getString("lecture_teacher_id"));
        obj.setLabInstructorId(rs.getString("lab_instructor_id"));
        obj.setRequired(rs.getBoolean("is_required"));
        obj.setSemesterId(rs.getString("semester_id"));
        return obj;
    };

    @Override
    public BatchCourseMapping save(BatchCourseMapping entity) {
        if (entity.getId() == null || entity.getId().isEmpty()) {
            entity.setId(UUID.randomUUID().toString());
        }
        jdbcTemplate.update(
            "INSERT INTO batch_course_mapping (id, batch_id, course_id, lecture_teacher_id, lab_instructor_id, is_required, semester_id) VALUES (?, ?, ?, ?, ?, ?, ?)",
            entity.getId(), entity.getBatchId(), entity.getCourseId(), entity.getLectureTeacherId(), entity.getLabInstructorId(), entity.isRequired(), entity.getSemesterId()
        );
        return entity;
    }

    @Override
    public Optional<BatchCourseMapping> findById(String id) {
        List<BatchCourseMapping> results = jdbcTemplate.query("SELECT * FROM batch_course_mapping WHERE id = ?", rowMapper, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public List<BatchCourseMapping> findAll() {
        return jdbcTemplate.query("SELECT * FROM batch_course_mapping", rowMapper);
    }

    @Override
    public boolean delete(String id) {
        return jdbcTemplate.update("DELETE FROM batch_course_mapping WHERE id = ?", id) > 0;
    }

    @Override
    public boolean update(BatchCourseMapping entity) {
        return jdbcTemplate.update(
            "UPDATE batch_course_mapping SET batch_id = ?, course_id = ?, lecture_teacher_id = ?, lab_instructor_id = ?, is_required = ?, semester_id = ? WHERE id = ?",
            entity.getBatchId(), entity.getCourseId(), entity.getLectureTeacherId(), entity.getLabInstructorId(), entity.isRequired(), entity.getSemesterId(), entity.getId()
        ) > 0;
    }

    @Override
    public List<BatchCourseMapping> findByBatchId(String batchId) {
        return jdbcTemplate.query("SELECT * FROM batch_course_mapping WHERE batch_id = ?", rowMapper, batchId);
    }

    @Override
    public List<BatchCourseMapping> findBySemesterId(String semesterId) {
        return jdbcTemplate.query("SELECT * FROM batch_course_mapping WHERE semester_id = ?", rowMapper, semesterId);
    }

    @Override
    public Optional<BatchCourseMapping> findByBatchIdAndCourseId(String batchId, String courseId) {
        List<BatchCourseMapping> results = jdbcTemplate.query("SELECT * FROM batch_course_mapping WHERE batch_id = ? AND course_id = ?", rowMapper, batchId, courseId);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

}
