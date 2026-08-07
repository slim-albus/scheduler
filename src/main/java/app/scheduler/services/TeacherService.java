package app.scheduler.services;

import app.scheduler.services.LoggerService;

import app.scheduler.models.Teacher;
import app.scheduler.repositories.TeacherRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TeacherService {
    private final LoggerService loggerService;

    private final TeacherRepository repository;
    private final AuthService authService;

    public TeacherService(TeacherRepository repository, AuthService authService, LoggerService loggerService) {
        this.loggerService = loggerService;
        this.repository = repository;
        this.authService = authService;
    }

    public List<Teacher> findAll() {
        return repository.findAll();
    }

    public Optional<Teacher> findById(String id) {
        return repository.findById(id);
    }

    public Teacher save(Teacher entity) {
        loggerService.logSystem("Creating new Teacher...");
        Teacher saved = repository.save(entity);
        authService.autoRegister(saved.getEmail(), "TEACHER", saved.getId(), null);
        loggerService.logSystem("Successfully saved Teacher.");
        return saved;
    }

    public boolean update(Teacher entity) {
        loggerService.logSystem("Updating Teacher...");
        boolean updated = repository.update(entity);
        loggerService.logSystem(updated ? "Successfully updated Teacher." : "Failed to update Teacher.");
        return updated;
    }

    public boolean delete(String id) {
        loggerService.logSystem("Deleting Teacher...");
        boolean deleted = repository.delete(id);
        loggerService.logSystem(deleted ? "Successfully deleted Teacher." : "Failed to delete Teacher.");
        return deleted;
    }
}
