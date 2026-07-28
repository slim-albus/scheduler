package app.scheduler.controllers.admin;

import app.scheduler.models.Event;
import app.scheduler.models.dtos.GeneratorConfigRequest;
import app.scheduler.generator.GeneratorConfig;
import app.scheduler.services.ScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminScheduleController {
    private final ScheduleService scheduleService;

    public AdminScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @PostMapping("/generate/{semesterId}")
    public ResponseEntity<List<Event>> generateSchedule(
            @PathVariable("semesterId") String semesterId,
            @RequestBody GeneratorConfigRequest configRequest) {
        GeneratorConfig config = new GeneratorConfig();
        config.setMaxClassesPerDay(configRequest.maxClassesPerDay);
        try {
            List<Event> generatedEvents = scheduleService.generateSchedule(semesterId, config);
            return ResponseEntity.ok(generatedEvents);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}
