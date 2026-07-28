package app.scheduler.controllers.admin;

import app.scheduler.models.BatchCourseMapping;
import app.scheduler.services.BatchCourseMappingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/mappings")
public class BatchCourseMappingController {
    private final BatchCourseMappingService service;

    public BatchCourseMappingController(BatchCourseMappingService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<BatchCourseMapping>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BatchCourseMapping> getById(@PathVariable("id") String id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<BatchCourseMapping> create(@RequestBody BatchCourseMapping entity) {
        return ResponseEntity.ok(service.save(entity));
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<BatchCourseMapping>> createBulk(@RequestBody List<BatchCourseMapping> entities) {
        return ResponseEntity.ok(service.saveAll(entities));
    }

    @GetMapping("/semester/{semesterId}")
    public ResponseEntity<List<BatchCourseMapping>> getBySemester(@PathVariable("semesterId") String semesterId) {
        return ResponseEntity.ok(service.findBySemesterId(semesterId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BatchCourseMapping> update(@PathVariable("id") String id, @RequestBody BatchCourseMapping entity) {
        entity.setId(id);
        if (service.update(entity)) {
            return ResponseEntity.ok(entity);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String id) {
        if (service.delete(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
