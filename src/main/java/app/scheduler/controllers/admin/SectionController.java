package app.scheduler.controllers.admin;

import app.scheduler.models.Section;
import app.scheduler.services.SectionService;
import app.scheduler.services.LoggerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/sections")
public class SectionController {
    private final SectionService service;
    private final LoggerService loggerService;

    public SectionController(SectionService service, LoggerService loggerService) {
        this.service = service;
        this.loggerService = loggerService;
    }

    @GetMapping
    public ResponseEntity<List<Section>> getAll() {
        loggerService.logAdmin("Admin fetched all sections");
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Section> getById(@PathVariable String id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Section> create(@RequestBody Section entity) {
        loggerService.logAdmin("Admin created Section");
        return ResponseEntity.ok(service.save(entity));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Section> update(@PathVariable String id, @RequestBody Section entity) {
        entity.setId(id);
        if (service.update(entity)) {
            loggerService.logAdmin("Admin updated Section " + id);
            return ResponseEntity.ok(entity);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (service.delete(id)) {
            loggerService.logAdmin("Admin deleted Section " + id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
