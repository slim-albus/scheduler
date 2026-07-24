package app.scheduler.services;

import app.scheduler.exceptions.AuthenticationException;
import app.scheduler.exceptions.ResourceNotFoundException;
import app.scheduler.exceptions.ValidationException;
import app.scheduler.generator.GeneratorConfig;
import app.scheduler.generator.GeneratorInput;
import app.scheduler.generator.ScheduleGenerator;
import app.scheduler.models.*;
import app.scheduler.models.dtos.EventDto;
import app.scheduler.models.dtos.RoomOccupationDto;
import app.scheduler.models.dtos.SlotDto;
import app.scheduler.repositories.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;



@Service
public class ScheduleService {
    private final LoggerService loggerService;

    private final List<ScheduleGenerator> generators;
    private String activeAlgorithmName;
    private final BatchRepository batchRepo;
    private final CourseRepository courseRepo;
    private final TeacherRepository teacherRepo;
    private final RoomRepository roomRepo;
    private final EventRepository eventRepo;
    private final BatchCourseMappingRepository mappingRepo;
    private final SemesterRepository semesterRepo;
    private final StudentRepository studentRepo;
    
    public ScheduleService(List<ScheduleGenerator> generators, BatchRepository batchRepo,
                            CourseRepository courseRepo, TeacherRepository teacherRepo, RoomRepository roomRepo,
                            EventRepository eventRepo, BatchCourseMappingRepository mappingRepo,
                            SemesterRepository semesterRepo, StudentRepository studentRepo, LoggerService loggerService) {
        this.generators = generators;
        if (!generators.isEmpty()) {
            this.activeAlgorithmName = generators.get(0).getAlgorithmName();
        }
        this.batchRepo = batchRepo;
        this.courseRepo = courseRepo;
        this.teacherRepo = teacherRepo;
        this.roomRepo = roomRepo;
        this.eventRepo = eventRepo;
        this.mappingRepo = mappingRepo;
        this.semesterRepo = semesterRepo;
        this.studentRepo = studentRepo;
        this.loggerService = loggerService;
    }

    public List<String> getAvailableAlgorithms() {
        return generators.stream().map(ScheduleGenerator::getAlgorithmName).collect(Collectors.toList());
    }

    public String getActiveAlgorithm() {
        return activeAlgorithmName;
    }

    public void setActiveAlgorithm(String algorithmName) {
        if (generators.stream().anyMatch(g -> g.getAlgorithmName().equals(algorithmName))) {
            this.activeAlgorithmName = algorithmName;
        } else {
            throw new ValidationException("Unknown algorithm: " + algorithmName);
        }
    }

    public List<Event> generateSchedule(String semesterId, GeneratorConfig config) {
        Semester semester = semesterRepo.findById(semesterId).orElseThrow(() -> new ResourceNotFoundException("Semester not found"));
        List<Batch> batches = batchRepo.findBySemesterId(semesterId);
        List<Section> sections = batchRepo.findSectionsBySemesterId(semesterId);
        List<Course> courses = courseRepo.findAll();
        List<Teacher> teachers = teacherRepo.findAll();
        List<Room> rooms = roomRepo.findAll();
        List<BatchCourseMapping> mappings = mappingRepo.findBySemesterId(semesterId);
        
        GeneratorInput input = new GeneratorInput();
        input.setSemester(semester);
        input.setBatches(batches);
        input.setSections(sections);
        input.setCourses(courses);
        input.setTeachers(teachers);
        input.setRooms(rooms);
        input.setBatchCourseMappings(mappings);
        input.setConfig(config);
        
        // Delete any existing schedule events for this semester before generating new ones
        List<Event> existing = eventRepo.findBySemesterId(semesterId);
        for (Event e : existing) {
            eventRepo.delete(e.getId());
        }
        
        ScheduleGenerator generator = generators.stream()
            .filter(g -> g.getAlgorithmName().equals(activeAlgorithmName))
            .findFirst()
            .orElseThrow(() -> new ValidationException("Active algorithm not found"));
            
        List<Event> events = generator.generate(input);
        
        for (Event e : events) {
            eventRepo.save(e);
        }
        return events;
    }
    
