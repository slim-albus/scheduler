package app.scheduler.services;

import app.scheduler.services.LoggerService;

import app.scheduler.models.Batch;
import app.scheduler.models.Section;
import app.scheduler.repositories.BatchRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BatchService {
    private final LoggerService loggerService;

    private final BatchRepository repository;

    public BatchService(BatchRepository repository, LoggerService loggerService) {
        this.loggerService = loggerService;
        this.repository = repository;
    }

    public List<Batch> findAll() {
        return repository.findAll();
    }

    public Optional<Batch> findById(String id) {
        return repository.findById(id);
    }

    public Batch save(Batch entity) {
        loggerService.logSystem("Creating new Batch...");
        Batch saved = repository.save(entity);
        if (entity.getSectionCount() > 0) {
            for (int i = 0; i < entity.getSectionCount(); i++) {
                Section section = new Section();
                section.setName(saved.getName() + " - Section " + (char)('A' + i));
                section.setBatchId(saved.getId());
                section.setStudentCount(35);
                repository.saveSection(section);
            }
        }
        loggerService.logSystem("Successfully saved Batch.");
        return saved;
    }

    public boolean update(Batch entity) {
        loggerService.logSystem("Updating Batch...");
        boolean updated = repository.update(entity);
        loggerService.logSystem(updated ? "Successfully updated Batch." : "Failed to update Batch.");
        return updated;
    }

    public boolean delete(String id) {
        loggerService.logSystem("Deleting Batch...");
        boolean deleted = repository.delete(id);
        loggerService.logSystem(deleted ? "Successfully deleted Batch." : "Failed to delete Batch.");
        return deleted;
    }

    // --- Section related methods ---

    public List<Section> findAllSections() {
        return repository.findAllSections();
    }

    public List<Section> findSectionsByBatchId(String batchId) {
        return repository.findSectionsByBatchId(batchId);
    }

    public List<Section> findSectionsBySemesterId(String semesterId) {
        return repository.findSectionsBySemesterId(semesterId);
    }

    public Optional<Section> findSectionById(String id) {
        return repository.findSectionById(id);
    }

    public Section saveSection(Section entity) {
        loggerService.logSystem("Creating new Batch...");
        var saved = repository.saveSection(entity);
        loggerService.logSystem("Successfully saved Section.");
        return saved;
    }

    public boolean updateSection(Section entity) {
        loggerService.logSystem("Updating Batch...");
        boolean updated = repository.updateSection(entity);
        loggerService.logSystem(updated ? "Successfully updated Section." : "Failed to update Section.");
        return updated;
    }

    public boolean deleteSection(String id) {
        loggerService.logSystem("Deleting Section...");
        boolean deleted = repository.deleteSection(id);
        loggerService.logSystem(deleted ? "Successfully deleted Section." : "Failed to delete Section.");
        return deleted;
    }
}
