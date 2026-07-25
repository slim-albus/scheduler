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
        loggerService.logSystem("Saving entity in CourseService");
        return repository.save(entity);
    }

    public boolean update(Course entity) {
        loggerService.logSystem("Updating entity in CourseService");
        return repository.update(entity);
    }

    public boolean delete(String id) {
        return repository.delete(id);
    }
}
