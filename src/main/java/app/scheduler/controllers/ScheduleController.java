package app.scheduler.controllers;

import app.scheduler.generator.GeneratorConfig;
import app.scheduler.models.Event;
import app.scheduler.models.User;
import app.scheduler.models.dtos.SlotDto;
import app.scheduler.models.dtos.EventDto;
import app.scheduler.models.dtos.RoomOccupationDto;
import app.scheduler.services.ScheduleService;
import app.scheduler.services.LoggerService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/schedule")
public class ScheduleController {
    private final LoggerService loggerService;
    private final ScheduleService scheduleService;
    
    public ScheduleController(ScheduleService scheduleService, LoggerService loggerService) {
        this.loggerService = loggerService;
        this.scheduleService = scheduleService;
    }

    @GetMapping
    public ResponseEntity<List<EventDto>> getSchedule(
            @RequestParam(required = false) String semesterId,
            @RequestParam(required = false) String sectionId,
            @RequestParam(required = false) String teacherId,
            HttpServletRequest request) {
        
        User user = (User) request.getAttribute("user");
        String role = user.getRole();
        // Use provided semesterId or fallback to the active semester
        String targetSemesterId = semesterId;
        if (targetSemesterId == null) {
            app.scheduler.models.Semester activeSem = scheduleService.getActiveSemester();
            if (activeSem != null) {
                targetSemesterId = activeSem.getId();
            }
        }

        loggerService.logSchedule("Fetching schedule for " + role + " " + user.getUsername() + " in semester " + targetSemesterId);
        
        if ("STUDENT".equals(role)) {
            List<Event> studentEvents = scheduleService.getScheduleForStudent(user.getStudentId(), targetSemesterId);
            return ResponseEntity.ok(scheduleService.mapEventsToDto(studentEvents));
        } else if ("TEACHER".equals(role)) {
            return ResponseEntity.ok(scheduleService.getEventsByTeacher(user.getTeacherId(), targetSemesterId));
        } else {
            // ADMIN
            return ResponseEntity.ok(scheduleService.getAllEvents(targetSemesterId, sectionId, teacherId));
        }
    }

    @GetMapping("/slots/available")
    public ResponseEntity<List<SlotDto>> getAvailableSlots(
            @RequestParam(required = false) String semesterId,
            @RequestParam(required = false) String sectionId,
            @RequestParam(required = false) String teacherId,
            @RequestParam(required = false) Integer week,
            @RequestParam(required = false) String eventIdToIgnore) {
            
        loggerService.logSchedule("Fetching available slots for section: " + sectionId + " week: " + week);
        return ResponseEntity.ok(scheduleService.getAvailableSlots(semesterId, sectionId, teacherId, week, eventIdToIgnore));
    }

    @GetMapping("/active-semester")
    public ResponseEntity<app.scheduler.models.Semester> getActiveSemester() {
        return ResponseEntity.ok(scheduleService.getActiveSemester());
    }

    @GetMapping("/map")
    public ResponseEntity<List<RoomOccupationDto>> getRoomOccupation(
            @RequestParam(required = false) String time) {
        
        LocalDateTime targetTime;
        if (time == null || time.isEmpty()) {
            targetTime = LocalDateTime.now();
        } else {
            try {
                java.time.Instant instant = java.time.Instant.parse(time);
                targetTime = LocalDateTime.ofInstant(instant, java.time.ZoneId.systemDefault());
            } catch (Exception e) {
                targetTime = LocalDateTime.now();
            }
        }
        
        loggerService.logMap("Fetching live room occupation for time " + targetTime);
        return ResponseEntity.ok(scheduleService.getLiveRoomOccupation(targetTime));
    }

    @PutMapping("/event/{id}/cancel")
    public ResponseEntity<Event> cancelEvent(@PathVariable String id, HttpServletRequest request) {
        User user = (User) request.getAttribute("user");
        loggerService.logSchedule("User " + user.getUsername() + " cancelling event: " + id);
        return ResponseEntity.ok(scheduleService.cancelEvent(id, user));
    }

    @PutMapping("/event/{id}/restore")
    public ResponseEntity<Event> restoreEvent(@PathVariable String id, HttpServletRequest request) {
        User user = (User) request.getAttribute("user");
        loggerService.logSchedule("User " + user.getUsername() + " restoring event: " + id);
        return ResponseEntity.ok(scheduleService.restoreEvent(id, user));
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
