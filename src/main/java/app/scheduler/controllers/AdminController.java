package app.scheduler.controllers;

import app.scheduler.models.*;
import app.scheduler.models.dtos.*;
import app.scheduler.repositories.*;
import app.scheduler.services.LoggerService;
import app.scheduler.services.ScheduleService;
import app.scheduler.generator.GeneratorConfig;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final LoggerService loggerService;


    private final SemesterRepository semesterRepo;
    private final BatchRepository batchRepo;
    private final SectionRepository sectionRepo;
    private final CourseRepository courseRepo;
    private final TeacherRepository teacherRepo;
    private final RoomRepository roomRepo;
    private final StudentRepository studentRepo;
    private final BatchCourseMappingRepository batchcoursemappingRepo;
    private final ScheduleService scheduleService;

    public AdminController(
        SemesterRepository semesterRepo, 
        BatchRepository batchRepo, 
        SectionRepository sectionRepo, 
        CourseRepository courseRepo, 
        TeacherRepository teacherRepo, 
        RoomRepository roomRepo, 
        StudentRepository studentRepo, 
        BatchCourseMappingRepository batchcoursemappingRepo, 
        ScheduleService scheduleService,
        LoggerService loggerService) {
        
        this.loggerService = loggerService;
        this.semesterRepo = semesterRepo;
        this.batchRepo = batchRepo;
        this.sectionRepo = sectionRepo;
        this.courseRepo = courseRepo;
        this.teacherRepo = teacherRepo;
        this.roomRepo = roomRepo;
        this.studentRepo = studentRepo;
        this.batchcoursemappingRepo = batchcoursemappingRepo;
        this.scheduleService = scheduleService;
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

    // Semester CRUD
    @GetMapping("/semesters")
    public ResponseEntity<List<SemesterDto>> getAllSemesters() {
        List<Semester> list = semesterRepo.findAll();
        List<SemesterDto> dtoList = new ArrayList<>();
        for (Semester item : list) {
            SemesterDto dto = new SemesterDto();
            // In a real app we'd map fields here, but since Dto extends model, we can just cast or copy
            // For now we'll just return the raw model wrapped in ResponseEntity as it serializes the same
            // This is a shortcut for scaffolding
        }
        // Returning raw models directly instead of mapping to avoid 1000 lines of boilerplate field copying
        return ResponseEntity.ok((List) list); 
    }

    @GetMapping("/semesters/{id}")
    public ResponseEntity<Semester> getSemester(@PathVariable String id) {
        return semesterRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new app.scheduler.exceptions.ResourceNotFoundException("Entity not found"));
    }

    @PostMapping("/semesters")
    public ResponseEntity<Semester> createSemester(@RequestBody SemesterDto dto) {
        return ResponseEntity.ok(semesterRepo.save(dto));
    }

    @PutMapping("/semesters/{id}")
    public ResponseEntity<Semester> updateSemester(@PathVariable String id, @RequestBody SemesterDto dto) {
        dto.setId(id);
        if (semesterRepo.update(dto)) {
            return ResponseEntity.ok(dto);
        }
        throw new app.scheduler.exceptions.ResourceNotFoundException("Entity not found");
    }

    @DeleteMapping("/semesters/{id}")
    public ResponseEntity<Void> deleteSemester(@PathVariable String id) {
        if (semesterRepo.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        throw new app.scheduler.exceptions.ResourceNotFoundException("Entity not found");
    }

    // Batch CRUD
    @GetMapping("/batches")
    public ResponseEntity<List<BatchDto>> getAllBatchs() {
        List<Batch> list = batchRepo.findAll();
        List<BatchDto> dtoList = new ArrayList<>();
        for (Batch item : list) {
            BatchDto dto = new BatchDto();
            // In a real app we'd map fields here, but since Dto extends model, we can just cast or copy
            // For now we'll just return the raw model wrapped in ResponseEntity as it serializes the same
            // This is a shortcut for scaffolding
        }
        // Returning raw models directly instead of mapping to avoid 1000 lines of boilerplate field copying
        return ResponseEntity.ok((List) list); 
    }

    @GetMapping("/batches/{id}")
    public ResponseEntity<Batch> getBatch(@PathVariable String id) {
        return batchRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new app.scheduler.exceptions.ResourceNotFoundException("Entity not found"));
    }

    @PostMapping("/batches")
    public ResponseEntity<Batch> createBatch(@RequestBody BatchDto dto) {
        return ResponseEntity.ok(batchRepo.save(dto));
    }

    @PutMapping("/batches/{id}")
    public ResponseEntity<Batch> updateBatch(@PathVariable String id, @RequestBody BatchDto dto) {
        dto.setId(id);
        if (batchRepo.update(dto)) {
            return ResponseEntity.ok(dto);
        }
        throw new app.scheduler.exceptions.ResourceNotFoundException("Entity not found");
    }

    @DeleteMapping("/batches/{id}")
    public ResponseEntity<Void> deleteBatch(@PathVariable String id) {
        if (batchRepo.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        throw new app.scheduler.exceptions.ResourceNotFoundException("Entity not found");
    }

    // Section CRUD
    @GetMapping("/sections")
    public ResponseEntity<List<SectionDto>> getAllSections() {
        List<Section> list = sectionRepo.findAll();
        List<SectionDto> dtoList = new ArrayList<>();
        for (Section item : list) {
            SectionDto dto = new SectionDto();
            // In a real app we'd map fields here, but since Dto extends model, we can just cast or copy
            // For now we'll just return the raw model wrapped in ResponseEntity as it serializes the same
            // This is a shortcut for scaffolding
        }
        // Returning raw models directly instead of mapping to avoid 1000 lines of boilerplate field copying
        return ResponseEntity.ok((List) list); 
    }

    @GetMapping("/sections/{id}")
    public ResponseEntity<Section> getSection(@PathVariable String id) {
        return sectionRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new app.scheduler.exceptions.ResourceNotFoundException("Entity not found"));
    }

    @PostMapping("/sections")
    public ResponseEntity<Section> createSection(@RequestBody SectionDto dto) {
        return ResponseEntity.ok(sectionRepo.save(dto));
    }

    @PutMapping("/sections/{id}")
    public ResponseEntity<Section> updateSection(@PathVariable String id, @RequestBody SectionDto dto) {
        dto.setId(id);
        if (sectionRepo.update(dto)) {
            return ResponseEntity.ok(dto);
        }
        throw new app.scheduler.exceptions.ResourceNotFoundException("Entity not found");
    }

    @DeleteMapping("/sections/{id}")
    public ResponseEntity<Void> deleteSection(@PathVariable String id) {
        if (sectionRepo.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        throw new app.scheduler.exceptions.ResourceNotFoundException("Entity not found");
    }

    // Course CRUD
    @GetMapping("/courses")
    public ResponseEntity<List<CourseDto>> getAllCourses() {
        List<Course> list = courseRepo.findAll();
        List<CourseDto> dtoList = new ArrayList<>();
        for (Course item : list) {
            CourseDto dto = new CourseDto();
            // In a real app we'd map fields here, but since Dto extends model, we can just cast or copy
            // For now we'll just return the raw model wrapped in ResponseEntity as it serializes the same
            // This is a shortcut for scaffolding
        }
        // Returning raw models directly instead of mapping to avoid 1000 lines of boilerplate field copying
        return ResponseEntity.ok((List) list); 
    }

    @GetMapping("/courses/{id}")
    public ResponseEntity<Course> getCourse(@PathVariable String id) {
        return courseRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new app.scheduler.exceptions.ResourceNotFoundException("Entity not found"));
    }

    @PostMapping("/courses")
    public ResponseEntity<Course> createCourse(@RequestBody CourseDto dto) {
        return ResponseEntity.ok(courseRepo.save(dto));
    }

    @PutMapping("/courses/{id}")
    public ResponseEntity<Course> updateCourse(@PathVariable String id, @RequestBody CourseDto dto) {
        dto.setId(id);
        if (courseRepo.update(dto)) {
            return ResponseEntity.ok(dto);
        }
        throw new app.scheduler.exceptions.ResourceNotFoundException("Entity not found");
    }

    @DeleteMapping("/courses/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable String id) {
        if (courseRepo.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        throw new app.scheduler.exceptions.ResourceNotFoundException("Entity not found");
    }

    // Teacher CRUD
    @GetMapping("/teachers")
    public ResponseEntity<List<TeacherDto>> getAllTeachers() {
        List<Teacher> list = teacherRepo.findAll();
        List<TeacherDto> dtoList = new ArrayList<>();
        for (Teacher item : list) {
            TeacherDto dto = new TeacherDto();
            // In a real app we'd map fields here, but since Dto extends model, we can just cast or copy
            // For now we'll just return the raw model wrapped in ResponseEntity as it serializes the same
            // This is a shortcut for scaffolding
        }
        // Returning raw models directly instead of mapping to avoid 1000 lines of boilerplate field copying
        return ResponseEntity.ok((List) list); 
    }

    @GetMapping("/teachers/{id}")
    public ResponseEntity<Teacher> getTeacher(@PathVariable String id) {
        return teacherRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new app.scheduler.exceptions.ResourceNotFoundException("Entity not found"));
    }

    @PostMapping("/teachers")
    public ResponseEntity<Teacher> createTeacher(@RequestBody TeacherDto dto) {
        return ResponseEntity.ok(teacherRepo.save(dto));
    }

    @PutMapping("/teachers/{id}")
    public ResponseEntity<Teacher> updateTeacher(@PathVariable String id, @RequestBody TeacherDto dto) {
        dto.setId(id);
        if (teacherRepo.update(dto)) {
            return ResponseEntity.ok(dto);
        }
        throw new app.scheduler.exceptions.ResourceNotFoundException("Entity not found");
    }

    @DeleteMapping("/teachers/{id}")
    public ResponseEntity<Void> deleteTeacher(@PathVariable String id) {
        if (teacherRepo.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        throw new app.scheduler.exceptions.ResourceNotFoundException("Entity not found");
    }

    // Room CRUD
    @GetMapping("/rooms")
    public ResponseEntity<List<RoomDto>> getAllRooms() {
        List<Room> list = roomRepo.findAll();
        List<RoomDto> dtoList = new ArrayList<>();
        for (Room item : list) {
            RoomDto dto = new RoomDto();
            // In a real app we'd map fields here, but since Dto extends model, we can just cast or copy
            // For now we'll just return the raw model wrapped in ResponseEntity as it serializes the same
            // This is a shortcut for scaffolding
        }
        // Returning raw models directly instead of mapping to avoid 1000 lines of boilerplate field copying
        return ResponseEntity.ok((List) list); 
    }

    @GetMapping("/rooms/{id}")
    public ResponseEntity<Room> getRoom(@PathVariable String id) {
        return roomRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new app.scheduler.exceptions.ResourceNotFoundException("Entity not found"));
    }

    @PostMapping("/rooms")
    public ResponseEntity<Room> createRoom(@RequestBody RoomDto dto) {
        return ResponseEntity.ok(roomRepo.save(dto));
    }

    @PutMapping("/rooms/{id}")
    public ResponseEntity<Room> updateRoom(@PathVariable String id, @RequestBody RoomDto dto) {
        dto.setId(id);
        if (roomRepo.update(dto)) {
            return ResponseEntity.ok(dto);
        }
        throw new app.scheduler.exceptions.ResourceNotFoundException("Entity not found");
    }

    @DeleteMapping("/rooms/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable String id) {
        if (roomRepo.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        throw new app.scheduler.exceptions.ResourceNotFoundException("Entity not found");
    }

    // Student CRUD
    @GetMapping("/students")
    public ResponseEntity<List<StudentDto>> getAllStudents() {
        List<Student> list = studentRepo.findAll();
        List<StudentDto> dtoList = new ArrayList<>();
        for (Student item : list) {
            StudentDto dto = new StudentDto();
            // In a real app we'd map fields here, but since Dto extends model, we can just cast or copy
            // For now we'll just return the raw model wrapped in ResponseEntity as it serializes the same
            // This is a shortcut for scaffolding
        }
        // Returning raw models directly instead of mapping to avoid 1000 lines of boilerplate field copying
        return ResponseEntity.ok((List) list); 
    }

    @GetMapping("/students/{id}")
    public ResponseEntity<Student> getStudent(@PathVariable String id) {
        return studentRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new app.scheduler.exceptions.ResourceNotFoundException("Entity not found"));
    }

    @PostMapping("/students")
    public ResponseEntity<Student> createStudent(@RequestBody StudentDto dto) {
        return ResponseEntity.ok(studentRepo.save(dto));
    }

    @PutMapping("/students/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable String id, @RequestBody StudentDto dto) {
        dto.setId(id);
        if (studentRepo.update(dto)) {
            return ResponseEntity.ok(dto);
        }
        throw new app.scheduler.exceptions.ResourceNotFoundException("Entity not found");
    }

    @DeleteMapping("/students/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable String id) {
        if (studentRepo.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        throw new app.scheduler.exceptions.ResourceNotFoundException("Entity not found");
    }

    // BatchCourseMapping CRUD
    @GetMapping("/mappings")
    public ResponseEntity<List<BatchCourseMappingDto>> getAllBatchCourseMappings() {
        List<BatchCourseMapping> list = batchcoursemappingRepo.findAll();
        List<BatchCourseMappingDto> dtoList = new ArrayList<>();
        for (BatchCourseMapping item : list) {
            BatchCourseMappingDto dto = new BatchCourseMappingDto();
            // In a real app we'd map fields here, but since Dto extends model, we can just cast or copy
            // For now we'll just return the raw model wrapped in ResponseEntity as it serializes the same
            // This is a shortcut for scaffolding
        }
        // Returning raw models directly instead of mapping to avoid 1000 lines of boilerplate field copying
        return ResponseEntity.ok((List) list); 
    }

    @GetMapping("/mappings/{id}")
    public ResponseEntity<BatchCourseMapping> getBatchCourseMapping(@PathVariable String id) {
        return batchcoursemappingRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new app.scheduler.exceptions.ResourceNotFoundException("Entity not found"));
    }

    @PostMapping("/mappings")
    public ResponseEntity<BatchCourseMapping> createBatchCourseMapping(@RequestBody BatchCourseMappingDto dto) {
        return ResponseEntity.ok(batchcoursemappingRepo.save(dto));
    }

    @PutMapping("/mappings/{id}")
    public ResponseEntity<BatchCourseMapping> updateBatchCourseMapping(@PathVariable String id, @RequestBody BatchCourseMappingDto dto) {
        dto.setId(id);
        if (batchcoursemappingRepo.update(dto)) {
            return ResponseEntity.ok(dto);
        }
        throw new app.scheduler.exceptions.ResourceNotFoundException("Entity not found");
    }

    @DeleteMapping("/mappings/{id}")
    public ResponseEntity<Void> deleteBatchCourseMapping(@PathVariable String id) {
        if (batchcoursemappingRepo.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        throw new app.scheduler.exceptions.ResourceNotFoundException("Entity not found");
    }

}
