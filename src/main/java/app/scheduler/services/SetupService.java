package app.scheduler.services;

import app.scheduler.models.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class SetupService implements CommandLineRunner {

    private final SemesterService semesterService;
    private final BatchService batchService;
    private final CourseService courseService;
    private final RoomService roomService;
    private final TeacherService teacherService;
    private final BatchCourseMappingService mappingService;

    public SetupService(SemesterService semesterService,
                        BatchService batchService,
                        CourseService courseService,
                        RoomService roomService,
                        TeacherService teacherService,
                        BatchCourseMappingService mappingService) {
        this.semesterService = semesterService;
        this.batchService = batchService;
        this.courseService = courseService;
        this.roomService = roomService;
        this.teacherService = teacherService;
        this.mappingService = mappingService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!semesterService.findAll().isEmpty()) {
            System.out.println("Database already seeded. Skipping SetupService.");
            return;
        }

        System.out.println("Starting SetupService to seed database...");

        // Create Semesters (Make Fall2026 active by default)
        Semester fall2026 = createSemester("Fall2026", LocalDate.of(2025, 8, 1), "2026", true);
        Semester spring2027 = createSemester("Spring2027", LocalDate.of(2026, 1, 15), "2027", false);
        Semester summer2027 = createSemester("Summer2027", LocalDate.of(2027, 5, 20), "2027", false);

        // Create Batches
        Batch drb2502 = createBatch("DRB2502", "COMPUTER_SCIENCE", 2, "2025");
        Batch drb2503 = createBatch("DRB2503", "COMPUTER_SCIENCE", 2, "2025");
        Batch drbse2502 = createBatch("DRBSE2502", "SOFTWARE_ENGINEERING", 2, "2025");
        Batch drbse2503 = createBatch("DRBSE2503", "SOFTWARE_ENGINEERING", 2, "2025");

        // Create Rooms
        createRoom("LR201", "LECTURE", 50, 2, false);
        createRoom("LR202", "LECTURE", 50, 2, true);
        createRoom("LR203", "LECTURE", 50, 2, true);
        createRoom("LR204", "LAB", 50, 2, true);

        createRoom("LR303", "LECTURE", 50, 3, false);
        createRoom("LR302", "LECTURE", 50, 3, false);
        createRoom("LR301", "LECTURE", 50, 3, false);
        createRoom("LR304", "LAB", 50, 3, true);

        createRoom("LR402", "LECTURE", 50, 4, false);
        createRoom("LR401", "LECTURE", 50, 4, false);
        createRoom("LR404", "LAB", 50, 4, true);

        createRoom("LR504", "LAB", 50, 5, true);
        createRoom("LR601", "LECTURE", 50, 2, false);
        createRoom("LIBRARY", "LIBRARY", 50, 5, false);

        // Create Teachers
        Teacher tLecCS1 = createTeacher("Mohamed Abdalla", "mohamed.abdalla@hilcoeschool.com", "COMPUTER_SCIENCE", "LECTURE");
        Teacher tLecSWE1 = createTeacher("Sami Saad", "sami.saad@hilcoeschool.com", "SOFTWARE_ENGINEERING", "LECTURE");
        Teacher tLecCS2 = createTeacher("Khaled Omar", "khaled.omar@hilcoeschool.com", "COMPUTER_SCIENCE", "LECTURE");
        Teacher tLecSWE2 = createTeacher("Ali Ahmed", "ali.ahmed@hilcoeschool.com", "SOFTWARE_ENGINEERING", "LECTURE");
        Teacher tLecCS3 = createTeacher("Hassan Ibrahim", "hassan.ibrahim@hilcoeschool.com", "COMPUTER_SCIENCE", "LECTURE");
        Teacher tLecSWE3 = createTeacher("Ahmed Mohamed", "ahmed.mohamed@hilcoeschool.com", "SOFTWARE_ENGINEERING", "LECTURE");

        Teacher tLabCS1 = createTeacher("Omar Mostafa", "omar.mostafa@hilcoeschool.com", "COMPUTER_SCIENCE", "LAB");
        Teacher tLabSWE1 = createTeacher("Tamer Hamed", "tamer.hamed@hilcoeschool.com", "SOFTWARE_ENGINEERING", "LAB");
        Teacher tLabCS2 = createTeacher("Karim Mostafa", "karim.mostafa@hilcoeschool.com", "COMPUTER_SCIENCE", "LAB");
        Teacher tLabSWE2 = createTeacher("Osama Samir", "osama.samir@hilcoeschool.com", "SOFTWARE_ENGINEERING", "LAB");
        Teacher tLabCS3 = createTeacher("Karim Samir", "karim.samir@hilcoeschool.com", "COMPUTER_SCIENCE", "LAB");

        // Create Courses
        Course cs101 = createCourse("Computer Science Fundamentals", "CS101", "COMPUTER_SCIENCE", 3, false);
        Course cs102 = createCourse("Programming Concepts", "CS102", "COMPUTER_SCIENCE", 3, true);
        Course cs103 = createCourse("Data Structures", "CS103", "COMPUTER_SCIENCE", 3, true);
        Course cs104 = createCourse("Algorithms", "CS104", "COMPUTER_SCIENCE", 3, true);
        Course cs105 = createCourse("Database Systems", "CS105", "COMPUTER_SCIENCE", 3, true);
        
        createCourse("Computer Networks", "CS106", "COMPUTER_SCIENCE", 3, true);
        createCourse("Operating Systems", "CS107", "COMPUTER_SCIENCE", 3, true);
        createCourse("Artificial Intelligence", "CS108", "COMPUTER_SCIENCE", 3, true);
        createCourse("Machine Learning", "CS109", "COMPUTER_SCIENCE", 3, true);
        createCourse("Web Development", "CS110", "COMPUTER_SCIENCE", 3, false);
        createCourse("Mobile Development", "CS111", "COMPUTER_SCIENCE", 3, false);
        createCourse("Computer Graphics", "CS112", "COMPUTER_SCIENCE", 3, false);
        createCourse("Cybersecurity", "CS113", "COMPUTER_SCIENCE", 3, false);
        createCourse("Cloud Computing", "CS114", "COMPUTER_SCIENCE", 3, false);
        createCourse("Blockchain Systems", "CS115", "COMPUTER_SCIENCE", 3, false);
        createCourse("Internet of Things", "CS116", "COMPUTER_SCIENCE", 3, false);
        createCourse("AR/VR Design", "CS117", "COMPUTER_SCIENCE", 3, false);
        createCourse("Quantum Computing", "CS118", "COMPUTER_SCIENCE", 3, false);
        createCourse("DevOps Practices", "CS119", "COMPUTER_SCIENCE", 3, false);
        createCourse("Human-Computer Interaction", "CS120", "COMPUTER_SCIENCE", 3, false);

        Course swe201 = createCourse("Software Engineering Principles", "SWE201", "SOFTWARE_ENGINEERING", 3, false);
        Course swe202 = createCourse("Software Testing", "SWE202", "SOFTWARE_ENGINEERING", 3, true);
        Course swe203 = createCourse("Software Architecture", "SWE203", "SOFTWARE_ENGINEERING", 3, true);
        
        createCourse("Project Management", "SWE204", "SOFTWARE_ENGINEERING", 3, false);
        createCourse("Quality Assurance", "SWE205", "SOFTWARE_ENGINEERING", 3, true);
        createCourse("Configuration Management", "SWE206", "SOFTWARE_ENGINEERING", 3, true);
        createCourse("Process Improvement", "SWE207", "SOFTWARE_ENGINEERING", 3, false);
        createCourse("Software Metrics", "SWE208", "SOFTWARE_ENGINEERING", 3, false);
        createCourse("Requirements Engineering", "SWE209", "SOFTWARE_ENGINEERING", 3, false);
        createCourse("Software Security", "SWE210", "SOFTWARE_ENGINEERING", 3, true);

        // Auto-seed Course Mappings so algorithm can run immediately without manual config
        createMapping(drb2502.getId(), cs101.getId(), tLecCS1.getId(), null, fall2026.getId());
        createMapping(drb2502.getId(), cs102.getId(), tLecCS1.getId(), tLabCS1.getId(), fall2026.getId());
        createMapping(drb2502.getId(), cs103.getId(), tLecCS2.getId(), tLabCS2.getId(), fall2026.getId());

        createMapping(drb2503.getId(), cs104.getId(), tLecCS2.getId(), tLabCS2.getId(), fall2026.getId());
        createMapping(drb2503.getId(), cs105.getId(), tLecCS3.getId(), tLabCS3.getId(), fall2026.getId());

        createMapping(drbse2502.getId(), swe201.getId(), tLecSWE1.getId(), null, fall2026.getId());
        createMapping(drbse2502.getId(), swe202.getId(), tLecSWE2.getId(), tLabSWE1.getId(), fall2026.getId());

        createMapping(drbse2503.getId(), swe203.getId(), tLecSWE3.getId(), tLabSWE2.getId(), fall2026.getId());
        
        System.out.println("SetupService successfully seeded Fall2026 active semester mappings.");
    }

    private Semester createSemester(String name, LocalDate startDate, String academicYear, boolean isActive) {
        Semester semester = new Semester();
        semester.setName(name);
        semester.setStartDate(startDate);
        semester.setActive(isActive);
        semester.setGenerated(false);
        semester.setWeeks(16);
        semester.setAcademicYear(academicYear);
        return semesterService.save(semester);
    }

    private Batch createBatch(String name, String program, int sectionCount, String year) {
        Batch batch = new Batch();
        batch.setName(name);
        batch.setYear(year);
        batch.setProgram(program);
        batch.setSectionCount(sectionCount);
        return batchService.save(batch);
    }

    private Course createCourse(String name, String code, String department, int credits, boolean hasLab) {
        Course course = new Course();
        course.setName(name);
        course.setCode(code);
        course.setDepartment(department);
        course.setCredits(credits);
        course.setHasLab(hasLab);
        return courseService.save(course);
    }

    private Room createRoom(String name, String type, int capacity, int level, boolean hasEquipment) {
        Room room = new Room();
        room.setName(name);
        room.setType(type);
        room.setCapacity(capacity);
        room.setLevel(level);
        room.setHasEquipment(hasEquipment);
        return roomService.save(room);
    }

    private Teacher createTeacher(String name, String email, String department, String type) {
        Teacher teacher = new Teacher();
        teacher.setName(name);
        teacher.setEmail(email);
        teacher.setDepartment(department);
        teacher.setType(type);
        return teacherService.save(teacher);
    }

    private void createMapping(String batchId, String courseId, String lectureTeacherId, String labInstructorId, String semesterId) {
        BatchCourseMapping mapping = new BatchCourseMapping();
        mapping.setBatchId(batchId);
        mapping.setCourseId(courseId);
        mapping.setLectureTeacherId(lectureTeacherId);
        mapping.setLabInstructorId(labInstructorId);
        mapping.setSemesterId(semesterId);
        mappingService.save(mapping);
    }
}
