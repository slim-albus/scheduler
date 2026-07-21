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
                targetTime = LocalDateTime.parse(time, DateTimeFormatter.ISO_DATE_TIME);
            } catch (DateTimeParseException e) {
                targetTime = LocalDateTime.now();
            }
        }
        
        // Map DayOfWeek (1=Monday...7=Sunday). Keep 1-6 for Schedule.
        int day = targetTime.getDayOfWeek().getValue();
        if (day > 6) day = 1; // Default Sunday to Monday for map visualization if needed, or leave it (no events on Sunday)
        
        int hour = targetTime.getHour();
        int period = 0;
        
        if (hour >= 8 && hour < 10) period = 1;
        else if (hour >= 10 && hour < 12) period = 2;
        else if (hour >= 12 && hour < 14) period = 3;
        else if (hour >= 14 && hour < 16) period = 4;
        else if (hour >= 16 && hour < 18) period = 5;

        loggerService.logMap("Fetching live room occupation for time " + targetTime + " -> day " + day + " period " + period);
        return ResponseEntity.ok(queryService.getRoomOccupation(day, period));
    }
}
