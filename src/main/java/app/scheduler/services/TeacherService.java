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
        loggerService.logSystem("Saving entity in TeacherService");
        Teacher saved = repository.save(entity);
        authService.autoRegister(saved.getEmail(), "TEACHER", saved.getId(), null);
        return saved;
    }

    public boolean update(Teacher entity) {
        loggerService.logSystem("Updating entity in TeacherService");
        return repository.update(entity);
    }

    public boolean delete(String id) {
        return repository.delete(id);
    }
}
