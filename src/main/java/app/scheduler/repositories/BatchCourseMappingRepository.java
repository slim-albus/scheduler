package app.scheduler.repositories;

import app.scheduler.models.BatchCourseMapping;
import java.util.List;
import java.util.Optional;

public interface BatchCourseMappingRepository extends Repository<BatchCourseMapping> {
    List<BatchCourseMapping> findByBatchId(String batchId);
    List<BatchCourseMapping> findBySemesterId(String semesterId);
    Optional<BatchCourseMapping> findByBatchIdAndCourseId(String batchId, String courseId);
}
