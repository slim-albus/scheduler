# PROJECT NOTE: University Timetable Generation System

## Project Overview
A Java-based university timetable generation system that uses a genetic algorithm to automatically schedule semester courses across multiple batches, sections, and lab groups. The system outputs schedule events that can be stored in a database and displayed on calendars.

## Core Concept
The system works on a simple principle: **All models are independent POJOs with NO references to other models**. Relationships between entities (which courses a batch takes, which teacher teaches what) are established through separate mapping tables/objects. The generator consumes these independent entities and mapping data, then produces Events that connect everything together.

## University Structure

```
Semester (e.g., Fall 2026)
    ├── Batch (e.g., 1st Year CS)
    │   ├── Section A (sister class to Section B)
    │   │   ├── Lab Group 1
    │   │   └── Lab Group 2
    │   └── Section B (sister class to Section A)
    │       ├── Lab Group 1
    │       └── Lab Group 2
    ├── Batch (e.g., 2nd Year CS)
    │   └── Section A
    │       ├── Lab Group 1
    │       └── Lab Group 2
    └── Batch (e.g., 3rd Year IT)
        └── Section A
            ├── Lab Group 1
            └── Lab Group 2
```

### Key Concepts
- **Batches**: Groups of students by year and program (e.g., "1st Year CS")
- **Sections**: Sister classes under a batch that take the same courses with same teachers but have different timetables
- **Lab Groups**: Each section has 2 lab groups (for lab sessions only)
- **Events**: The generated output that connects everything together

## Requirements

### Scheduling Constraints
- 6 days per week (Monday to Saturday)
- 5 periods per day
- Each period = 1.5 hours (90 minutes)
- 15-minute breaks between periods
- 3 periods before lunch (8:00 AM - 1:00 PM)
- 2 periods after lunch (2:00 PM - 5:15 PM)
- Lunch break: 1:00 PM - 2:00 PM

### Per Section/Group Rules
- Max 3 classes per day
- Exactly 5 days per week (minimum and maximum)
- At least 2 classes per day

### Course Rules
- Each course has exactly 2 lecture sessions per week
- If a course has a lab: exactly 1 lab session per week
- Lab groups: each section has 2 groups, each group gets 1 lab per week

### Teacher Rules
- One lecture teacher teaches ALL sections of a batch for a specific course
- One lab instructor teaches ALL sections of a batch for a specific course
- If a batch has 2 sections with lab courses, the lab instructor teaches 4 lab sessions per week (2 sections × 2 groups)
- Teachers have availability bitmasks (6 days × 5 periods = 30 bits)

### Room Rules
- Lecture rooms for theory classes
- Computer labs for lab sessions
- Rooms have availability bitmasks (30 bits)

## Folder Structure

```
src/
├── main/
│   └── java/
│       └── app/
│           └── scheduler/
│               ├── models/
│               │   ├── Semester.java
│               │   ├── Batch.java
│               │   ├── Section.java
│               │   ├── Course.java
│               │   ├── Teacher.java
│               │   ├── Room.java
│               │   ├── Event.java
│               │   ├── Student.java
│               │   ├── User.java
│               │   └── Session.java
│               │
│               ├── generator/
│               │   ├── GeneratorInput.java
│               │   ├── GeneratorConfig.java
│               │   ├── BatchCourseMapping.java
│               │   ├── TimetableGenerator.java
│               │   ├── GeneticAlgorithmGenerator.java (implements TimetableGenerator)
│               │   └── TimeSlot.java (helper)
│               │
│               ├── repositories/
│               │   ├── SemesterRepository.java (interface)
│               │   ├── BatchRepository.java (interface)
│               │   ├── SectionRepository.java (interface)
│               │   ├── CourseRepository.java (interface)
│               │   ├── TeacherRepository.java (interface)
│               │   ├── RoomRepository.java (interface)
│               │   ├── EventRepository.java (interface)
│               │   ├── BatchCourseMappingRepository.java (interface)
│               │   ├── impl/
│               │   │   ├── InMemoryRepository.java
│               │   │   ├── JdbcRepository.java
│               │   │   └── SqliteRepository.java
│               │   └── DatabaseConnection.java
│               │
│               ├── services/
│               │   ├── TimetableService.java
│               │   ├── AuthService.java
│               │   ├── ScheduleQueryService.java
│               │   └── ExportService.java
│               │
│               ├── controllers/
│               │   ├── TimetableController.java
│               │   ├── AuthController.java
│               │   ├── AdminController.java
│               │   └── CalendarController.java
│               │
│               ├── auth/
│               │   ├── TokenManager.java
│               │   ├── AuthFilter.java
│               │   └── PasswordHasher.java
│               │
│               └── utils/
│                   ├── BitmaskUtils.java
│                   ├── TimeUtils.java
│                   ├── ValidationUtils.java
│                   └── JSONUtils.java
│
├── resources/
│   └── db/
│       ├── schema.sql
│       └── test_data.sql
│
└── test/
    └── java/
        └── app/
            └── scheduler/
                ├── generator/
                ├── repositories/
                └── services/
```

