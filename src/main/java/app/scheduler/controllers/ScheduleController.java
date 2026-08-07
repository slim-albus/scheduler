package app.scheduler.controllers;

import app.scheduler.generator.GeneratorConfig;
import app.scheduler.models.Event;
import app.scheduler.models.User;
import app.scheduler.models.dtos.SlotDto;
import app.scheduler.models.dtos.EventDto;
import app.scheduler.models.dtos.RoomOccupationDto;
import app.scheduler.services.ScheduleService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/schedule")
public class ScheduleController {
    private final ScheduleService scheduleService;
    
    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping
    public ResponseEntity<List<EventDto>> getSchedule(
            @RequestParam(name = "semesterId", required = false) String semesterId,
            @RequestParam(name = "sectionId", required = false) String sectionId,
            @RequestParam(name = "teacherId", required = false) String teacherId,
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
            @RequestParam(name = "semesterId", required = false) String semesterId,
            @RequestParam(name = "sectionId", required = false) String sectionId,
            @RequestParam(name = "teacherId", required = false) String teacherId,
            @RequestParam(name = "startWeek", required = false) Integer startWeek,
            @RequestParam(name = "eventIdToIgnore", required = false) String eventIdToIgnore) {
        return ResponseEntity.ok(scheduleService.getAvailableSlots(semesterId, sectionId, teacherId, startWeek, eventIdToIgnore));
    }

    @GetMapping("/active-semester")
    public ResponseEntity<app.scheduler.models.Semester> getActiveSemester() {
        return ResponseEntity.ok(scheduleService.getActiveSemester());
    }

    @GetMapping("/map")
    public ResponseEntity<List<RoomOccupationDto>> getRoomOccupation(
            @RequestParam(name = "time", required = false) String time) {
        
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
        return ResponseEntity.ok(scheduleService.getLiveRoomOccupation(targetTime));
    }

    @PutMapping("/event/{id}/cancel")
    public ResponseEntity<Event> cancelEvent(@PathVariable("id") String id, HttpServletRequest request) {
        User user = (User) request.getAttribute("user");
        return ResponseEntity.ok(scheduleService.cancelEvent(id, user));
    }

    @PutMapping("/event/{id}/restore")
    public ResponseEntity<Event> restoreEvent(@PathVariable("id") String id, HttpServletRequest request) {
        User user = (User) request.getAttribute("user");
        return ResponseEntity.ok(scheduleService.restoreEvent(id, user));
    }

    @PutMapping("/event/{id}/reschedule")
    public ResponseEntity<Event> rescheduleEvent(
            @PathVariable("id") String id,
            @RequestParam("week") int week,
            @RequestParam("day") int day,
            @RequestParam("period") int period,
            @RequestParam("roomId") String roomId,
            HttpServletRequest request) {
        User user = (User) request.getAttribute("user");
        return ResponseEntity.ok(scheduleService.rescheduleEvent(id, week, day, period, roomId, user));
    }

    @PostMapping("/event")
    public ResponseEntity<Event> bookEvent(@RequestBody Event event, HttpServletRequest request) {
        User user = (User) request.getAttribute("user");
        return ResponseEntity.ok(scheduleService.bookEvent(event, user));
    }
}
