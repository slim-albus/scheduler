package app.scheduler.services;

import app.scheduler.models.Semester;
import app.scheduler.repositories.SemesterRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SemesterService {
    private final SemesterRepository repository;

    public SemesterService(SemesterRepository repository) {
        this.repository = repository;
    }

    public List<Semester> findAll() {
        return repository.findAll();
    }

    public Optional<Semester> findById(String id) {
        return repository.findById(id);
    }

    public Semester save(Semester entity) {
        return repository.save(entity);
    }

    public boolean update(Semester entity) {
        return repository.update(entity);
    }

    public boolean delete(String id) {
        return repository.delete(id);
    }
}
