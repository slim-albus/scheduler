package app.scheduler.services;

import app.scheduler.services.LoggerService;

import app.scheduler.models.BatchCourseMapping;
import app.scheduler.repositories.BatchCourseMappingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BatchCourseMappingService {
    private final LoggerService loggerService;

    private final BatchCourseMappingRepository repository;

    public BatchCourseMappingService(BatchCourseMappingRepository repository, LoggerService loggerService) {
        this.loggerService = loggerService;
        this.repository = repository;
    }

    public List<BatchCourseMapping> findAll() {
        return repository.findAll();
    }

    public Optional<BatchCourseMapping> findById(String id) {
        return repository.findById(id);
    }

    public BatchCourseMapping save(BatchCourseMapping entity) {
        loggerService.logSystem("Creating new BatchCourseMapping...");
        var saved = repository.save(entity);
        loggerService.logSystem("Successfully saved BatchCourseMapping.");
        return saved;
    }

    public List<BatchCourseMapping> saveAll(List<BatchCourseMapping> entities) {
        return entities.stream().map(repository::save).toList();
    }

    public boolean update(BatchCourseMapping entity) {
        loggerService.logSystem("Updating BatchCourseMapping...");
        boolean updated = repository.update(entity);
        loggerService.logSystem(updated ? "Successfully updated BatchCourseMapping." : "Failed to update BatchCourseMapping.");
        return updated;
    }

    public boolean delete(String id) {
        loggerService.logSystem("Deleting BatchCourseMapping...");
        boolean deleted = repository.delete(id);
        loggerService.logSystem(deleted ? "Successfully deleted BatchCourseMapping." : "Failed to delete BatchCourseMapping.");
        return deleted;
    }
    
    public List<BatchCourseMapping> findBySemesterId(String semesterId) {
        return repository.findBySemesterId(semesterId);
    }
}
