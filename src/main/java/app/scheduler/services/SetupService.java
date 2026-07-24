package app.scheduler.services;

import app.scheduler.models.*;
import app.scheduler.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;
import java.util.List;

@Service
public class SetupService implements CommandLineRunner {

    private final SemesterRepository semesterRepo;
    private final BatchRepository batchRepo;
    private final CourseRepository courseRepo;
    private final RoomRepository roomRepo;
    private final TeacherService teacherService;
    private final StudentService studentService;
    private final BatchCourseMappingRepository mappingRepo;
    private final EventRepository eventRepo;

    public SetupService(SemesterRepository semesterRepo,
                        BatchRepository batchRepo,
                        CourseRepository courseRepo,
                        RoomRepository roomRepo,
                        TeacherService teacherService,
                        StudentService studentService,
                        BatchCourseMappingRepository mappingRepo,
                        EventRepository eventRepo) {
        this.semesterRepo = semesterRepo;
        this.batchRepo = batchRepo;
        this.courseRepo = courseRepo;
        this.roomRepo = roomRepo;
        this.teacherService = teacherService;
        this.studentService = studentService;
        this.mappingRepo = mappingRepo;
        this.eventRepo = eventRepo;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!semesterRepo.findAll().isEmpty()) {
            System.out.println("Database already seeded. Skipping SetupService.");
            return;
        }

        System.out.println("Starting SetupService to seed database...");

        // Semesters
        Semester sem1 = new Semester();
        sem1.setId("sem-1");
        sem1.setName("2026/27 First Semester");
        // We set the start date to Monday of the current week so that live map works today!
        LocalDate now = LocalDate.now();
        LocalDate startOfThisWeek = now.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        sem1.setStartDate(startOfThisWeek);
        sem1.setEndDate(startOfThisWeek.plusWeeks(16));
        sem1.setWeeks(16);
        sem1.setAcademicYear("2026/27");
        sem1.setGenerated(false);
        sem1.setActive(true);
        semesterRepo.save(sem1);

        // Batches & Sections
        Batch batch1 = new Batch();
        batch1.setId("batch-drbse");
        batch1.setName("DRBSE2502");
        batch1.setProgram("Software Engineering");
        batch1.setYear("2026/27");
        batch1.setActive(true);
        batchRepo.save(batch1);

        Section secA = new Section();
        secA.setId("sec-drbse-a");
        secA.setName("Section A");
        secA.setBatchId("batch-drbse");
        secA.setStudentCount(35);
        secA.setActive(true);
        batchRepo.saveSection(secA);

        // Rooms
        createRoom("room-201", "201", "LECTURE_ROOM", 45, 2, true);
        createRoom("room-202", "202", "LECTURE_ROOM", 45, 2, false);
        createRoom("room-lab-204", "Lab 204", "COMPUTER_LAB", 24, 2, true);
        createRoom("room-301", "301", "LECTURE_ROOM", 45, 3, true);

        // Courses
        createCourse("course-se2222", "SE2222", "Web", true, 3, 3, 2, "Software Engineering");
        createCourse("course-se1221", "SE1221", "OOP", true, 3, 3, 2, "Software Engineering");

        // Teachers
        Teacher tKibrom = createTeacher("t-kibrom", "Kibrom", "kibrom@acse.local", "Software Engineering", "LECTURER");
        Teacher tAbelti = createTeacher("t-abelti", "Abelti", "abelti@acse.local", "Software Engineering", "LAB_INSTRUCTOR");

        // Students
        createStudent("s-1001", "Alice Student", "1001", "alice@acse.local", "sec-drbse-a", "batch-drbse");

        // Mappings
        createMapping("map-web", "batch-drbse", "course-se2222", "t-kibrom", "t-abelti", "sem-1");

        // Dummy Event - let's schedule an event TODAY so the live map is populated
        int currentDayOfWeek = now.getDayOfWeek().getValue(); // 1=Mon, 7=Sun
        if (currentDayOfWeek <= 6) {
            Event e = new Event();
            e.setId("evt-live-demo");
            e.setType("Lecture");
            e.setTopic("Web Dev Intro");
            e.setSectionId("sec-drbse-a");
            e.setCourseId("course-se2222");
            e.setTeacherId("t-kibrom");
            e.setRoomId("room-201");
            e.setSemesterId("sem-1");
            e.setBatchId("batch-drbse");
            e.setDay(currentDayOfWeek);
            e.setPeriod(3); // period 3 is usually around 11:30
            e.setWeek(1);
            
            // Time logic matches the query service
            LocalTime p3Start = LocalTime.of(11, 30);
            e.setDate(sem1.getStartDate());
            e.setStatus("SCHEDULED");
            eventRepo.save(e);
        }

        System.out.println("SetupService completed successfully!");
    }

    private void createRoom(String id, String name, String type, int capacity, int level, boolean hasEquipment) {
        Room r = new Room();
        r.setId(id);
        r.setName(name);
        r.setType(type);
        r.setCapacity(capacity);
        r.setLevel(level);
        r.setHasEquipment(hasEquipment);
        r.setActive(true);
        roomRepo.save(r);
    }

    private void createCourse(String id, String code, String name, boolean hasLab, int credits, int lecHours, int labHours, String dept) {
        Course c = new Course();
        c.setId(id);
        c.setCode(code);
        c.setName(name);
        c.setHasLab(hasLab);
        c.setCredits(credits);
        c.setLectureHoursPerWeek(lecHours);
        c.setLabHoursPerWeek(labHours);
        c.setDepartment(dept);
        c.setActive(true);
        courseRepo.save(c);
    }

    private Teacher createTeacher(String id, String name, String email, String dept, String type) {
        Teacher t = new Teacher();
        t.setId(id);
        t.setName(name);
        t.setEmail(email);
        t.setDepartment(dept);
        t.setType(type);
        t.setActive(true);
        return teacherService.save(t);
    }

    private void createStudent(String id, String name, String studentId, String email, String secId, String batchId) {
        Student s = new Student();
        s.setId(id);
        s.setName(name);
        s.setStudentId(studentId);
        s.setEmail(email);
        s.setSectionId(secId);
        s.setBatchId(batchId);
        s.setActive(true);
        studentService.save(s);
    }

    private void createMapping(String id, String batchId, String courseId, String lecTeacherId, String labTeacherId, String semId) {
        BatchCourseMapping m = new BatchCourseMapping();
        m.setId(id);
        m.setBatchId(batchId);
        m.setCourseId(courseId);
        m.setLectureTeacherId(lecTeacherId);
        m.setLabInstructorId(labTeacherId);
        m.setSemesterId(semId);
        m.setRequired(true);
        mappingRepo.save(m);
    }
}
