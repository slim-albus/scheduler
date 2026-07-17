package app.scheduler.controllers;

import app.scheduler.models.Event;
import app.scheduler.services.ScheduleQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {
    private final ScheduleQueryService queryService;
    
    public CalendarController(ScheduleQueryService queryService) {
        this.queryService = queryService;
    }
    
    @GetMapping("/json/{sectionId}")
    public ResponseEntity<List<Event>> getCalendarEvents(@PathVariable String sectionId) {
        return ResponseEntity.ok(queryService.getEventsBySection(sectionId));
    }
}
