package app.scheduler.controllers.admin;

import app.scheduler.models.Batch;
import app.scheduler.services.BatchService;
import app.scheduler.services.LoggerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/batches")
public class BatchController {
    private final BatchService service;
    private final LoggerService loggerService;

    public BatchController(BatchService service, LoggerService loggerService) {
        this.service = service;
        this.loggerService = loggerService;
    }

    @GetMapping
    public ResponseEntity<List<Batch>> getAll() {
        loggerService.logAdmin("Admin fetched all batches");
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Batch> getById(@PathVariable String id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Batch> create(@RequestBody Batch entity) {
        loggerService.logAdmin("Admin created Batch");
        return ResponseEntity.ok(service.save(entity));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Batch> update(@PathVariable String id, @RequestBody Batch entity) {
        entity.setId(id);
        if (service.update(entity)) {
            loggerService.logAdmin("Admin updated Batch " + id);
            return ResponseEntity.ok(entity);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (service.delete(id)) {
            loggerService.logAdmin("Admin deleted Batch " + id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