## Model Definitions

### Semester.java
```java
public class Semester {
    private String id;
    private String name;           // "Fall 2026"
    private String code;           // "F2026"
    private LocalDate startDate;   // All batches start same day
    private LocalDate endDate;
    private int weeks;             // 16
    private String academicYear;   // "2026-2027"
    private boolean isActive;
}
```

### Batch.java
```java
public class Batch {
    private String id;
    private String name;           // "1st Year CS"
    private String program;        // "Computer Science"
    private String year;           // "1st Year"
    private String semesterId;     // Which semester
    private boolean isActive;
}
```

### Section.java
```java
public class Section {
    private String id;
    private String name;           // "Section A" or "Section B"
    private String batchId;        // Which batch
    private int labGroup;          // 0, 1, or 2 (for lab groups)
    private int studentCount;
    private String semesterId;
    private boolean isActive;
}
```

### Course.java
```java
public class Course {
    private String id;
    private String code;           // "CS101"
    private String name;           // "Programming Fundamentals"
    private boolean hasLab;
    private int credits;
    private int lectureHoursPerWeek;  // 2
    private int labHoursPerWeek;      // 1
    private int midtermDuration;     // minutes
    private int finalDuration;       // minutes
    private String department;
    private boolean isActive;
}
```

### Teacher.java
```java
public class Teacher {
    private String id;
    private String name;
    private String email;
    private String department;
    private String type;           // "LECTURE", "LAB", "BOTH"
    private long availabilityBitmask; // 30 bits
    private int maxClassesPerDay;
    private boolean isActive;
}
```

### Room.java
```java
public class Room {
    private String id;
    private String name;           // "L101"
    private String type;           // "LECTURE", "COMPUTER_LAB"
    private int capacity;
    private String building;
    private int level;
    private boolean hasTV;
    private boolean hasProjector;
    private long availabilityBitmask; // 30 bits
    private boolean isActive;
}
```

### Event.java (The Output)
```java
public class Event {
    private String id;
    private String type;           // "LECTURE", "LAB"
    private String topic;
    
    // All references (IDs only - connect everything)
    private String sectionId;
    private String courseId;
    private String teacherId;
    private String roomId;
    private String semesterId;
    private String batchId;
    private int labGroup;          // 0, 1, or 2
    
    // Timetable location
    private int day;               // 1=Mon, 2=Tue, ..., 6=Sat
    private int period;            // 0-4
    private int week;              // Week number in semester
    
    // ISO time (for calendar display)
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private int durationMinutes;   // 90
    
    // Instance tracking
    private int instance;          // 0, 1 for two lectures per week
    
    // Audit
    private String createdBy;
    private LocalDateTime createdAt;
    private int version;
}
```

### BatchCourseMapping.java (Generator Input Mapping)
```java
public class BatchCourseMapping {
    private String id;
    private String batchId;
    private String courseId;
    private String lectureTeacherId;
    private String labInstructorId;   // Null if no lab
    private boolean isRequired;
    private String semesterId;
}
```

