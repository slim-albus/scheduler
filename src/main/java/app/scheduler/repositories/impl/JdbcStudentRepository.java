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
        obj.setProgram(rs.getString("program"));
        obj.setYear(rs.getString("year"));
        obj.setSemesterId(rs.getString("semester_id"));
        obj.setActive(rs.getBoolean("is_active"));
        return obj;
    };

    @Override
    public Student save(Student entity) {
        if (entity.getId() == null || entity.getId().isEmpty()) {
            entity.setId(UUID.randomUUID().toString());
        }
        jdbcTemplate.update(
            "INSERT INTO student (id, name, student_id, email, section_id, batch_id, lab_group, program, year, semester_id, is_active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            entity.getId(), entity.getName(), entity.getStudentId(), entity.getEmail(), entity.getSectionId(), entity.getBatchId(), entity.getLabGroup(), entity.getProgram(), entity.getYear(), entity.getSemesterId(), entity.isActive()
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
            "UPDATE student SET name = ?, student_id = ?, email = ?, section_id = ?, batch_id = ?, lab_group = ?, program = ?, year = ?, semester_id = ?, is_active = ? WHERE id = ?",
            entity.getName(), entity.getStudentId(), entity.getEmail(), entity.getSectionId(), entity.getBatchId(), entity.getLabGroup(), entity.getProgram(), entity.getYear(), entity.getSemesterId(), entity.isActive(), entity.getId()
        ) > 0;
    }

}
