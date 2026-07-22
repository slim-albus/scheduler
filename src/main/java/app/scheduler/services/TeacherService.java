package app.scheduler.services;

import app.scheduler.models.Teacher;
import app.scheduler.repositories.TeacherRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TeacherService {
    private final TeacherRepository repository;
    private final AuthService authService;

    public TeacherService(TeacherRepository repository, AuthService authService) {
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
        Teacher saved = repository.save(entity);
        authService.autoRegister(saved.getId(), "TEACHER", saved.getId(), null);
        return saved;
    }

    public boolean update(Teacher entity) {
        return repository.update(entity);
    }

    public boolean delete(String id) {
        return repository.delete(id);
    }
}
