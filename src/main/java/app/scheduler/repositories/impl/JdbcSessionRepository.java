package app.scheduler.repositories.impl;

import app.scheduler.models.Session;
import app.scheduler.repositories.SessionRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import app.scheduler.utils.SQLQueries;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import app.scheduler.utils.DateUtils;

@Repository
public class JdbcSessionRepository implements SessionRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcSessionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Session> rowMapper = (rs, rowNum) -> {
        Session session = new Session();
        session.setId(rs.getString("id"));
        session.setToken(rs.getString("token"));
        session.setUserId(rs.getString("user_id"));
        session.setIpAddress(rs.getString("ip_address"));
        session.setUserAgent(rs.getString("user_agent"));
        session.setActive(rs.getBoolean("is_active"));
        session.setCreatedAt(DateUtils.parseSqliteTimestamp(rs.getString("created_at")));
        session.setExpiresAt(DateUtils.parseSqliteTimestamp(rs.getString("expires_at")));
        return session;
    };

    @Override
    public Session save(Session entity) {
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID().toString());
        }
        jdbcTemplate.update(
            "INSERT INTO sessions (id, token, user_id, ip_address, user_agent, expires_at, is_active) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)",
            entity.getId(), entity.getToken(), entity.getUserId(), entity.getIpAddress(),
            entity.getUserAgent(), 
            DateUtils.formatSqliteTimestamp(entity.getExpiresAt()), 
            entity.isActive()
        );
        return entity;
    }

    @Override
    public Optional<Session> findById(String id) {
        List<Session> results = jdbcTemplate.query(SQLQueries.SESSION_FIND_BY_ID, rowMapper, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public List<Session> findAll() {
        return jdbcTemplate.query(SQLQueries.SESSION_FIND_ALL, rowMapper);
    }

    @Override
    public boolean delete(String id) {
        return jdbcTemplate.update(SQLQueries.SESSION_DELETE, id) > 0;
    }

    @Override
    public boolean update(Session entity) {
        return jdbcTemplate.update(
            "UPDATE sessions SET token = ?, user_id = ?, ip_address = ?, user_agent = ?, " +
            "expires_at = ?, is_active = ? WHERE id = ?",
            entity.getToken(), entity.getUserId(), entity.getIpAddress(), entity.getUserAgent(),
            DateUtils.formatSqliteTimestamp(entity.getExpiresAt()), 
            entity.isActive(), entity.getId()
        ) > 0;
    }

    @Override
    public Optional<Session> findByToken(String token) {
        List<Session> results = jdbcTemplate.query(SQLQueries.SESSION_FIND_BY_TOKEN, rowMapper, token);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
}
