package app.scheduler.services;

import app.scheduler.models.Section;
import app.scheduler.repositories.SectionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SectionService {
    private final SectionRepository repository;

    public SectionService(SectionRepository repository) {
        this.repository = repository;
    }

    public List<Section> findAll() {
        return repository.findAll();
    }

    public Optional<Section> findById(String id) {
        return repository.findById(id);
    }

    public Section save(Section entity) {
        return repository.save(entity);
    }

    public boolean update(Section entity) {
        return repository.update(entity);
    }

    public boolean delete(String id) {
        return repository.delete(id);
    }
}
