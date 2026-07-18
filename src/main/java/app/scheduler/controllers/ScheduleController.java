package app.scheduler.controllers;

import app.scheduler.generator.GeneratorConfig;
import app.scheduler.models.Event;
import app.scheduler.models.User;
import app.scheduler.models.dtos.SlotDto;
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

    @GetMapping
    public ResponseEntity<List<Event>> getSchedule(
            @RequestParam(required = false) String semesterId,
            @RequestParam(required = false) String sectionId,
            @RequestParam(required = false) String teacherId,
            HttpServletRequest request) {
        
        User user = (User) request.getAttribute("user");
        String role = user.getRole();
        loggerService.logSchedule("Fetching schedule for " + role + " " + user.getUsername());
        
        if ("STUDENT".equals(role)) {
            return ResponseEntity.ok(scheduleService.getScheduleForStudent(user.getStudentId()));
        } else if ("TEACHER".equals(role)) {
            return ResponseEntity.ok(queryService.getEventsByTeacher(user.getTeacherId()));
        } else {
            // ADMIN
            return ResponseEntity.ok(queryService.getAllEvents(semesterId, sectionId, teacherId));
        }
    }

    @GetMapping("/slots/available")
    public ResponseEntity<List<SlotDto>> getAvailableSlots(
            @RequestParam(required = false) String semesterId,
            @RequestParam(required = false) String sectionId,
            @RequestParam(required = false) String teacherId,
            @RequestParam(required = false) String roomId,
            @RequestParam(required = false) String eventIdToIgnore) {
            
        loggerService.logSchedule("Fetching available slots for section: " + sectionId + " room: " + roomId);
        return ResponseEntity.ok(queryService.getAvailableSlots(semesterId, sectionId, teacherId, roomId, eventIdToIgnore));
    }

    @PutMapping("/event/{id}/cancel")
    public ResponseEntity<Event> cancelEvent(@PathVariable String id, HttpServletRequest request) {
        User user = (User) request.getAttribute("user");
        loggerService.logSchedule("User " + user.getUsername() + " cancelling event: " + id);
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
        loggerService.logSchedule("User " + user.getUsername() + " rescheduling event: " + id + " to day " + day + " period " + period);
        return ResponseEntity.ok(scheduleService.rescheduleEvent(id, day, period, roomId, user));
    }

    @PostMapping("/event")
    public ResponseEntity<Event> bookEvent(@RequestBody Event event, HttpServletRequest request) {
        User user = (User) request.getAttribute("user");
        loggerService.logSchedule("User " + user.getUsername() + " booking new event for section: " + event.getSectionId());
        return ResponseEntity.ok(scheduleService.bookEvent(event, user));
    }
}
