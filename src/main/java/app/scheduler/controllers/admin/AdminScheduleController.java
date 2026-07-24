package app.scheduler.controllers.admin;

import app.scheduler.models.Event;
import app.scheduler.models.dtos.GeneratorConfigRequest;
import app.scheduler.generator.GeneratorConfig;
import app.scheduler.services.ScheduleService;
import app.scheduler.services.LoggerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminScheduleController {
    private final ScheduleService scheduleService;
    private final LoggerService loggerService;

    public AdminScheduleController(ScheduleService scheduleService, LoggerService loggerService) {
        this.scheduleService = scheduleService;
        this.loggerService = loggerService;
    }

    @PostMapping("/generate/{semesterId}")
    public ResponseEntity<List<Event>> generateSchedule(
            @PathVariable String semesterId,
            @RequestBody GeneratorConfigRequest configRequest) {
        GeneratorConfig config = new GeneratorConfig();
        config.setPopulationSize(configRequest.populationSize);

        loggerService.logAdmin("Admin triggered schedule generation for semester " + semesterId);
        try {
            List<Event> generatedEvents = scheduleService.generateSchedule(semesterId, config);
            return ResponseEntity.ok(generatedEvents);
        } catch (Exception e) {
            loggerService.logError("Generation failed: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/generate/algorithms")
    public ResponseEntity<List<String>> getAvailableAlgorithms() {
        return ResponseEntity.ok(scheduleService.getAvailableAlgorithms());
    }

    @GetMapping("/generate/algorithms/active")
    public ResponseEntity<String> getActiveAlgorithm() {
        return ResponseEntity.ok(scheduleService.getActiveAlgorithm());
    }

    @PostMapping("/generate/algorithms/active")
    public ResponseEntity<Void> setActiveAlgorithm(@RequestBody String algorithmName) {
        loggerService.logAdmin("Admin setting active algorithm to: " + algorithmName);
        try {
            scheduleService.setActiveAlgorithm(algorithmName.replace("\"", "").trim());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
