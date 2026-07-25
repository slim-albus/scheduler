package app.scheduler.controllers.admin;

import app.scheduler.models.BatchCourseMapping;
import app.scheduler.services.BatchCourseMappingService;
import app.scheduler.services.LoggerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/mappings")
public class BatchCourseMappingController {
    private final BatchCourseMappingService service;
    private final LoggerService loggerService;

    public BatchCourseMappingController(BatchCourseMappingService service, LoggerService loggerService) {
        this.service = service;
        this.loggerService = loggerService;
    }

    @GetMapping
    public ResponseEntity<List<BatchCourseMapping>> getAll() {
        loggerService.logAdmin("Admin fetched all mappings");
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BatchCourseMapping> getById(@PathVariable String id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<BatchCourseMapping> create(@RequestBody BatchCourseMapping entity) {
        loggerService.logAdmin("Admin created BatchCourseMapping");
        return ResponseEntity.ok(service.save(entity));
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<BatchCourseMapping>> createBulk(@RequestBody List<BatchCourseMapping> entities) {
        loggerService.logAdmin("Received POST request in BatchCourseMappingController");
        loggerService.logAdmin("Admin created bulk BatchCourseMappings");
        return ResponseEntity.ok(service.saveAll(entities));
    }

    @GetMapping("/semester/{semesterId}")
    public ResponseEntity<List<BatchCourseMapping>> getBySemester(@PathVariable String semesterId) {
        return ResponseEntity.ok(service.findBySemesterId(semesterId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BatchCourseMapping> update(@PathVariable String id, @RequestBody BatchCourseMapping entity) {
        loggerService.logAdmin("Received PUT request in BatchCourseMappingController");
        entity.setId(id);
        if (service.update(entity)) {
            loggerService.logAdmin("Admin updated BatchCourseMapping " + id);
            return ResponseEntity.ok(entity);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        loggerService.logAdmin("Received DELETE request in BatchCourseMappingController");
        if (service.delete(id)) {
            loggerService.logAdmin("Admin deleted BatchCourseMapping " + id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
