package app.scheduler.services;

import app.scheduler.services.LoggerService;

import app.scheduler.models.Room;
import app.scheduler.repositories.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoomService {
    private final LoggerService loggerService;

    private final RoomRepository repository;

    public RoomService(RoomRepository repository, LoggerService loggerService) {
        this.loggerService = loggerService;
        this.repository = repository;
    }

    public List<Room> findAll() {
        return repository.findAll();
    }

    public Optional<Room> findById(String id) {
        return repository.findById(id);
    }

    public Room save(Room entity) {
        loggerService.logSystem("Creating new Room...");
        var saved = repository.save(entity);
        loggerService.logSystem("Successfully saved Room.");
        return saved;
    }

    public boolean update(Room entity) {
        loggerService.logSystem("Updating Room...");
        boolean updated = repository.update(entity);
        loggerService.logSystem(updated ? "Successfully updated Room." : "Failed to update Room.");
        return updated;
    }

    public boolean delete(String id) {
        loggerService.logSystem("Deleting Room...");
        boolean deleted = repository.delete(id);
        loggerService.logSystem(deleted ? "Successfully deleted Room." : "Failed to delete Room.");
        return deleted;
    }
}
