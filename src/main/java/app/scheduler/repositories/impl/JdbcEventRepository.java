package app.scheduler.repositories.impl;

import app.scheduler.models.Event;
import app.scheduler.repositories.EventRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import app.scheduler.utils.DateUtils;

@Repository
public class JdbcEventRepository implements EventRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcEventRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Event> rowMapper = (rs, rowNum) -> {
        Event obj = new Event();
        obj.setId(rs.getString("id"));
        obj.setType(rs.getString("type"));
        obj.setTopic(rs.getString("topic"));
        obj.setSectionId(rs.getString("section_id"));
        obj.setCourseId(rs.getString("course_id"));
        obj.setTeacherId(rs.getString("teacher_id"));
        obj.setRoomId(rs.getString("room_id"));
        obj.setSemesterId(rs.getString("semester_id"));
        obj.setBatchId(rs.getString("batch_id"));
        obj.setLabGroup(rs.getInt("lab_group"));
        obj.setDay(rs.getInt("day"));
        obj.setPeriod(rs.getInt("period"));
        obj.setWeek(rs.getInt("week"));
        obj.setStartDateTime(DateUtils.parseSqliteTimestamp(rs.getString("start_date_time")));
        obj.setEndDateTime(DateUtils.parseSqliteTimestamp(rs.getString("end_date_time")));
        obj.setDurationMinutes(rs.getInt("duration_minutes"));
        obj.setInstance(rs.getInt("instance"));
        obj.setCreatedBy(rs.getString("created_by"));
        obj.setCreatedAt(DateUtils.parseSqliteTimestamp(rs.getString("created_at")));
        obj.setVersion(rs.getInt("version"));
        obj.setStatus(rs.getString("status"));
        return obj;
    };

    @Override
    public Event save(Event entity) {
        if (entity.getId() == null || entity.getId().isEmpty()) {
            entity.setId(UUID.randomUUID().toString());
        }
        jdbcTemplate.update(
            "INSERT INTO event (id, type, topic, section_id, course_id, teacher_id, room_id, semester_id, batch_id, lab_group, day, period, week, start_date_time, end_date_time, duration_minutes, instance, created_by, created_at, version, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            entity.getId(), entity.getType(), entity.getTopic(), entity.getSectionId(), entity.getCourseId(), entity.getTeacherId(), entity.getRoomId(), entity.getSemesterId(), entity.getBatchId(), entity.getLabGroup(), entity.getDay(), entity.getPeriod(), entity.getWeek(), 
            DateUtils.formatSqliteTimestamp(entity.getStartDateTime()), 
            DateUtils.formatSqliteTimestamp(entity.getEndDateTime()), 
            entity.getDurationMinutes(), entity.getInstance(), entity.getCreatedBy(), 
            DateUtils.formatSqliteTimestamp(entity.getCreatedAt()), 
            entity.getVersion(), entity.getStatus()
        );
        return entity;
    }

    @Override
    public Optional<Event> findById(String id) {
        List<Event> results = jdbcTemplate.query("SELECT * FROM event WHERE id = ?", rowMapper, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public List<Event> findAll() {
        return jdbcTemplate.query("SELECT * FROM event", rowMapper);
    }

    @Override
    public boolean delete(String id) {
        return jdbcTemplate.update("DELETE FROM event WHERE id = ?", id) > 0;
    }

    @Override
    public boolean update(Event entity) {
        return jdbcTemplate.update(
            "UPDATE event SET type = ?, topic = ?, section_id = ?, course_id = ?, teacher_id = ?, room_id = ?, semester_id = ?, batch_id = ?, lab_group = ?, day = ?, period = ?, week = ?, start_date_time = ?, end_date_time = ?, duration_minutes = ?, instance = ?, created_by = ?, created_at = ?, version = ?, status = ? WHERE id = ?",
            entity.getType(), entity.getTopic(), entity.getSectionId(), entity.getCourseId(), entity.getTeacherId(), entity.getRoomId(), entity.getSemesterId(), entity.getBatchId(), entity.getLabGroup(), entity.getDay(), entity.getPeriod(), entity.getWeek(), 
            DateUtils.formatSqliteTimestamp(entity.getStartDateTime()), 
            DateUtils.formatSqliteTimestamp(entity.getEndDateTime()), 
            entity.getDurationMinutes(), entity.getInstance(), entity.getCreatedBy(), 
            DateUtils.formatSqliteTimestamp(entity.getCreatedAt()), 
            entity.getVersion(), entity.getStatus(), entity.getId()
        ) > 0;
    }

    @Override
    public List<Event> findBySectionId(String sectionId) {
        return jdbcTemplate.query("SELECT * FROM event WHERE section_id = ?", rowMapper, sectionId);
    }

    @Override
    public List<Event> findByTeacherId(String teacherId) {
        return jdbcTemplate.query("SELECT * FROM event WHERE teacher_id = ?", rowMapper, teacherId);
    }

    @Override
    public List<Event> findByRoomId(String roomId) {
        return jdbcTemplate.query("SELECT * FROM event WHERE room_id = ?", rowMapper, roomId);
    }

    @Override
    public List<Event> findBySemesterId(String semesterId) {
        return jdbcTemplate.query("SELECT * FROM event WHERE semester_id = ?", rowMapper, semesterId);
    }

    @Override
    public List<Event> findBySectionIdAndWeek(String sectionId, int week) {
        return jdbcTemplate.query("SELECT * FROM event WHERE section_id = ? AND week = ?", rowMapper, sectionId, week);
    }

    @Override
    public List<Event> findByTeacherIdAndDay(String teacherId, int day) {
        return jdbcTemplate.query("SELECT * FROM event WHERE teacher_id = ? AND day = ?", rowMapper, teacherId, day);
    }

}
