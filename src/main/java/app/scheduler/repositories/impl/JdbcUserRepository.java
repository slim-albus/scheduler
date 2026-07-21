package app.scheduler.repositories.impl;

import app.scheduler.models.User;
import app.scheduler.repositories.UserRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import app.scheduler.utils.SQLQueries;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import app.scheduler.utils.DateUtils;

@Repository
public class JdbcUserRepository implements UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcUserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<User> rowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getString("id"));
        user.setUserId(rs.getString("user_id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setSalt(rs.getString("salt"));
        user.setRole(rs.getString("role"));
        user.setTeacherId(rs.getString("teacher_id"));
        user.setStudentId(rs.getString("student_id"));
        user.setActive(rs.getBoolean("is_active"));
        user.setCreatedAt(DateUtils.parseSqliteTimestamp(rs.getString("created_at")));
        user.setUpdatedAt(DateUtils.parseSqliteTimestamp(rs.getString("updated_at")));
        return user;
    };

    @Override
    public User save(User entity) {
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID().toString());
        }
        jdbcTemplate.update(
            "INSERT INTO users (id, user_id, username, email, password_hash, salt, role, teacher_id, student_id, is_active) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            entity.getId(), entity.getUserId(), entity.getUsername(), entity.getEmail(),
            entity.getPasswordHash(), entity.getSalt(), entity.getRole(),
            entity.getTeacherId(), entity.getStudentId(), entity.isActive()
        );
        return entity;
    }

    @Override
    public Optional<User> findById(String id) {
        List<User> results = jdbcTemplate.query(SQLQueries.USER_FIND_BY_ID, rowMapper, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public List<User> findAll() {
        return jdbcTemplate.query(SQLQueries.USER_FIND_ALL, rowMapper);
    }

    @Override
    public boolean delete(String id) {
        return jdbcTemplate.update(SQLQueries.USER_DELETE, id) > 0;
    }

    @Override
    public boolean update(User entity) {
        return jdbcTemplate.update(
            "UPDATE users SET user_id = ?, username = ?, email = ?, password_hash = ?, salt = ?, " +
            "role = ?, teacher_id = ?, student_id = ?, is_active = ? WHERE id = ?",
            entity.getUserId(), entity.getUsername(), entity.getEmail(),
            entity.getPasswordHash(), entity.getSalt(), entity.getRole(),
            entity.getTeacherId(), entity.getStudentId(), entity.isActive(), entity.getId()
        ) > 0;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        List<User> results = jdbcTemplate.query(SQLQueries.USER_FIND_BY_USERNAME, rowMapper, username);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
}
