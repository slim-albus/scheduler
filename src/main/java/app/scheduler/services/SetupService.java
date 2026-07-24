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
        
        sem1.setWeeks(16);
        sem1.setAcademicYear("2026/27");
        sem1.setGenerated(false);
        semesterRepo.save(sem1);

        // Batches & Sections
        Batch batch1 = new Batch();
        batch1.setId("batch-drbse");
        batch1.setName("DRBSE2502");
        batch1.setProgram("Software Engineering");
        batch1.setYear("2026/27");
        batchRepo.save(batch1);

        Section secA = new Section();
        secA.setId("sec-drbse-a");
        secA.setName("Section A");
        secA.setBatchId("batch-drbse");
        secA.setStudentCount(35);
        batchRepo.saveSection(secA);

        Section secB = new Section();
        secB.setId("sec-drbse-b");
        secB.setName("Section B");
        secB.setBatchId("batch-drbse");
        secB.setStudentCount(35);
        batchRepo.saveSection(secB);

        // Rooms (201-601, Lab 204-504)
        createRoom("room-201", "201", "LECTURE_ROOM", 45, 2, true);
        createRoom("room-301", "301", "LECTURE_ROOM", 45, 3, true);
        createRoom("room-401", "401", "LECTURE_ROOM", 45, 4, true);
        createRoom("room-501", "501", "LECTURE_ROOM", 45, 5, true);
        createRoom("room-601", "601", "LECTURE_ROOM", 45, 6, true);
        
        createRoom("room-lab-204", "Lab 204", "COMPUTER_LAB", 30, 2, true);
        createRoom("room-lab-304", "Lab 304", "COMPUTER_LAB", 30, 3, true);
        createRoom("room-lab-404", "Lab 404", "COMPUTER_LAB", 30, 4, true);
        createRoom("room-lab-504", "Lab 504", "COMPUTER_LAB", 30, 5, true);

        // Courses
        createCourse("course-se2222", "SE2222", "Web Development", true, 3, "Software Engineering");
        createCourse("course-se1221", "SE1221", "Object Oriented Programming", true, 3, "Software Engineering");
        createCourse("course-cc2131", "CC2131", "Data Structures", false, 3, "Computer Science");
        createCourse("course-cc0193", "CC0193", "Database Systems", true, 4, "Computer Science");
        createCourse("course-cc0197", "CC0197", "Operating Systems", false, 3, "Computer Science");

        // Teachers: Kibrom, Abelti, Nesredin, Betsi, Gech, Tewlde, Yirga
        Teacher tKibrom = createTeacher("t-kibrom", "Kibrom", "kibrom@acse.local", "Software Engineering", "LECTURER");
        Teacher tAbelti = createTeacher("t-abelti", "Abelti", "abelti@acse.local", "Software Engineering", "LAB_INSTRUCTOR");
        Teacher tNesredin = createTeacher("t-nesredin", "Nesredin", "nesredin@acse.local", "Computer Science", "LECTURER");
        Teacher tBetsi = createTeacher("t-betsi", "Betsi", "betsi@acse.local", "Computer Science", "LAB_INSTRUCTOR");
        Teacher tGech = createTeacher("t-gech", "Gech", "gech@acse.local", "Computer Science", "LECTURER");
        Teacher tTewlde = createTeacher("t-tewlde", "Tewlde", "tewlde@acse.local", "Software Engineering", "LECTURER");
        Teacher tYirga = createTeacher("t-yirga", "Yirga", "yirga@acse.local", "Computer Science", "LECTURER");

        // Students
        createStudent("s-1001", "Alice Student", "1001", "alice@acse.local", "sec-drbse-a", "batch-drbse");
        createStudent("s-1002", "Bob Student", "1002", "bob@acse.local", "sec-drbse-b", "batch-drbse");

        // Mappings
        createMapping("map-1", "batch-drbse", "course-se2222", "t-kibrom", "t-abelti", "sem-1");
        createMapping("map-2", "batch-drbse", "course-se1221", "t-tewlde", "t-abelti", "sem-1");
        createMapping("map-3", "batch-drbse", "course-cc2131", "t-nesredin", null, "sem-1");
        createMapping("map-4", "batch-drbse", "course-cc0193", "t-gech", "t-betsi", "sem-1");
        createMapping("map-5", "batch-drbse", "course-cc0197", "t-yirga", null, "sem-1");

        // Dummy Event - let's schedule an event TODAY so the live map is populated
        int currentDayOfWeek = now.getDayOfWeek().getValue(); // 1=Mon, 7=Sun
        if (currentDayOfWeek <= 6) {
            for (int i = 1; i <= 5; i++) {
                Event e = new Event();
                e.setId("evt-live-demo-" + i);
                e.setType(i % 2 == 0 ? "Lab" : "Lecture");
                e.setTopic(i % 2 == 0 ? "Web Dev Lab" : "Web Dev Intro");
                e.setSectionId(i % 2 == 0 ? "sec-drbse-b" : "sec-drbse-a");
                e.setCourseId("course-se2222");
                e.setTeacherId("t-kibrom");
                e.setRoomId(i % 2 == 0 ? "room-lab1" : "room-201");
                e.setSemesterId("sem-1");
                e.setBatchId("batch-drbse");
                e.setDay(currentDayOfWeek);
                e.setPeriod(i);
                e.setWeek(1);
                e.setDate(sem1.getStartDate().plusDays(currentDayOfWeek - 1));
                e.setStatus("SCHEDULED");
                eventRepo.save(e);
            }
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
        roomRepo.save(r);
    }

    private void createCourse(String id, String code, String name, boolean hasLab, int credits, String dept) {
        Course c = new Course();
        c.setId(id);
        c.setCode(code);
        c.setName(name);
        c.setHasLab(hasLab);
        c.setCredits(credits);
        c.setDepartment(dept);
        courseRepo.save(c);
    }

    private Teacher createTeacher(String id, String name, String email, String dept, String type) {
        Teacher t = new Teacher();
        t.setId(id);
        t.setName(name);
        t.setEmail(email);
        t.setDepartment(dept);
        t.setType(type);
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
