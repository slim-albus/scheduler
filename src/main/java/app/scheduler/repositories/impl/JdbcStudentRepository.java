package app.scheduler.repositories.impl;

import app.scheduler.models.Student;
import app.scheduler.repositories.StudentRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import app.scheduler.utils.SQLQueries;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JdbcStudentRepository implements StudentRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcStudentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Student> rowMapper = (rs, rowNum) -> {
        Student obj = new Student();
        obj.setId(rs.getString("id"));
        obj.setName(rs.getString("name"));
        obj.setStudentId(rs.getString("student_id"));
        obj.setEmail(rs.getString("email"));
        obj.setSectionId(rs.getString("section_id"));
        obj.setBatchId(rs.getString("batch_id"));
        obj.setLabGroup(rs.getInt("lab_group"));
        return obj;
    };

    @Override
    public Student save(Student entity) {
        if (entity.getId() == null || entity.getId().isEmpty()) {
            entity.setId(UUID.randomUUID().toString());
        }
        jdbcTemplate.update(
            SQLQueries.STUDENT_INSERT,
            entity.getId(), entity.getName(), entity.getStudentId(), entity.getEmail(), entity.getSectionId(), entity.getBatchId(), entity.getLabGroup()
        );
        return entity;
    }

    @Override
    public Optional<Student> findById(String id) {
        List<Student> results = jdbcTemplate.query(SQLQueries.STUDENT_FIND_BY_ID, rowMapper, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public List<Student> findAll() {
        return jdbcTemplate.query(SQLQueries.STUDENT_FIND_ALL, rowMapper);
    }

    @Override
    public boolean delete(String id) {
        return jdbcTemplate.update(SQLQueries.STUDENT_DELETE, id) > 0;
    }

    @Override
    public boolean update(Student entity) {
        return jdbcTemplate.update(
            SQLQueries.STUDENT_UPDATE,
            entity.getName(), entity.getStudentId(), entity.getEmail(), entity.getSectionId(), entity.getBatchId(), entity.getLabGroup(), entity.getId()
        ) > 0;
    }

}
