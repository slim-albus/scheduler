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
        loggerService.logSystem("Saving entity in StudentService");
        Student saved = repository.save(entity);
        authService.autoRegister(saved.getStudentId(), "STUDENT", null, saved.getId());
        return saved;
    }

    public boolean update(Student entity) {
        loggerService.logSystem("Updating entity in StudentService");
        return repository.update(entity);
    }

    public boolean delete(String id) {
        return repository.delete(id);
    }
}