### GeneratorInput.java
```java
public class GeneratorInput {
    private Semester semester;
    private List<Batch> batches;
    private List<Section> sections;
    private List<Course> courses;
    private List<Teacher> teachers;
    private List<Room> rooms;
    private List<BatchCourseMapping> batchCourseMappings;
    private GeneratorConfig config;
}
```

### GeneratorConfig.java
```java
public class GeneratorConfig {
    // Algorithm parameters
    private int populationSize = 200;
    private int generations = 1000;
    private double mutationRate = 0.15;
    private double crossoverRate = 0.85;
    private double elitismRate = 0.10;
    private double optimalFitnessThreshold = 0.95;
    private boolean verbose = true;
    
    // Scheduling rules
    private int maxClassesPerDay = 3;
    private int requiredActiveDays = 5;
    private int minClassesPerDay = 2;
    private int lecturesPerCoursePerWeek = 2;
    private int labsPerCoursePerWeek = 1;
    
    // Time configuration
    private int periodsPerDay = 5;
    private int daysPerWeek = 6;
    private int startHour = 8;          // 8:00 AM
    private int lunchStartHour = 13;    // 1:00 PM
    private int lunchEndHour = 14;      // 2:00 PM
    private int breakMinutes = 15;
    private int periodDuration = 90;    // 1.5 hours
}
```

### Student.java
```java
public class Student {
    private String id;
    private String name;
    private String studentId;
    private String email;
    private String sectionId;
    private String batchId;
    private int labGroup;
    private String program;
    private String year;
    private String semesterId;
    private boolean isActive;
}
```

### User.java
```java
public class User {
    private String id;
    private String userId;
    private String username;
    private String email;
    private String passwordHash;
    private String salt;
    private String role;           // "ADMIN", "SCHEDULER", "TEACHER", "STUDENT"
    private String teacherId;      // Optional
    private String studentId;      // Optional
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

### Session.java
```java
public class Session {
    private String id;
    private String token;
    private String userId;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private boolean isActive;
}
```

## Repository Interfaces

### Base Repository Pattern
```java
public interface Repository<T> {
    T save(T entity);
    T findById(String id);
    List<T> findAll();
    boolean delete(String id);
    boolean update(T entity);
}
```

### Specific Repositories
```java
public interface SemesterRepository extends Repository<Semester> {
    Semester findByCode(String code);
    List<Semester> findByYear(String year);
}

public interface BatchRepository extends Repository<Batch> {
    List<Batch> findBySemesterId(String semesterId);
    List<Batch> findByProgram(String program);
}

public interface SectionRepository extends Repository<Section> {
    List<Section> findByBatchId(String batchId);
    List<Section> findBySemesterId(String semesterId);
}

public interface EventRepository extends Repository<Event> {
    List<Event> findBySectionId(String sectionId);
    List<Event> findByTeacherId(String teacherId);
    List<Event> findByRoomId(String roomId);
    List<Event> findBySemesterId(String semesterId);
    List<Event> findBySectionIdAndWeek(String sectionId, int week);
    List<Event> findByTeacherIdAndDay(String teacherId, int day);
}

public interface BatchCourseMappingRepository extends Repository<BatchCourseMapping> {
    List<BatchCourseMapping> findByBatchId(String batchId);
    List<BatchCourseMapping> findBySemesterId(String semesterId);
    BatchCourseMapping findByBatchIdAndCourseId(String batchId, String courseId);
}
```

## Database Schema (SQLite)

```sql
-- Tables (all independent with ID references only)

CREATE TABLE semester (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(20) UNIQUE NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    weeks INT DEFAULT 16,
    academic_year VARCHAR(20),
    is_active BOOLEAN DEFAULT 1
);

CREATE TABLE batch (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    program VARCHAR(100),
    year VARCHAR(20),
    semester_id VARCHAR(36),
    is_active BOOLEAN DEFAULT 1,
    FOREIGN KEY (semester_id) REFERENCES semester(id)
);

