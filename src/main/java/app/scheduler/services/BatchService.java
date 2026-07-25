package app.scheduler.services;

import app.scheduler.models.Batch;
import app.scheduler.models.Section;
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
        return repository.saveSection(entity);
    }

    public boolean updateSection(Section entity) {
        return repository.updateSection(entity);
    }

    public boolean deleteSection(String id) {
        return repository.deleteSection(id);
    }
}
