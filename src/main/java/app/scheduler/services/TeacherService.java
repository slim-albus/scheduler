package app.scheduler.services;

import app.scheduler.models.Teacher;
import app.scheduler.repositories.TeacherRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TeacherService {
    private final TeacherRepository repository;

    public TeacherService(TeacherRepository repository) {
        this.repository = repository;
    }

    public List<Teacher> findAll() {
        return repository.findAll();
    }

    public Optional<Teacher> findById(String id) {
        return repository.findById(id);
    }

    public Teacher save(Teacher entity) {
        return repository.save(entity);
    }

    public boolean update(Teacher entity) {
        return repository.update(entity);
    }

    public boolean delete(String id) {
        return repository.delete(id);
    }
}
