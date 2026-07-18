package app.scheduler.services;

import app.scheduler.models.Batch;
import app.scheduler.repositories.BatchRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BatchService {
    private final BatchRepository repository;

    public BatchService(BatchRepository repository) {
        this.repository = repository;
    }

    public List<Batch> findAll() {
        return repository.findAll();
    }

    public Optional<Batch> findById(String id) {
        return repository.findById(id);
    }

    public Batch save(Batch entity) {
        return repository.save(entity);
    }

    public boolean update(Batch entity) {
        return repository.update(entity);
    }

    public boolean delete(String id) {
        return repository.delete(id);
    }
}
