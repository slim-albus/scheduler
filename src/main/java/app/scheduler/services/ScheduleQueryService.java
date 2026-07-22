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
import app.scheduler.models.Semester;
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
    private final BatchRepository batchRepo;
    private final SemesterRepository semesterRepo;
    
    public ScheduleQueryService(EventRepository eventRepo, RoomRepository roomRepo, 
                                TeacherRepository teacherRepo, CourseRepository courseRepo,
                                BatchRepository batchRepo,
                                SemesterRepository semesterRepo) {
        this.eventRepo = eventRepo;
        this.roomRepo = roomRepo;
        this.teacherRepo = teacherRepo;
        this.courseRepo = courseRepo;
        this.batchRepo = batchRepo;
        this.semesterRepo = semesterRepo;
    }

    private List<Event> filterByActiveSemester(List<Event> events) {
        return semesterRepo.findActive()
            .map(active -> events.stream().filter(e -> active.getId().equals(e.getSemesterId())).toList())
            .orElse(new ArrayList<>());
    }

    public Semester getActiveSemester() {
        return semesterRepo.findActive().orElse(null);
    }

    public List<EventDto> getEventsBySection(String sectionId, String semesterId) {
        List<Event> events = eventRepo.findBySectionId(sectionId);
        if (semesterId != null) {
            events = events.stream().filter(e -> semesterId.equals(e.getSemesterId())).toList();
        } else {
            events = filterByActiveSemester(events);
        }
        return events.stream().map(this::mapToDto).toList();
    }
    
    public List<EventDto> getEventsByTeacher(String teacherId, String semesterId) {
        List<Event> events = eventRepo.findByTeacherId(teacherId);
        if (semesterId != null) {
            events = events.stream().filter(e -> semesterId.equals(e.getSemesterId())).toList();
        } else {
            events = filterByActiveSemester(events);
        }
        return events.stream().map(this::mapToDto).toList();
    }
    
    public List<EventDto> getEventsByRoom(String roomId) {
        return filterByActiveSemester(eventRepo.findByRoomId(roomId))
            .stream().map(this::mapToDto).toList();
    }
    
    public List<EventDto> getEventsBySectionAndWeek(String sectionId, int week) {
        return filterByActiveSemester(eventRepo.findBySectionIdAndWeek(sectionId, week))
            .stream().map(this::mapToDto).toList();
    }
    
    public List<EventDto> getEventsByTeacherAndDay(String teacherId, int day) {
        return filterByActiveSemester(eventRepo.findByTeacherIdAndDay(teacherId, day))
            .stream().map(this::mapToDto).toList();
    }

    public List<EventDto> getAllEvents(String semesterId, String sectionId, String teacherId) {
        List<Event> all = eventRepo.findAll();
        return all.stream()
            .filter(e -> {
                if (semesterId != null) return semesterId.equals(e.getSemesterId());
                return semesterRepo.findActive().map(active -> active.getId().equals(e.getSemesterId())).orElse(false);
            })
            .filter(e -> sectionId == null || sectionId.equals(e.getSectionId()))
            .filter(e -> teacherId == null || teacherId.equals(e.getTeacherId()))
            .map(this::mapToDto)
            .toList();
    }

    public List<EventDto> mapEventsToDto(List<Event> events) {
        return events.stream().map(this::mapToDto).toList();
    }

    public EventDto mapToDto(Event current) {
        if (current == null) return null;
        EventDto eventDto = new EventDto();
        eventDto.id = current.getId();
        eventDto.topic = current.getTopic();
        eventDto.type = current.getType();
        eventDto.day = current.getDay();
        eventDto.period = current.getPeriod();
        eventDto.status = current.getStatus();
        eventDto.week = current.getWeek();
        
        if (current.getTeacherId() != null) teacherRepo.findById(current.getTeacherId()).ifPresent(t -> eventDto.teacherName = t.getName());
        if (current.getCourseId() != null) courseRepo.findById(current.getCourseId()).ifPresent(c -> eventDto.courseName = c.getName());
        if (current.getSectionId() != null) batchRepo.findSectionById(current.getSectionId()).ifPresent(s -> eventDto.sectionName = s.getName());
        if (current.getBatchId() != null) batchRepo.findById(current.getBatchId()).ifPresent(b -> eventDto.batchName = b.getName());
        if (current.getRoomId() != null) roomRepo.findById(current.getRoomId()).ifPresent(r -> eventDto.roomName = r.getName());
        
        return eventDto;
    }
    public List<SlotDto> getAvailableSlots(String semesterId, String sectionId, String teacherId, Integer week, String eventIdToIgnore) {
        // Find teacher type to filter rooms
        String roomTypePreference = null;
        if (teacherId != null) {
            teacherRepo.findById(teacherId).ifPresent(t -> {
                if ("LAB_INSTRUCTOR".equals(t.getType())) {
                    // roomTypePreference = "COMPUTER_LAB"; // Actually the user said "consider the kind of room the need lab or lecture(based on their teacher type)". Let's just find rooms that match the type.
                }
            });
        }
        final String teacherType = teacherId != null ? teacherRepo.findById(teacherId).map(Teacher::getType).orElse(null) : null;
        final String requiredRoomType = "LAB_INSTRUCTOR".equals(teacherType) ? "COMPUTER_LAB" : "LECTURE_ROOM";

        List<Event> conflicts = eventRepo.findAll().stream()
            .filter(e -> {
                if (semesterId != null) return semesterId.equals(e.getSemesterId());
                return semesterRepo.findActive().map(active -> active.getId().equals(e.getSemesterId())).orElse(false);
            })
            .filter(e -> !"CANCELED".equals(e.getStatus()))
            .filter(e -> eventIdToIgnore == null || !e.getId().equals(eventIdToIgnore))
            .filter(e -> week == null || e.getWeek() == week)
            .toList();
            
        List<Room> allRooms = roomRepo.findAll().stream()
            .filter(r -> requiredRoomType.equals(r.getType()))
            .toList();

        List<SlotDto> available = new ArrayList<>();
        for (int day = 1; day <= 6; day++) {
            for (int period = 1; period <= 5; period++) {
                int d = day;
                int p = period;
                
                // Check if section or teacher is busy
                boolean personOrSectionBusy = conflicts.stream().anyMatch(e -> 
                    e.getDay() == d && e.getPeriod() == p && 
                    ((sectionId != null && sectionId.equals(e.getSectionId())) ||
                     (teacherId != null && teacherId.equals(e.getTeacherId())))
                );
                
                if (!personOrSectionBusy) {
                    // Find available rooms for this slot
                    List<Room> availableRooms = new ArrayList<>();
                    for (Room room : allRooms) {
                        boolean roomBusy = conflicts.stream().anyMatch(e -> 
                            e.getDay() == d && e.getPeriod() == p && room.getId().equals(e.getRoomId())
                        );
                        if (!roomBusy) {
                            availableRooms.add(room);
                        }
                    }
                    
                    if (!availableRooms.isEmpty()) {
                        available.add(new SlotDto(d, p, availableRooms));
                    }
                }
            }
        }
        return available;
    }
    
    public List<RoomOccupationDto> getLiveRoomOccupation(java.time.LocalDateTime clientTime) {
        Semester active = semesterRepo.findActive().orElse(null);
        if (active == null || active.getStartDate() == null) {
            return new ArrayList<>();
        }

        // Calculate week and day
        java.time.LocalDate start = active.getStartDate().with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        java.time.LocalDate clientDate = clientTime.toLocalDate();
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(start, clientDate);
        
        int targetWeek = (int) (daysBetween / 7) + 1;
        int targetDay = (int) (daysBetween % 7) + 1;
        
        // Calculate period based on time (8:00 start, 90m duration, 15m break, lunch 13-14)
        java.time.LocalTime time = clientTime.toLocalTime();
        int targetPeriod = 0;
        
        java.time.LocalTime p1Start = java.time.LocalTime.of(8, 0);
        java.time.LocalTime p1End = p1Start.plusMinutes(90);
        java.time.LocalTime p2Start = p1End.plusMinutes(15);
        java.time.LocalTime p2End = p2Start.plusMinutes(90);
        java.time.LocalTime p3Start = p2End.plusMinutes(15);
        java.time.LocalTime p3End = p3Start.plusMinutes(90);
        java.time.LocalTime p4Start = java.time.LocalTime.of(14, 0); // After lunch
        java.time.LocalTime p4End = p4Start.plusMinutes(90);
        java.time.LocalTime p5Start = p4End.plusMinutes(15);
        java.time.LocalTime p5End = p5Start.plusMinutes(90);
        
        if (!time.isBefore(p1Start) && time.isBefore(p1End)) targetPeriod = 1;
        else if (!time.isBefore(p2Start) && time.isBefore(p2End)) targetPeriod = 2;
        else if (!time.isBefore(p3Start) && time.isBefore(p3End)) targetPeriod = 3;
        else if (!time.isBefore(p4Start) && time.isBefore(p4End)) targetPeriod = 4;
        else if (!time.isBefore(p5Start) && time.isBefore(p5End)) targetPeriod = 5;

        // If outside normal hours or during break, we can just return empty or the next period. 
        // For simplicity, if targetPeriod is 0, we can just say no rooms are occupied right now (or it's break time).
        
        List<Room> allRooms = roomRepo.findAll();
        List<Event> activeEvents = filterByActiveSemester(eventRepo.findAll());
        List<RoomOccupationDto> result = new ArrayList<>();
        
        for (Room room : allRooms) {
            RoomOccupationDto dto = new RoomOccupationDto();
            dto.roomId = room.getId();
            dto.roomName = room.getName();
            dto.building = room.getBuilding();
            dto.level = room.getLevel();
            dto.roomType = room.getType();
            dto.hasEquipment = room.isHasEquipment();
            
            Event current = null;
            if (targetPeriod > 0 && targetWeek > 0 && targetWeek <= active.getWeeks() && targetDay >= 1 && targetDay <= 6) {
                current = findEventForMap(activeEvents, room.getId(), targetWeek, targetDay, targetPeriod);
            }
            
            if (current != null) {
                dto.isOccupied = !"CANCELED".equals(current.getStatus());
                
                EventDto eventDto = new EventDto();
                eventDto.id = current.getId();
                eventDto.topic = current.getTopic();
                eventDto.type = current.getType();
                eventDto.day = current.getDay();
                eventDto.period = current.getPeriod();
                eventDto.status = current.getStatus();
                
                if (current.getTeacherId() != null) teacherRepo.findById(current.getTeacherId()).ifPresent(t -> eventDto.teacherName = t.getName());
                if (current.getCourseId() != null) courseRepo.findById(current.getCourseId()).ifPresent(c -> eventDto.courseName = c.getName());
                if (current.getSectionId() != null) batchRepo.findSectionById(current.getSectionId()).ifPresent(s -> eventDto.sectionName = s.getName());
                if (current.getBatchId() != null) batchRepo.findById(current.getBatchId()).ifPresent(b -> eventDto.batchName = b.getName());
                
                dto.currentEvent = eventDto;
            } else {
                dto.isOccupied = false;
            }
            
            result.add(dto);
        }
        
        return result;
    }

    private Event findEventForMap(List<Event> events, String roomId, int week, int day, int period) {
        // Find the event for this room, day, period. Don't exclude CANCELED because the UI needs to show it as unoccupied but with info.
        return events.stream()
            .filter(e -> roomId.equals(e.getRoomId()) && e.getWeek() == week && e.getDay() == day && e.getPeriod() == period)
            .findFirst()
            .orElse(null);
    }
}
