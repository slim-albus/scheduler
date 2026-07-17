package app.scheduler.controllers;

import app.scheduler.generator.GeneratorConfig;
import app.scheduler.models.Event;
import app.scheduler.models.User;
import app.scheduler.models.dtos.GeneratorConfigRequest;
import app.scheduler.models.dtos.RoomOccupationDto;
import app.scheduler.services.ScheduleQueryService;
import app.scheduler.services.ScheduleService;
import app.scheduler.services.LoggerService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedule")
public class ScheduleController {
    private final LoggerService loggerService;
    private final ScheduleService scheduleService;
    private final ScheduleQueryService queryService;
    
    public ScheduleController(ScheduleService scheduleService, ScheduleQueryService queryService, LoggerService loggerService) {
        this.loggerService = loggerService;
        this.scheduleService = scheduleService;
        this.queryService = queryService;
    }

    @PostMapping("/generate/{semesterId}")
    public ResponseEntity<List<Event>> generateSchedule(
            @PathVariable String semesterId,
            @RequestBody GeneratorConfigRequest configRequest) {
        GeneratorConfig config = new GeneratorConfig();
        config.setPopulationSize(configRequest.populationSize);
        List<Event> events = scheduleService.generateSchedule(semesterId, config);
        return ResponseEntity.ok(events);
    }
    
    @GetMapping("/section/{sectionId}")
    public ResponseEntity<List<Event>> getSectionSchedule(
            @PathVariable String sectionId,
            @RequestParam(required = false) Integer week) {
        if (week != null) {
            return ResponseEntity.ok(queryService.getEventsBySectionAndWeek(sectionId, week));
        }
        return ResponseEntity.ok(queryService.getEventsBySection(sectionId));
    }
    
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Event>> getStudentSchedule(@PathVariable String studentId) {
        return ResponseEntity.ok(scheduleService.getScheduleForStudent(studentId));
    }
    
    @GetMapping("/rooms/occupation")
    public ResponseEntity<List<RoomOccupationDto>> getRoomOccupation(
            @RequestParam(required = false) Integer day,
            @RequestParam(required = false) Integer period) {
        return ResponseEntity.ok(queryService.getRoomOccupation(day, period));
    }

    @PutMapping("/event/{id}/cancel")
    public ResponseEntity<Event> cancelEvent(@PathVariable String id, HttpServletRequest request) {
        User user = (User) request.getAttribute("user");
        return ResponseEntity.ok(scheduleService.cancelEvent(id, user));
    }

    @PutMapping("/event/{id}/reschedule")
    public ResponseEntity<Event> rescheduleEvent(
            @PathVariable String id,
            @RequestParam int day,
            @RequestParam int period,
            @RequestParam String roomId,
            HttpServletRequest request) {
        User user = (User) request.getAttribute("user");
        return ResponseEntity.ok(scheduleService.rescheduleEvent(id, day, period, roomId, user));
    }

    @PostMapping("/event")
    public ResponseEntity<Event> bookEvent(@RequestBody Event event, HttpServletRequest request) {
        User user = (User) request.getAttribute("user");
        return ResponseEntity.ok(scheduleService.bookEvent(event, user));
    }
}
