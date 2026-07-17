package app.scheduler.repositories;

import app.scheduler.models.Batch;
import java.util.List;
import java.util.Optional;

public interface BatchRepository extends Repository<Batch> {
    List<Batch> findBySemesterId(String semesterId);
    List<Batch> findByProgram(String program);
}