CREATE TABLE section (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    batch_id VARCHAR(36),
    lab_group INT DEFAULT 0,
    student_count INT DEFAULT 0,
    semester_id VARCHAR(36),
    is_active BOOLEAN DEFAULT 1,
    FOREIGN KEY (batch_id) REFERENCES batch(id)
);

CREATE TABLE course (
    id VARCHAR(36) PRIMARY KEY,
    code VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    has_lab BOOLEAN DEFAULT 0,
    credits INT DEFAULT 3,
    lecture_hours_per_week INT DEFAULT 2,
    lab_hours_per_week INT DEFAULT 1,
    midterm_duration INT DEFAULT 60,
    final_duration INT DEFAULT 120,
    department VARCHAR(100),
    is_active BOOLEAN DEFAULT 1
);

CREATE TABLE teacher (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    department VARCHAR(100),
    type VARCHAR(20) NOT NULL,
    availability_bitmask BIGINT DEFAULT -1,
    max_classes_per_day INT DEFAULT 5,
    is_active BOOLEAN DEFAULT 1
);

CREATE TABLE room (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    type VARCHAR(20) NOT NULL,
    capacity INT DEFAULT 30,
    building VARCHAR(50),
    level INT DEFAULT 0,
    has_tv BOOLEAN DEFAULT 0,
    has_projector BOOLEAN DEFAULT 0,
    availability_bitmask BIGINT DEFAULT -1,
    is_active BOOLEAN DEFAULT 1
);

CREATE TABLE student (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    student_id VARCHAR(20) UNIQUE NOT NULL,
    email VARCHAR(100),
    section_id VARCHAR(36),
    batch_id VARCHAR(36),
    lab_group INT DEFAULT 0,
    program VARCHAR(100),
    year VARCHAR(20),
    semester_id VARCHAR(36),
    is_active BOOLEAN DEFAULT 1,
    FOREIGN KEY (section_id) REFERENCES section(id)
);

CREATE TABLE user (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(50) UNIQUE NOT NULL,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    salt VARCHAR(64) NOT NULL,
    role VARCHAR(20) NOT NULL,
    teacher_id VARCHAR(36),
    student_id VARCHAR(36),
    is_active BOOLEAN DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (teacher_id) REFERENCES teacher(id),
    FOREIGN KEY (student_id) REFERENCES student(id)
);

CREATE TABLE session (
    id VARCHAR(36) PRIMARY KEY,
    token VARCHAR(255) UNIQUE NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    ip_address VARCHAR(45),
    user_agent VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    is_active BOOLEAN DEFAULT 1,
    FOREIGN KEY (user_id) REFERENCES user(id)
);

-- Mapping Table (Batch → Courses → Teachers)
CREATE TABLE batch_course_mapping (
    id VARCHAR(36) PRIMARY KEY,
    batch_id VARCHAR(36) NOT NULL,
    course_id VARCHAR(36) NOT NULL,
    lecture_teacher_id VARCHAR(36) NOT NULL,
    lab_instructor_id VARCHAR(36),
    is_required BOOLEAN DEFAULT 1,
    semester_id VARCHAR(36) NOT NULL,
    FOREIGN KEY (batch_id) REFERENCES batch(id),
    FOREIGN KEY (course_id) REFERENCES course(id),
    FOREIGN KEY (lecture_teacher_id) REFERENCES teacher(id),
    FOREIGN KEY (lab_instructor_id) REFERENCES teacher(id),
    FOREIGN KEY (semester_id) REFERENCES semester(id),
    UNIQUE(batch_id, course_id)
);

