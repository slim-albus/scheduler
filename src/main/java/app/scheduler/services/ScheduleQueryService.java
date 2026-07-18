package app.scheduler.services;

import app.scheduler.models.Event;
import app.scheduler.models.Room;
import app.scheduler.models.Teacher;
import app.scheduler.models.Course;
import app.scheduler.models.Section;
import app.scheduler.models.Batch;
import app.scheduler.models.dtos.SlotDto;
import app.scheduler.models.dtos.EventDto;
import app.scheduler.models.dtos.RoomOccupationDto;
import app.scheduler.repositories.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ScheduleQueryService {
    private final EventRepository eventRepo;
    private final RoomRepository roomRepo;
    private final TeacherRepository teacherRepo;
    private final CourseRepository courseRepo;
    private final SectionRepository sectionRepo;
    private final BatchRepository batchRepo;
    
    public ScheduleQueryService(EventRepository eventRepo, RoomRepository roomRepo, 
                                TeacherRepository teacherRepo, CourseRepository courseRepo,
                                SectionRepository sectionRepo, BatchRepository batchRepo) {
        this.eventRepo = eventRepo;
        this.roomRepo = roomRepo;
        this.teacherRepo = teacherRepo;
        this.courseRepo = courseRepo;
        this.sectionRepo = sectionRepo;
        this.batchRepo = batchRepo;
    }

    public List<Event> getEventsBySection(String sectionId) {
        return eventRepo.findBySectionId(sectionId);
    }
    
    public List<Event> getEventsByTeacher(String teacherId) {
        return eventRepo.findByTeacherId(teacherId);
    }
    
    public List<Event> getEventsByRoom(String roomId) {
        return eventRepo.findByRoomId(roomId);
    }
    
    public List<Event> getEventsBySectionAndWeek(String sectionId, int week) {
        return eventRepo.findBySectionIdAndWeek(sectionId, week);
    }
    
    public List<Event> getEventsByTeacherAndDay(String teacherId, int day) {
        return eventRepo.findByTeacherIdAndDay(teacherId, day);
    }

    public List<Event> getAllEvents(String semesterId, String sectionId, String teacherId) {
        List<Event> all = eventRepo.findAll();
        return all.stream()
            .filter(e -> semesterId == null || semesterId.equals(e.getSemesterId()))
            .filter(e -> sectionId == null || sectionId.equals(e.getSectionId()))
            .filter(e -> teacherId == null || teacherId.equals(e.getTeacherId()))
            .toList();
    }
    
    public List<SlotDto> getAvailableSlots(String semesterId, String sectionId, String teacherId, String roomId, String eventIdToIgnore) {
        List<Event> conflicts = eventRepo.findAll().stream()
            .filter(e -> semesterId == null || semesterId.equals(e.getSemesterId()))
            .filter(e -> !"CANCELED".equals(e.getStatus()))
            .filter(e -> eventIdToIgnore == null || !e.getId().equals(eventIdToIgnore))
            .filter(e -> 
                (sectionId != null && sectionId.equals(e.getSectionId())) ||
                (teacherId != null && teacherId.equals(e.getTeacherId())) ||
                (roomId != null && roomId.equals(e.getRoomId()))
            )
            .toList();
            
        List<SlotDto> available = new ArrayList<>();
        for (int day = 1; day <= 5; day++) {
            for (int period = 1; period <= 6; period++) {
                int d = day;
                int p = period;
                boolean conflict = conflicts.stream().anyMatch(e -> e.getDay() == d && e.getPeriod() == p);
                if (!conflict) {
                    available.add(new SlotDto(d, p));
                }
            }
        }
        return available;
    }
    
    public List<RoomOccupationDto> getRoomOccupation(Integer day, Integer period) {
        // If day/period are null, ideally we calculate the current day/period. 
        // For now, we default to day 0, period 0 if missing.
        int targetDay = day != null ? day : 0;
        int targetPeriod = period != null ? period : 0;
        
        List<Room> allRooms = roomRepo.findAll();
        List<RoomOccupationDto> result = new ArrayList<>();
        
        for (Room room : allRooms) {
            RoomOccupationDto dto = new RoomOccupationDto();
            dto.roomId = room.getId();
            dto.roomName = room.getName();
            dto.building = room.getBuilding();
            dto.level = room.getLevel();
            
            // Find an event in this room at this day/period
            List<Event> roomEvents = eventRepo.findByRoomId(room.getId());
            Event current = roomEvents.stream()
                .filter(e -> e.getDay() == targetDay && e.getPeriod() == targetPeriod && !"CANCELED".equals(e.getStatus()))
                .findFirst()
                .orElse(null);
                
            if (current != null) {
                dto.isOccupied = true;
                EventDto eventDto = new EventDto();
                eventDto.id = current.getId();
                eventDto.topic = current.getTopic();
                eventDto.day = current.getDay();
                eventDto.period = current.getPeriod();
                eventDto.type = current.getType();
                
                // Fetch rich details if the foreign keys are set
                if (current.getTeacherId() != null) teacherRepo.findById(current.getTeacherId()).ifPresent(t -> eventDto.teacherName = t.getName());
                if (current.getCourseId() != null) courseRepo.findById(current.getCourseId()).ifPresent(c -> eventDto.courseName = c.getName());
                if (current.getSectionId() != null) sectionRepo.findById(current.getSectionId()).ifPresent(s -> eventDto.sectionName = s.getName());
                if (current.getBatchId() != null) batchRepo.findById(current.getBatchId()).ifPresent(b -> eventDto.batchName = b.getName());

                dto.currentEvent = eventDto;
            } else {
                dto.isOccupied = false;
            }
            result.add(dto);
        }
        
        return result;
    }
}
