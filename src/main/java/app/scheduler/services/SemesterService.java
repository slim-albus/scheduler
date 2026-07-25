package app.scheduler.services;

import app.scheduler.services.LoggerService;

import app.scheduler.models.Semester;
import app.scheduler.repositories.SemesterRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SemesterService {
    private final LoggerService loggerService;

    private final SemesterRepository repository;

    public SemesterService(SemesterRepository repository, LoggerService loggerService) {
        this.loggerService = loggerService;
        this.repository = repository;
    }

    public List<Semester> findAll() {
        return repository.findAll();
    }

    public Optional<Semester> findById(String id) {
        return repository.findById(id);
    }

    public Semester save(Semester entity) {
        loggerService.logSystem("Saving entity in SemesterService");
        adjustDates(entity);
        return repository.save(entity);
    }

    public boolean update(Semester entity) {
        loggerService.logSystem("Updating entity in SemesterService");
        adjustDates(entity);
        return repository.update(entity);
    }

    private void adjustDates(Semester entity) {
        if (entity.getStartDate() != null && entity.getWeeks() > 0) {
            java.time.LocalDate start = entity.getStartDate();
            while (start.getDayOfWeek() != java.time.DayOfWeek.MONDAY) {
                start = start.minusDays(1);
            }
            entity.setStartDate(start);
            // End date is Sunday of the final week
            
        }
    }

    public boolean setActive(String id) {
        return repository.setActive(id);
    }

    public boolean delete(String id) {
        return repository.delete(id);
    }
}
