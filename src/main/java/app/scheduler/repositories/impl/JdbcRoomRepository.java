package app.scheduler.repositories.impl;

import app.scheduler.models.Room;
import app.scheduler.repositories.RoomRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import app.scheduler.utils.SQLQueries;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JdbcRoomRepository implements RoomRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcRoomRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Room> rowMapper = (rs, rowNum) -> {
        Room obj = new Room();
        obj.setId(rs.getString("id"));
        obj.setName(rs.getString("name"));
        obj.setType(rs.getString("type"));
        obj.setCapacity(rs.getInt("capacity"));
        obj.setLevel(rs.getInt("level"));
        obj.setHasEquipment(rs.getBoolean("has_equipment"));
        return obj;
    };

    @Override
    public Room save(Room entity) {
        if (entity.getId() == null || entity.getId().isEmpty()) {
            entity.setId(UUID.randomUUID().toString());
            jdbcTemplate.update(
                SQLQueries.ROOM_INSERT,
                entity.getId(), entity.getName(), entity.getType(), entity.getCapacity(), entity.getLevel(), entity.isHasEquipment()
            );
        } else {
            update(entity);
        }
        return entity;
    }

    @Override
    public Optional<Room> findById(String id) {
        List<Room> results = jdbcTemplate.query(SQLQueries.ROOM_FIND_BY_ID, rowMapper, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public List<Room> findAll() {
        return jdbcTemplate.query(SQLQueries.ROOM_FIND_ALL, rowMapper);
    }

    @Override
    public boolean delete(String id) {
        return jdbcTemplate.update(SQLQueries.ROOM_DELETE, id) > 0;
    }

    @Override
    public boolean update(Room entity) {
        return jdbcTemplate.update(
            SQLQueries.ROOM_UPDATE,
            entity.getName(), entity.getType(), entity.getCapacity(), entity.getLevel(), entity.isHasEquipment(), entity.getId()
        ) > 0;
    }

}