-- Events Table (The Output)
CREATE TABLE event (
    id VARCHAR(36) PRIMARY KEY,
    type VARCHAR(20) NOT NULL,
    topic VARCHAR(200),
    section_id VARCHAR(36) NOT NULL,
    course_id VARCHAR(36) NOT NULL,
    teacher_id VARCHAR(36) NOT NULL,
    room_id VARCHAR(36) NOT NULL,
    semester_id VARCHAR(36) NOT NULL,
    batch_id VARCHAR(36) NOT NULL,
    lab_group INT DEFAULT 0,
    day INT NOT NULL,
    period INT NOT NULL,
    week INT NOT NULL,
    start_date_time TIMESTAMP NOT NULL,
    end_date_time TIMESTAMP NOT NULL,
    duration_minutes INT DEFAULT 90,
    instance INT DEFAULT 0,
    created_by VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version INT DEFAULT 1,
    FOREIGN KEY (section_id) REFERENCES section(id),
    FOREIGN KEY (course_id) REFERENCES course(id),
    FOREIGN KEY (teacher_id) REFERENCES teacher(id),
    FOREIGN KEY (room_id) REFERENCES room(id),
    FOREIGN KEY (semester_id) REFERENCES semester(id),
    FOREIGN KEY (batch_id) REFERENCES batch(id)
);

-- Indexes for performance
CREATE INDEX idx_event_section ON event(section_id);
CREATE INDEX idx_event_teacher ON event(teacher_id);
CREATE INDEX idx_event_room ON event(room_id);
CREATE INDEX idx_event_semester ON event(semester_id);
CREATE INDEX idx_event_day ON event(day, week);
```

## Generator Interface

### TimetableGenerator.java (Interface)
```java
public interface TimetableGenerator {
    List<Event> generate(GeneratorInput input);
    String getAlgorithmName();
    boolean supportsPartialGeneration();
}
```

### GeneticAlgorithmGenerator.java (Implementation)
```java
public class GeneticAlgorithmGenerator implements TimetableGenerator {
    @Override
    public List<Event> generate(GeneratorInput input) {
        // 1. Build lookup maps from input
        // 2. Validate input data
        // 3. Initialize population
        // 4. Run genetic algorithm
        // 5. Convert best chromosome to events
        // 6. Return events
    }
    
    private double calculateFitness(Map<String, String> chromosome) {
        // Check all constraints and return fitness score (0.0 to 1.0)
        // - No duplicate slots for section
        // - Teacher availability
        // - Room availability
        // - Max classes per day (≤3)
        // - Exactly 5 days per week
        // - No overlapping classes
        // - No teacher conflicts
        // - No room conflicts
    }
}
```

## Service Layer

### TimetableService.java
```java
public class TimetableService {
    private final TimetableGenerator generator;
    private final BatchRepository batchRepo;
    private final SectionRepository sectionRepo;
    private final CourseRepository courseRepo;
    private final TeacherRepository teacherRepo;
    private final RoomRepository roomRepo;
    private final EventRepository eventRepo;
    private final BatchCourseMappingRepository mappingRepo;
    private final SemesterRepository semesterRepo;
    
    public List<Event> generateTimetable(String semesterId, GeneratorConfig config) {
        // 1. Fetch all data
        Semester semester = semesterRepo.findById(semesterId);
        List<Batch> batches = batchRepo.findBySemesterId(semesterId);
        List<Section> sections = sectionRepo.findBySemesterId(semesterId);
        List<Course> courses = courseRepo.findAll();
        List<Teacher> teachers = teacherRepo.findAll();
        List<Room> rooms = roomRepo.findAll();
        List<BatchCourseMapping> mappings = mappingRepo.findBySemesterId(semesterId);
        
        // 2. Build input
        GeneratorInput input = new GeneratorInput();
        input.setSemester(semester);
        input.setBatches(batches);
        input.setSections(sections);
        input.setCourses(courses);
        input.setTeachers(teachers);
        input.setRooms(rooms);
        input.setBatchCourseMappings(mappings);
        input.setConfig(config);
        
        // 3. Generate
        List<Event> events = generator.generate(input);
        
        // 4. Save
        return eventRepo.saveAll(events);
    }
    
    public List<Event> getScheduleForSection(String sectionId, int week) {
        return eventRepo.findBySectionIdAndWeek(sectionId, week);
    }
    
    public List<Event> getScheduleForTeacher(String teacherId, int day) {
        return eventRepo.findByTeacherIdAndDay(teacherId, day);
    }
    
