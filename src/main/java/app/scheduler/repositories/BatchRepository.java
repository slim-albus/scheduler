package app.scheduler.repositories;

import app.scheduler.models.Batch;
import java.util.List;
import java.util.Optional;

import app.scheduler.models.Section;

public interface BatchRepository extends Repository<Batch> {
    List<Batch> findBySemesterId(String semesterId);
    List<Batch> findByProgram(String program);
    
    // Section related methods moved from SectionRepository
    List<Section> findSectionsByBatchId(String batchId);
    List<Section> findSectionsBySemesterId(String semesterId);
    List<Section> findAllSections();
    Section saveSection(Section entity);
    Optional<Section> findSectionById(String id);
    boolean updateSection(Section entity);
    boolean deleteSection(String id);
}
