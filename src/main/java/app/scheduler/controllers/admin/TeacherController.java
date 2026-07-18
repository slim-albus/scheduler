package app.scheduler.controllers.admin;

import app.scheduler.models.Teacher;
import app.scheduler.services.TeacherService;
import app.scheduler.services.LoggerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/teachers")
public class TeacherController {
    private final TeacherService service;
    private final LoggerService loggerService;

    public TeacherController(TeacherService service, LoggerService loggerService) {
        this.service = service;
        this.loggerService = loggerService;
    }

    @GetMapping
    public ResponseEntity<List<Teacher>> getAll() {
        loggerService.logAdmin("Admin fetched all teachers");
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Teacher> getById(@PathVariable String id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Teacher> create(@RequestBody Teacher entity) {
        loggerService.logAdmin("Admin created Teacher");
        return ResponseEntity.ok(service.save(entity));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Teacher> update(@PathVariable String id, @RequestBody Teacher entity) {
        entity.setId(id);
        if (service.update(entity)) {
            loggerService.logAdmin("Admin updated Teacher " + id);
            return ResponseEntity.ok(entity);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (service.delete(id)) {
            loggerService.logAdmin("Admin deleted Teacher " + id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
