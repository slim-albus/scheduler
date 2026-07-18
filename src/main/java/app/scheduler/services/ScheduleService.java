package app.scheduler.services;

import app.scheduler.generator.GeneratorConfig;
import app.scheduler.generator.GeneratorInput;
import app.scheduler.generator.ScheduleGenerator;
import app.scheduler.models.*;
import app.scheduler.repositories.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import app.scheduler.exceptions.ValidationException;
import app.scheduler.exceptions.AuthenticationException;
import app.scheduler.exceptions.ResourceNotFoundException;

@Service
public class ScheduleService {
    private final LoggerService loggerService;

    private final ScheduleGenerator generator;
    private final BatchRepository batchRepo;
    private final SectionRepository sectionRepo;
    private final CourseRepository courseRepo;
    private final TeacherRepository teacherRepo;
    private final RoomRepository roomRepo;
    private final EventRepository eventRepo;
    private final BatchCourseMappingRepository mappingRepo;
    private final SemesterRepository semesterRepo;
    private final StudentRepository studentRepo;
    
    public ScheduleService(ScheduleGenerator generator, BatchRepository batchRepo, SectionRepository sectionRepo,
                            CourseRepository courseRepo, TeacherRepository teacherRepo, RoomRepository roomRepo,
                            EventRepository eventRepo, BatchCourseMappingRepository mappingRepo,
                            SemesterRepository semesterRepo, StudentRepository studentRepo, LoggerService loggerService) {
        this.generator = generator;
        this.batchRepo = batchRepo;
        this.sectionRepo = sectionRepo;
        this.courseRepo = courseRepo;
        this.teacherRepo = teacherRepo;
        this.roomRepo = roomRepo;
        this.eventRepo = eventRepo;
        this.mappingRepo = mappingRepo;
        this.semesterRepo = semesterRepo;
        this.studentRepo = studentRepo;
        this.loggerService = loggerService;
    }

    public List<Event> generateSchedule(String semesterId, GeneratorConfig config) {
        Semester semester = semesterRepo.findById(semesterId).orElseThrow(() -> new ResourceNotFoundException("Semester not found"));
        List<Batch> batches = batchRepo.findBySemesterId(semesterId);
        List<Section> sections = sectionRepo.findBySemesterId(semesterId);
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
        
        List<Event> events = generator.generate(input);
        
        for (Event e : events) {
            eventRepo.save(e);
        }
        return events;
    }
    
    public List<Event> getScheduleForStudent(String studentId) {
        Student student = studentRepo.findById(studentId).orElse(null);
        if (student == null) return List.of();
        
        List<Event> events = eventRepo.findBySectionId(student.getSectionId());
        
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

    private void validateAvailability(int day, int period, String roomId, String sectionId, String eventIdToIgnore) {
        // Room availability
        List<Event> roomEvents = eventRepo.findByRoomId(roomId);
        boolean roomConflict = roomEvents.stream().anyMatch(e -> 
            e.getDay() == day && e.getPeriod() == period && 
            !"CANCELED".equals(e.getStatus()) &&
            (eventIdToIgnore == null || !e.getId().equals(eventIdToIgnore))
        );
        if (roomConflict) throw new ValidationException("Room is already booked at this time.");

        // Section availability
        List<Event> sectionEvents = eventRepo.findBySectionId(sectionId);
        boolean sectionConflict = sectionEvents.stream().anyMatch(e -> 
            e.getDay() == day && e.getPeriod() == period && 
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

    public Event rescheduleEvent(String eventId, int newDay, int newPeriod, String newRoomId, User user) {
        Event event = eventRepo.findById(eventId).orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        validateUserCanEditEvent(event, user);
        validateAvailability(newDay, newPeriod, newRoomId, event.getSectionId(), eventId);
        
        event.setDay(newDay);
        event.setPeriod(newPeriod);
        event.setRoomId(newRoomId);
        event.setStatus("SCHEDULED");
        eventRepo.update(event);
        return event;
    }

    public Event bookEvent(Event newEvent, User user) {
        validateUserCanEditEvent(newEvent, user);
        validateAvailability(newEvent.getDay(), newEvent.getPeriod(), newEvent.getRoomId(), newEvent.getSectionId(), null);
        
        newEvent.setStatus("SCHEDULED");
        Event saved = eventRepo.save(newEvent);
        return saved;
    }
}
