package app.scheduler.services;

import app.scheduler.models.BatchCourseMapping;
import app.scheduler.repositories.BatchCourseMappingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BatchCourseMappingService {
    private final BatchCourseMappingRepository repository;

    public BatchCourseMappingService(BatchCourseMappingRepository repository) {
        this.repository = repository;
    }

    public List<BatchCourseMapping> findAll() {
        return repository.findAll();
    }

    public Optional<BatchCourseMapping> findById(String id) {
        return repository.findById(id);
    }

    public BatchCourseMapping save(BatchCourseMapping entity) {
        return repository.save(entity);
    }

    public boolean update(BatchCourseMapping entity) {
        return repository.update(entity);
    }

    public boolean delete(String id) {
        return repository.delete(id);
    }
}
