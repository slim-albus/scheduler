package app.scheduler.services;

import app.scheduler.models.Batch;
import app.scheduler.models.Section;
import app.scheduler.repositories.BatchRepository;
import app.scheduler.repositories.SectionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BatchService {
    private final BatchRepository repository;
    private final SectionRepository sectionRepository;

    public BatchService(BatchRepository repository, SectionRepository sectionRepository) {
        this.repository = repository;
        this.sectionRepository = sectionRepository;
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
                section.setActive(true);
                sectionRepository.save(section);
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
}
