package app.scheduler.controllers.admin;

import app.scheduler.models.Room;
import app.scheduler.services.RoomService;
import app.scheduler.services.LoggerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/rooms")
public class RoomController {
    private final RoomService service;
    private final LoggerService loggerService;

    public RoomController(RoomService service, LoggerService loggerService) {
        this.service = service;
        this.loggerService = loggerService;
    }

    @GetMapping
    public ResponseEntity<List<Room>> getAll() {
        loggerService.logAdmin("Admin fetched all rooms");
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Room> getById(@PathVariable String id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Room> create(@RequestBody Room entity) {
        loggerService.logAdmin("Admin created Room");
        return ResponseEntity.ok(service.save(entity));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Room> update(@PathVariable String id, @RequestBody Room entity) {
        entity.setId(id);
        if (service.update(entity)) {
            loggerService.logAdmin("Admin updated Room " + id);
            return ResponseEntity.ok(entity);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (service.delete(id)) {
            loggerService.logAdmin("Admin deleted Room " + id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
