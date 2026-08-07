package app.scheduler.repositories;

import app.scheduler.models.Semester;
import java.util.List;
import java.util.Optional;

public interface SemesterRepository extends Repository<Semester> {
    Optional<Semester> findByCode(String code);
    List<Semester> findByYear(String year);
    Optional<Semester> findActive();
    boolean setActive(String id);
}
