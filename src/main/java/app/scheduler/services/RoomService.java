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
        loggerService.logSystem("Saving entity in RoomService");
        return repository.save(entity);
    }

    public boolean update(Room entity) {
        loggerService.logSystem("Updating entity in RoomService");
        return repository.update(entity);
    }

    public boolean delete(String id) {
        return repository.delete(id);
    }
}
