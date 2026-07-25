package app.scheduler.controllers.admin;

import app.scheduler.models.Batch;
import app.scheduler.models.Section;
import app.scheduler.services.BatchService;
import app.scheduler.services.LoggerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class BatchController {
    private final BatchService service;
    private final LoggerService loggerService;

    public BatchController(BatchService service, LoggerService loggerService) {
        this.service = service;
        this.loggerService = loggerService;
    }

    // --- BATCH ENDPOINTS ---

    @GetMapping("/batches")
    public ResponseEntity<List<Batch>> getAllBatches() {
        loggerService.logAdmin("Admin fetched all batches");
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/batches/{id}")
    public ResponseEntity<Batch> getBatchById(@PathVariable String id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/batches")
    public ResponseEntity<Batch> createBatch(@RequestBody Batch entity) {
        loggerService.logAdmin("Received POST request in BatchController");
        loggerService.logAdmin("Admin created Batch");
        return ResponseEntity.ok(service.save(entity));
    }

    @PutMapping("/batches/{id}")
    public ResponseEntity<Batch> updateBatch(@PathVariable String id, @RequestBody Batch entity) {
        loggerService.logAdmin("Received PUT request in BatchController");
        entity.setId(id);
        if (service.update(entity)) {
            loggerService.logAdmin("Admin updated Batch " + id);
            return ResponseEntity.ok(entity);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/batches/{id}")
    public ResponseEntity<Void> deleteBatch(@PathVariable String id) {
        loggerService.logAdmin("Received DELETE request in BatchController");
        if (service.delete(id)) {
            loggerService.logAdmin("Admin deleted Batch " + id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // --- SECTION ENDPOINTS ---

    @GetMapping("/sections")
    public ResponseEntity<List<Section>> getAllSections() {
        loggerService.logAdmin("Admin fetched all sections");
        return ResponseEntity.ok(service.findAllSections());
    }

    @GetMapping("/sections/{id}")
    public ResponseEntity<Section> getSectionById(@PathVariable String id) {
        return service.findSectionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/sections")
    public ResponseEntity<Section> createSection(@RequestBody Section entity) {
        loggerService.logAdmin("Received POST request in BatchController");
        loggerService.logAdmin("Admin created Section");
        return ResponseEntity.ok(service.saveSection(entity));
    }

    @PutMapping("/sections/{id}")
    public ResponseEntity<Section> updateSection(@PathVariable String id, @RequestBody Section entity) {
        loggerService.logAdmin("Received PUT request in BatchController");
        entity.setId(id);
        if (service.updateSection(entity)) {
            loggerService.logAdmin("Admin updated Section " + id);
            return ResponseEntity.ok(entity);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/sections/{id}")
    public ResponseEntity<Void> deleteSection(@PathVariable String id) {
        loggerService.logAdmin("Received DELETE request in BatchController");
        if (service.deleteSection(id)) {
            loggerService.logAdmin("Admin deleted Section " + id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
