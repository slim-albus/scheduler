package app.scheduler.controllers.admin;

import app.scheduler.models.Semester;
import app.scheduler.services.SemesterService;
import app.scheduler.services.LoggerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/semesters")
public class SemesterController {
    private final SemesterService service;
    private final LoggerService loggerService;

    public SemesterController(SemesterService service, LoggerService loggerService) {
        this.service = service;
        this.loggerService = loggerService;
    }

    @GetMapping
    public ResponseEntity<List<Semester>> getAll() {
        loggerService.logAdmin("Admin fetched all semesters");
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Semester> getById(@PathVariable String id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Semester> create(@RequestBody Semester entity) {
        loggerService.logAdmin("Admin created Semester");
        return ResponseEntity.ok(service.save(entity));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Semester> update(@PathVariable String id, @RequestBody Semester entity) {
        entity.setId(id);
        if (service.update(entity)) {
            loggerService.logAdmin("Admin updated Semester " + id);
            return ResponseEntity.ok(entity);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/active")
    public ResponseEntity<Void> setActive(@PathVariable String id) {
        if (service.setActive(id)) {
            loggerService.logAdmin("Admin set active semester to " + id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (service.delete(id)) {
            loggerService.logAdmin("Admin deleted Semester " + id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
