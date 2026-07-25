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
        loggerService.logSystem("Saving entity in BatchService");
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
        return saved;
    }

    public boolean update(Batch entity) {
        loggerService.logSystem("Updating entity in BatchService");
        return repository.update(entity);
    }

    public boolean delete(String id) {
        return repository.delete(id);
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
        loggerService.logSystem("Saving entity in BatchService");
        return repository.saveSection(entity);
    }

    public boolean updateSection(Section entity) {
        loggerService.logSystem("Updating entity in BatchService");
        return repository.updateSection(entity);
    }

    public boolean deleteSection(String id) {
        return repository.deleteSection(id);
    }
}
