package app.scheduler.controllers.admin;

import app.scheduler.models.Course;
import app.scheduler.services.CourseService;
import app.scheduler.services.LoggerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/courses")
public class CourseController {
    private final CourseService service;
    private final LoggerService loggerService;

    public CourseController(CourseService service, LoggerService loggerService) {
        this.service = service;
        this.loggerService = loggerService;
    }

    @GetMapping
    public ResponseEntity<List<Course>> getAll() {
        loggerService.logAdmin("Admin fetched all courses");
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Course> getById(@PathVariable String id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Course> create(@RequestBody Course entity) {
        loggerService.logAdmin("Admin created Course");
        return ResponseEntity.ok(service.save(entity));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Course> update(@PathVariable String id, @RequestBody Course entity) {
        entity.setId(id);
        if (service.update(entity)) {
            loggerService.logAdmin("Admin updated Course " + id);
            return ResponseEntity.ok(entity);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (service.delete(id)) {
            loggerService.logAdmin("Admin deleted Course " + id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
