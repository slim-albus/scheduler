package app.scheduler.repositories;

import app.scheduler.models.Section;
import java.util.List;
import java.util.Optional;

public interface SectionRepository extends Repository<Section> {
    List<Section> findByBatchId(String batchId);
    List<Section> findBySemesterId(String semesterId);
}
