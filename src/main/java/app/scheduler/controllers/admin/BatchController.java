package app.scheduler.controllers.admin;

import app.scheduler.models.Batch;
import app.scheduler.models.Section;
import app.scheduler.services.BatchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class BatchController {
    private final BatchService service;

    public BatchController(BatchService service) {
        this.service = service;
    }

    // --- BATCH ENDPOINTS ---

    @GetMapping("/batches")
    public ResponseEntity<List<Batch>> getAllBatches() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/batches/{id}")
    public ResponseEntity<Batch> getBatchById(@PathVariable("id") String id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/batches")
    public ResponseEntity<Batch> createBatch(@RequestBody Batch entity) {
        return ResponseEntity.ok(service.save(entity));
    }

    @PutMapping("/batches/{id}")
    public ResponseEntity<Batch> updateBatch(@PathVariable("id") String id, @RequestBody Batch entity) {
        entity.setId(id);
        if (service.update(entity)) {
            return ResponseEntity.ok(entity);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/batches/{id}")
    public ResponseEntity<Void> deleteBatch(@PathVariable("id") String id) {
        if (service.delete(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // --- SECTION ENDPOINTS ---

    @GetMapping("/sections")
    public ResponseEntity<List<Section>> getAllSections() {
        return ResponseEntity.ok(service.findAllSections());
    }

    @GetMapping("/sections/{id}")
    public ResponseEntity<Section> getSectionById(@PathVariable("id") String id) {
        return service.findSectionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/sections")
    public ResponseEntity<Section> createSection(@RequestBody Section entity) {
        return ResponseEntity.ok(service.saveSection(entity));
    }

    @PutMapping("/sections/{id}")
    public ResponseEntity<Section> updateSection(@PathVariable("id") String id, @RequestBody Section entity) {
        entity.setId(id);
        if (service.updateSection(entity)) {
            return ResponseEntity.ok(entity);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/sections/{id}")
    public ResponseEntity<Void> deleteSection(@PathVariable("id") String id) {
        if (service.deleteSection(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