    public List<Event> getScheduleForStudent(String studentId) {
        Student student = studentRepo.findById(studentId);
        List<Event> events = eventRepo.findBySectionId(student.getSectionId());
        
        // Filter by lab group if needed
        if (student.getLabGroup() > 0) {
            events = events.stream()
                .filter(e -> e.getLabGroup() == 0 || e.getLabGroup() == student.getLabGroup())
                .collect(Collectors.toList());
        }
        return events;
    }
}
```

### ScheduleQueryService.java
```java
public class ScheduleQueryService {
    private final EventRepository eventRepo;
    private final SectionRepository sectionRepo;
    private final TeacherRepository teacherRepo;
    
    public List<Event> getEventsBySection(String sectionId) {
        return eventRepo.findBySectionId(sectionId);
    }
    
    public List<Event> getEventsByTeacher(String teacherId) {
        return eventRepo.findByTeacherId(teacherId);
    }
    
    public List<Event> getEventsByRoom(String roomId) {
        return eventRepo.findByRoomId(roomId);
    }
    
    public List<Event> getEventsByDay(int day, int week) {
        // Custom query
    }
}
```

### AuthService.java
```java
public class AuthService {
    private final UserRepository userRepo;
    private final SessionRepository sessionRepo;
    private final TokenManager tokenManager;
    private final PasswordHasher passwordHasher;
    
    public User authenticate(String username, String password) {
        // Verify credentials
    }
    
    public Session createSession(String userId) {
        // Create session with JWT token
    }
    
    public void logout(String token) {
        // Invalidate session
    }
    
    public boolean validateToken(String token) {
        // Validate JWT
    }
}
```

## Controllers (REST API)

### TimetableController.java
```java
@RestController
@RequestMapping("/api/timetable")
public class TimetableController {
    private final TimetableService timetableService;
    private final ScheduleQueryService queryService;
    
    @PostMapping("/generate/{semesterId}")
    public ResponseEntity<List<Event>> generateTimetable(
            @PathVariable String semesterId,
            @RequestBody GeneratorConfig config) {
        List<Event> events = timetableService.generateTimetable(semesterId, config);
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
    
    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<Event>> getTeacherSchedule(
            @PathVariable String teacherId,
            @RequestParam(required = false) Integer day) {
        if (day != null) {
            return ResponseEntity.ok(queryService.getEventsByTeacherAndDay(teacherId, day));
        }
        return ResponseEntity.ok(queryService.getEventsByTeacher(teacherId));
    }
    
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Event>> getStudentSchedule(@PathVariable String studentId) {
        return ResponseEntity.ok(timetableService.getScheduleForStudent(studentId));
    }
}
```

### AdminController.java
```java
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final SemesterRepository semesterRepo;
    private final BatchRepository batchRepo;
    private final SectionRepository sectionRepo;
    private final BatchCourseMappingRepository mappingRepo;
    
    @PostMapping("/semester")
    public ResponseEntity<Semester> createSemester(@RequestBody Semester semester) {
        return ResponseEntity.ok(semesterRepo.save(semester));
    }
    
    @PostMapping("/batch")
    public ResponseEntity<Batch> createBatch(@RequestBody Batch batch) {
        return ResponseEntity.ok(batchRepo.save(batch));
    }
    
    @PostMapping("/mapping")
    public ResponseEntity<BatchCourseMapping> createMapping(@RequestBody BatchCourseMapping mapping) {
        return ResponseEntity.ok(mappingRepo.save(mapping));
    }
}
```

### CalendarController.java
```java
@RestController
@RequestMapping("/api/calendar")
public class CalendarController {
    private final ScheduleQueryService queryService;
    private final ExportService exportService;
    
    @GetMapping("/export/{sectionId}")
    public ResponseEntity<String> exportCalendar(@PathVariable String sectionId) {
        List<Event> events = queryService.getEventsBySection(sectionId);
        String ics = exportService.toICS(events);
        return ResponseEntity.ok()
            .header("Content-Type", "text/calendar")
            .body(ics);
    }
    
