package app.scheduler.services;

import app.scheduler.models.Student;
import app.scheduler.repositories.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {
    private final StudentRepository repository;

    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }

    public List<Student> findAll() {
        return repository.findAll();
    }

    public Optional<Student> findById(String id) {
        return repository.findById(id);
    }

    public Student save(Student entity) {
        return repository.save(entity);
    }

    public boolean update(Student entity) {
        return repository.update(entity);
    }

    public boolean delete(String id) {
        return repository.delete(id);
    }
}
