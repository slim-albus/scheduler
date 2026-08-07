package app.scheduler.services;

import app.scheduler.services.LoggerService;

import app.scheduler.models.Course;
import app.scheduler.repositories.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {
    private final LoggerService loggerService;

    private final CourseRepository repository;

    public CourseService(CourseRepository repository, LoggerService loggerService) {
        this.loggerService = loggerService;
        this.repository = repository;
    }

    public List<Course> findAll() {
        return repository.findAll();
    }

    public Optional<Course> findById(String id) {
        return repository.findById(id);
    }

    public Course save(Course entity) {
        loggerService.logSystem("Creating new Course...");
        var saved = repository.save(entity);
        loggerService.logSystem("Successfully saved Course.");
        return saved;
    }

    public boolean update(Course entity) {
        loggerService.logSystem("Updating Course...");
        boolean updated = repository.update(entity);
        loggerService.logSystem(updated ? "Successfully updated Course." : "Failed to update Course.");
        return updated;
    }

    public boolean delete(String id) {
        loggerService.logSystem("Deleting Course...");
        boolean deleted = repository.delete(id);
        loggerService.logSystem(deleted ? "Successfully deleted Course." : "Failed to delete Course.");
        return deleted;
    }
}
