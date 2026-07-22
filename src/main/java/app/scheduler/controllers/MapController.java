package app.scheduler.controllers;

import app.scheduler.models.dtos.RoomOccupationDto;
import app.scheduler.services.ScheduleQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

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
            @RequestParam(required = false) String time) {
        
        LocalDateTime targetTime;
        if (time == null || time.isEmpty()) {
            targetTime = LocalDateTime.now();
        } else {
            try {
                // frontend might send ISO string like "2026-07-21T17:31:25.123Z"
                // Instant parsing would be better but we'll try to parse or fallback to now
                java.time.Instant instant = java.time.Instant.parse(time);
                targetTime = LocalDateTime.ofInstant(instant, java.time.ZoneId.systemDefault());
            } catch (Exception e) {
                targetTime = LocalDateTime.now();
            }
        }
        
        loggerService.logMap("Fetching live room occupation for time " + targetTime);
        return ResponseEntity.ok(queryService.getLiveRoomOccupation(targetTime));
    }
}
