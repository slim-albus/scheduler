package app.scheduler.controllers.admin;

import app.scheduler.models.Student;
import app.scheduler.services.StudentService;
import app.scheduler.services.LoggerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/students")
public class StudentController {
    private final StudentService service;
    private final LoggerService loggerService;

    public StudentController(StudentService service, LoggerService loggerService) {
        this.service = service;
        this.loggerService = loggerService;
    }

    @GetMapping
    public ResponseEntity<List<Student>> getAll() {
        loggerService.logAdmin("Admin fetched all students");
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getById(@PathVariable("id") String id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Student> create(@RequestBody Student entity) {
        loggerService.logAdmin("Admin created Student");
        return ResponseEntity.ok(service.save(entity));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Student> update(@PathVariable("id") String id, @RequestBody Student entity) {
        loggerService.logAdmin("Received PUT request in StudentController");
        entity.setId(id);
        if (service.update(entity)) {
            loggerService.logAdmin("Admin updated Student " + id);
            return ResponseEntity.ok(entity);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String id) {
        loggerService.logAdmin("Received DELETE request in StudentController");
        if (service.delete(id)) {
            loggerService.logAdmin("Admin deleted Student " + id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
