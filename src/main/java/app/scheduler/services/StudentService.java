package app.scheduler.services;

import app.scheduler.services.LoggerService;

import app.scheduler.models.Student;
import app.scheduler.repositories.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {
    private final LoggerService loggerService;

    private final StudentRepository repository;
    private final AuthService authService;

    public StudentService(StudentRepository repository, AuthService authService, LoggerService loggerService) {
        this.loggerService = loggerService;
        this.repository = repository;
        this.authService = authService;
    }

    public List<Student> findAll() {
        return repository.findAll();
    }

    public Optional<Student> findById(String id) {
        return repository.findById(id);
    }

    public Student save(Student entity) {
        loggerService.logSystem("Creating new Student...");
        Student saved = repository.save(entity);
        authService.autoRegister(saved.getStudentId(), "STUDENT", null, saved.getId());
        loggerService.logSystem("Successfully saved Student.");
        return saved;
    }

    public boolean update(Student entity) {
        loggerService.logSystem("Updating Student...");
        boolean updated = repository.update(entity);
        loggerService.logSystem(updated ? "Successfully updated Student." : "Failed to update Student.");
        return updated;
    }

    public boolean delete(String id) {
        loggerService.logSystem("Deleting Student...");
        boolean deleted = repository.delete(id);
        loggerService.logSystem(deleted ? "Successfully deleted Student." : "Failed to delete Student.");
        return deleted;
    }
}