    public List<Event> getScheduleForStudent(String studentId, String semesterId) {
        Student student = studentRepo.findById(studentId).orElse(null);
        if (student == null) return List.of();
        
        List<Event> events = eventRepo.findBySectionId(student.getSectionId());
        
        if (semesterId != null) {
            events = events.stream().filter(e -> semesterId.equals(e.getSemesterId())).collect(Collectors.toList());
        }
        
        if (student.getLabGroup() > 0) {
            events = events.stream()
                .filter(e -> e.getLabGroup() == 0 || e.getLabGroup() == student.getLabGroup())
                .collect(Collectors.toList());
        }
        return events;
    }

    private void validateUserCanEditEvent(Event event, User user) {
        if ("ADMIN".equals(user.getRole())) return;
        
        if ("TEACHER".equals(user.getRole())) {
            List<BatchCourseMapping> mappings = mappingRepo.findBySemesterId(event.getSemesterId());
            boolean isAssigned = mappings.stream().anyMatch(m -> 
                m.getBatchId().equals(event.getBatchId()) &&
                ((user.getTeacherId() != null && user.getTeacherId().equals(m.getLectureTeacherId())) || 
                 (user.getTeacherId() != null && user.getTeacherId().equals(m.getLabInstructorId())))
            );
            if (!isAssigned) {
                throw new ValidationException("Teacher is not assigned to this batch.");
            }
        } else {
            throw new AuthenticationException("Unauthorized to edit events.");
        }
    }

    private void validateAvailability(int week, int day, int period, String roomId, String sectionId, String eventIdToIgnore) {
        // Room availability
        List<Event> roomEvents = eventRepo.findByRoomId(roomId);
        boolean roomConflict = roomEvents.stream().anyMatch(e -> 
            e.getWeek() == week && e.getDay() == day && e.getPeriod() == period && 
            !"CANCELED".equals(e.getStatus()) &&
            (eventIdToIgnore == null || !e.getId().equals(eventIdToIgnore))
        );
        if (roomConflict) throw new ValidationException("Room is already booked at this time.");

        // Section availability
        List<Event> sectionEvents = eventRepo.findBySectionId(sectionId);
        boolean sectionConflict = sectionEvents.stream().anyMatch(e -> 
            e.getWeek() == week && e.getDay() == day && e.getPeriod() == period && 
            !"CANCELED".equals(e.getStatus()) &&
            (eventIdToIgnore == null || !e.getId().equals(eventIdToIgnore))
        );
        if (sectionConflict) throw new ValidationException("Section is already booked at this time.");
    }

    public Event cancelEvent(String eventId, User user) {
        Event event = eventRepo.findById(eventId).orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        validateUserCanEditEvent(event, user);
        
        event.setStatus("CANCELED");
        eventRepo.update(event);
        return event;
    }

    public Event restoreEvent(String eventId, User user) {
        Event event = eventRepo.findById(eventId).orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        validateUserCanEditEvent(event, user);
        validateAvailability(event.getWeek(), event.getDay(), event.getPeriod(), event.getRoomId(), event.getSectionId(), eventId);
        
        event.setStatus("SCHEDULED");
        eventRepo.update(event);
        return event;
    }

    public Event rescheduleEvent(String eventId, int newWeek, int newDay, int newPeriod, String newRoomId, User user) {
        Event event = eventRepo.findById(eventId).orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        validateUserCanEditEvent(event, user);
        validateAvailability(newWeek, newDay, newPeriod, newRoomId, event.getSectionId(), eventId);
        
        event.setWeek(newWeek);
        event.setDay(newDay);
        event.setPeriod(newPeriod);
        event.setRoomId(newRoomId);
        event.setStatus("SCHEDULED");
        eventRepo.update(event);
        return event;
    }

