package app.scheduler.controllers;

import app.scheduler.models.dtos.RoomOccupationDto;
import app.scheduler.services.ScheduleQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/map")
public class MapController {
    private final ScheduleQueryService queryService;
    private final app.scheduler.services.LoggerService loggerService;

    public MapController(ScheduleQueryService queryService, app.scheduler.services.LoggerService loggerService) {
        this.queryService = queryService;
        this.loggerService = loggerService;
    }

    @GetMapping
    public ResponseEntity<List<RoomOccupationDto>> getRoomOccupation(
            @RequestParam(required = false) Integer day,
            @RequestParam(required = false) Integer period) {
        loggerService.logMap("Fetching live room occupation for day " + day + " period " + period);
        return ResponseEntity.ok(queryService.getRoomOccupation(day, period));
    }
}