    @GetMapping("/json/{sectionId}")
    public ResponseEntity<List<Event>> getCalendarEvents(@PathVariable String sectionId) {
        return ResponseEntity.ok(queryService.getEventsBySection(sectionId));
    }
}
```

## Authentication

### TokenManager.java
```java
public class TokenManager {
    private static final long EXPIRATION_TIME = 86400000; // 24 hours
    private static final String SECRET_KEY = "your-secret-key";
    
    public String generateToken(String userId, String role) {
        // JWT generation
    }
    
    public Claims validateToken(String token) {
        // JWT validation
    }
}
```

### AuthFilter.java
```java
public class AuthFilter implements Filter {
    private final TokenManager tokenManager;
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        // Extract token from header
        // Validate token
        // Set user in context
        // Continue chain
    }
}
```

## Helper Utilities

### BitmaskUtils.java
```java
public class BitmaskUtils {
    private static final int DAYS = 6;
    private static final int PERIODS_PER_DAY = 5;
    
    public static long createMask(int[] days, int[] periods) {
        long mask = 0;
        for (int day : days) {
            for (int period : periods) {
                int slotIndex = day * PERIODS_PER_DAY + period;
                mask |= (1L << slotIndex);
            }
        }
        return mask;
    }
    
    public static boolean isAvailable(long mask, int day, int period) {
        int slotIndex = day * PERIODS_PER_DAY + period;
        return (mask & (1L << slotIndex)) != 0;
    }
}
```

### TimeUtils.java
```java
public class TimeUtils {
    public static LocalDateTime getStartTime(int day, int period, Semester semester, int week) {
        // Calculate start time based on period
        // Return LocalDateTime
    }
    
    public static String getPeriodLabel(int period) {
        String[] labels = {"P1 (8:00-9:30)", "P2 (9:45-11:15)", 
                          "P3 (11:30-13:00)", "P4 (14:00-15:30)", 
                          "P5 (15:45-17:15)"};
        return labels[period];
    }
}
```

## Implementation Notes

### Generator Algorithm Design
1. **Chromosome**: Map of gene key to value
   - Key format: `sectionId-courseId-type-instance`
   - Value format: `day,period,roomId`

2. **Fitness Function**: Checks all constraints (0.0 to 1.0)
   - Each constraint violation adds penalty
   - Return 1.0 for perfect schedule

3. **Genetic Algorithm Steps**:
   - Initialize random population
   - Evaluate fitness
   - Select parents (tournament selection)
   - Crossover (uniform crossover)
   - Mutate (random reassignment)
   - Elitism (keep top 10%)

### Data Flow
```
1. Admin creates: Semester → Batches → Sections → Courses
2. Admin creates: BatchCourseMapping (connects batch → courses → teachers)
3. Generator consumes: All entities + mappings
4. Generator produces: List<Event>
5. Events stored in database
6. Users query: Events filtered by section/teacher/student
7. Calendar display: Events rendered in UI
```

### Testing Strategy
1. **Unit Tests**: Each model, repository, service
2. **Integration Tests**: Generator with sample data
3. **Performance Tests**: Large semester with many sections
4. **Validation Tests**: Check all constraints

## Deployment Considerations

1. **Database**: SQLite (for development), PostgreSQL/MySQL (production)
2. **API**: RESTful endpoints with JSON
3. **Authentication**: Simple token-based (JWT)
4. **Caching**: Cache frequently queried schedules
5. **Export**: iCalendar format for external calendars
6. **Audit**: Track who generated schedules

## Success Criteria

1. **Generates valid schedules** - All constraints satisfied (fitness ≥ 0.95)
2. **Handles realistic data** - Multiple batches, sections, courses
3. **Fast enough** - Generation completes within reasonable time
4. **Queries work** - Student, teacher, and admin views display correctly
5. **Extensible** - Can add new constraints or algorithms

This is the complete design specification for the university timetable generation system. All models are clean POJOs with no references to each other, the generator handles all relationships through mapping tables, and the output is a list of Events that can be stored and queried efficiently.