    public Event bookEvent(Event newEvent, User user) {
        validateUserCanEditEvent(newEvent, user);
        validateAvailability(newEvent.getWeek(), newEvent.getDay(), newEvent.getPeriod(), newEvent.getRoomId(), newEvent.getSectionId(), null);
        
        newEvent.setStatus("SCHEDULED");
        Event saved = eventRepo.save(newEvent);
        return saved;
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
        eventDto.date = current.getDate() != null ? current.getDate().toString() : null;
        eventDto.labGroup = current.getLabGroup();
        
        if (current.getTeacherId() != null) teacherRepo.findById(current.getTeacherId()).ifPresent(t -> eventDto.teacherName = t.getName());
        if (current.getCourseId() != null) courseRepo.findById(current.getCourseId()).ifPresent(c -> eventDto.courseName = c.getName());
        if (current.getSectionId() != null) batchRepo.findSectionById(current.getSectionId()).ifPresent(s -> eventDto.sectionName = s.getName());
        if (current.getBatchId() != null) batchRepo.findById(current.getBatchId()).ifPresent(b -> eventDto.batchName = b.getName());
        if (current.getRoomId() != null) roomRepo.findById(current.getRoomId()).ifPresent(r -> eventDto.roomName = r.getName());
        
        return eventDto;
    }
    public List<SlotDto> getAvailableSlots(String semesterId, String sectionId, String teacherId, Integer startWeek, String eventIdToIgnore) {
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

        Semester active = semesterId != null ? semesterRepo.findById(semesterId).orElse(semesterRepo.findActive().orElse(null)) : semesterRepo.findActive().orElse(null);
        if (active == null) return new ArrayList<>();

        int maxWeeks = active.getWeeks();
        int beginWeek = startWeek != null ? startWeek : 1;

        List<Event> conflicts = eventRepo.findAll().stream()
            .filter(e -> active.getId().equals(e.getSemesterId()))
            .filter(e -> !"CANCELED".equals(e.getStatus()))
            .filter(e -> eventIdToIgnore == null || !e.getId().equals(eventIdToIgnore))
            .filter(e -> e.getWeek() >= beginWeek)
            .toList();
            
        List<Room> allRooms = roomRepo.findAll().stream()
            .filter(r -> requiredRoomType.equals(r.getType()))
            .toList();

        List<SlotDto> available = new ArrayList<>();
        java.time.LocalDate startDate = active.getStartDate();
        
        String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

        for (int w = beginWeek; w <= maxWeeks; w++) {
            for (int day = 1; day <= 6; day++) {
                for (int period = 1; period <= 5; period++) {
                    int currentW = w;
                    int d = day;
                    int p = period;
                    
                    // Check if section or teacher is busy in this specific week, day, period
                    boolean personOrSectionBusy = conflicts.stream().anyMatch(e -> 
                        e.getWeek() == currentW && e.getDay() == d && e.getPeriod() == p && 
                        ((sectionId != null && sectionId.equals(e.getSectionId())) ||
                         (teacherId != null && teacherId.equals(e.getTeacherId())))
                    );
                    
                    if (!personOrSectionBusy) {
                        // Find available rooms for this slot
                        List<Room> availableRooms = new ArrayList<>();
                        for (Room room : allRooms) {
                            boolean roomBusy = conflicts.stream().anyMatch(e -> 
                                e.getWeek() == currentW && e.getDay() == d && e.getPeriod() == p && room.getId().equals(e.getRoomId())
                            );
                            if (!roomBusy) {
                                availableRooms.add(room);
                            }
                        }
                        
                        if (!availableRooms.isEmpty()) {
                            SlotDto slot = new SlotDto();
                            slot.week = currentW;
                            slot.day = d;
                            slot.period = p;
                            slot.availableRooms = availableRooms;
                            
                            String dateStr = "";
                            if (startDate != null) {
                                java.time.LocalDate slotDate = startDate.plusWeeks(currentW - 1).plusDays(d - 1);
                                java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("MMM d");
                                dateStr = slotDate.format(formatter) + " ";
                            }
                            
                            String weekLabel = (startWeek != null && currentW == startWeek) ? "This Week" : 
                                               (startWeek != null && currentW == startWeek + 1) ? "Next Week" : "Week " + currentW;
                            
                            slot.description = weekLabel + " - " + dateStr + "(" + daysOfWeek[d - 1] + ") - Period " + p;
                            
                            available.add(slot);
                        }
                    }
                }
            }
        }
        return available;
    }
    
    public List<RoomOccupationDto> getLiveRoomOccupation(java.time.LocalDateTime clientTime) {
        Semester active = semesterRepo.findActive().orElse(null);
        if (active == null || active.getStartDate() == null) {
            loggerService.logMap("No active semester found or active semester has no start date. Returning empty room occupation.");
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