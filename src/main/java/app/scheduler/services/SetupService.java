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
    private final StudentService studentService;
    private final LoggerService loggerService;

    public SetupService(SemesterService semesterService,
                        BatchService batchService,
                        CourseService courseService,
                        RoomService roomService,
                        TeacherService teacherService,
                        BatchCourseMappingService mappingService,
                        StudentService studentService,
                        LoggerService loggerService) {
        this.semesterService = semesterService;
        this.batchService = batchService;
        this.courseService = courseService; 
        this.roomService = roomService;
        this.teacherService = teacherService;
        this.mappingService = mappingService;
        this.studentService = studentService;
        this.loggerService = loggerService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!semesterService.findAll().isEmpty()) {
            loggerService.logSystem("Database already seeded. Skipping SetupService.");
            return;
        }

        loggerService.logSystem("Starting SetupService to seed database...");

        // Create Semesters (Make Fall2026 active by default)
        Semester fall2026 = createSemester("Fall2026", LocalDate.of(2026, 7, 27), "2026", true);
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
        Teacher tLecCS2 = createTeacher("Khaled Omar", "khaled.omar@hilcoeschool.com", "COMPUTER_SCIENCE", "LECTURE");
        Teacher tLecCS3 = createTeacher("Hassan Ibrahim", "hassan.ibrahim@hilcoeschool.com", "COMPUTER_SCIENCE", "LECTURE");
        Teacher tLecCS4 = createTeacher("Youssef Nabil", "youssef.nabil@hilcoeschool.com", "COMPUTER_SCIENCE", "LECTURE");
        Teacher tLecCS5 = createTeacher("Amr Hisham", "amr.hisham@hilcoeschool.com", "COMPUTER_SCIENCE", "LECTURE");
        Teacher tLecCS6 = createTeacher("Tariq Ali", "tariq.ali@hilcoeschool.com", "COMPUTER_SCIENCE", "LECTURE");

        Teacher tLecSWE1 = createTeacher("Sami Saad", "sami.saad@hilcoeschool.com", "SOFTWARE_ENGINEERING", "LECTURE");
        Teacher tLecSWE2 = createTeacher("Ali Ahmed", "ali.ahmed@hilcoeschool.com", "SOFTWARE_ENGINEERING", "LECTURE");
        Teacher tLecSWE3 = createTeacher("Ahmed Mohamed", "ahmed.mohamed@hilcoeschool.com", "SOFTWARE_ENGINEERING", "LECTURE");
        Teacher tLecSWE4 = createTeacher("Fady Ghabour", "fady.ghabour@hilcoeschool.com", "SOFTWARE_ENGINEERING", "LECTURE");
        Teacher tLecSWE5 = createTeacher("Mina Maher", "mina.maher@hilcoeschool.com", "SOFTWARE_ENGINEERING", "LECTURE");
        Teacher tLecSWE6 = createTeacher("Emad Youssef", "emad.youssef@hilcoeschool.com", "SOFTWARE_ENGINEERING", "LECTURE");

        Teacher tLabCS1 = createTeacher("Omar Mostafa", "omar.mostafa@hilcoeschool.com", "COMPUTER_SCIENCE", "LAB");
        Teacher tLabCS2 = createTeacher("Karim Mostafa", "karim.mostafa@hilcoeschool.com", "COMPUTER_SCIENCE", "LAB");
        Teacher tLabCS3 = createTeacher("Karim Samir", "karim.samir@hilcoeschool.com", "COMPUTER_SCIENCE", "LAB");
        Teacher tLabCS4 = createTeacher("Ziad Sherif", "ziad.sherif@hilcoeschool.com", "COMPUTER_SCIENCE", "LAB");
        Teacher tLabCS5 = createTeacher("Marwan Ali", "marwan.ali@hilcoeschool.com", "COMPUTER_SCIENCE", "LAB");
        Teacher tLabCS6 = createTeacher("Adham Fawzi", "adham.fawzi@hilcoeschool.com", "COMPUTER_SCIENCE", "LAB");

        Teacher tLabSWE1 = createTeacher("Tamer Hamed", "tamer.hamed@hilcoeschool.com", "SOFTWARE_ENGINEERING", "LAB");
        Teacher tLabSWE2 = createTeacher("Osama Samir", "osama.samir@hilcoeschool.com", "SOFTWARE_ENGINEERING", "LAB");
        Teacher tLabSWE3 = createTeacher("Rami Essam", "rami.essam@hilcoeschool.com", "SOFTWARE_ENGINEERING", "LAB");
        Teacher tLabSWE4 = createTeacher("Sherif Wagdi", "sherif.wagdi@hilcoeschool.com", "SOFTWARE_ENGINEERING", "LAB");
        Teacher tLabSWE5 = createTeacher("Hazem Magdy", "hazem.magdy@hilcoeschool.com", "SOFTWARE_ENGINEERING", "LAB");
        Teacher tLabSWE6 = createTeacher("Wael Salah", "wael.salah@hilcoeschool.com", "SOFTWARE_ENGINEERING", "LAB");

        // Create Courses
        Course cs101 = createCourse("Computer Science Fundamentals", "CS101", "COMPUTER_SCIENCE", 3, false);
        Course cs102 = createCourse("Programming Concepts", "CS102", "COMPUTER_SCIENCE", 3, true);
        Course cs103 = createCourse("Data Structures", "CS103", "COMPUTER_SCIENCE", 3, true);
        Course cs104 = createCourse("Algorithms", "CS104", "COMPUTER_SCIENCE", 3, true);
        Course cs105 = createCourse("Database Systems", "CS105", "COMPUTER_SCIENCE", 3, true);
        
        Course cs106 = createCourse("Computer Networks", "CS106", "COMPUTER_SCIENCE", 3, true);
        Course cs107 = createCourse("Operating Systems", "CS107", "COMPUTER_SCIENCE", 3, true);
        Course cs108 = createCourse("Artificial Intelligence", "CS108", "COMPUTER_SCIENCE", 3, true);
        Course cs109 = createCourse("Machine Learning", "CS109", "COMPUTER_SCIENCE", 3, true);
        Course cs110 = createCourse("Web Development", "CS110", "COMPUTER_SCIENCE", 3, false);
        Course cs111 = createCourse("Mobile Development", "CS111", "COMPUTER_SCIENCE", 3, false);
        Course cs112 = createCourse("Computer Graphics", "CS112", "COMPUTER_SCIENCE", 3, false);
        Course cs113 = createCourse("Cybersecurity", "CS113", "COMPUTER_SCIENCE", 3, false);
        Course cs114 = createCourse("Cloud Computing", "CS114", "COMPUTER_SCIENCE", 3, false);
        createCourse("Blockchain Systems", "CS115", "COMPUTER_SCIENCE", 3, false);
        createCourse("Internet of Things", "CS116", "COMPUTER_SCIENCE", 3, false);
        createCourse("AR/VR Design", "CS117", "COMPUTER_SCIENCE", 3, false);
        createCourse("Quantum Computing", "CS118", "COMPUTER_SCIENCE", 3, false);
        createCourse("DevOps Practices", "CS119", "COMPUTER_SCIENCE", 3, false);
        createCourse("Human-Computer Interaction", "CS120", "COMPUTER_SCIENCE", 3, false);

        Course swe201 = createCourse("Software Engineering Principles", "SWE201", "SOFTWARE_ENGINEERING", 3, false);
        Course swe202 = createCourse("Software Testing", "SWE202", "SOFTWARE_ENGINEERING", 3, true);
        Course swe203 = createCourse("Software Architecture", "SWE203", "SOFTWARE_ENGINEERING", 3, true);
        
        Course swe204 = createCourse("Project Management", "SWE204", "SOFTWARE_ENGINEERING", 3, false);
        Course swe205 = createCourse("Quality Assurance", "SWE205", "SOFTWARE_ENGINEERING", 3, true);
        Course swe206 = createCourse("Configuration Management", "SWE206", "SOFTWARE_ENGINEERING", 3, true);
        Course swe207 = createCourse("Process Improvement", "SWE207", "SOFTWARE_ENGINEERING", 3, false);
        Course swe208 = createCourse("Software Metrics", "SWE208", "SOFTWARE_ENGINEERING", 3, false);
        Course swe209 = createCourse("Requirements Engineering", "SWE209", "SOFTWARE_ENGINEERING", 3, false);
        Course swe210 = createCourse("Software Security", "SWE210", "SOFTWARE_ENGINEERING", 3, true);

        // Auto-seed Course Mappings so algorithm can run immediately without manual config
        // Batch 1: DRB2502 (CS) - 3 Lab, 2 Theory
        createMapping(drb2502.getId(), cs102.getId(), tLecCS1.getId(), tLabCS1.getId(), fall2026.getId()); // Lab
        createMapping(drb2502.getId(), cs103.getId(), tLecCS2.getId(), tLabCS2.getId(), fall2026.getId()); // Lab
        createMapping(drb2502.getId(), cs104.getId(), tLecCS3.getId(), tLabCS3.getId(), fall2026.getId()); // Lab
        createMapping(drb2502.getId(), cs101.getId(), tLecCS4.getId(), null, fall2026.getId());            // Theory
        createMapping(drb2502.getId(), cs110.getId(), tLecCS5.getId(), null, fall2026.getId());            // Theory

        // Batch 2: DRB2503 (CS) - 3 Lab, 2 Theory
        createMapping(drb2503.getId(), cs105.getId(), tLecCS4.getId(), tLabCS4.getId(), fall2026.getId()); // Lab
        createMapping(drb2503.getId(), cs106.getId(), tLecCS5.getId(), tLabCS5.getId(), fall2026.getId()); // Lab
        createMapping(drb2503.getId(), cs107.getId(), tLecCS6.getId(), tLabCS6.getId(), fall2026.getId()); // Lab
        createMapping(drb2503.getId(), cs111.getId(), tLecCS1.getId(), null, fall2026.getId());            // Theory
        createMapping(drb2503.getId(), cs112.getId(), tLecCS2.getId(), null, fall2026.getId());            // Theory

        // Batch 3: DRBSE2502 (SWE) - 3 Lab, 2 Theory
        createMapping(drbse2502.getId(), swe202.getId(), tLecSWE1.getId(), tLabSWE1.getId(), fall2026.getId()); // Lab
        createMapping(drbse2502.getId(), swe203.getId(), tLecSWE2.getId(), tLabSWE2.getId(), fall2026.getId()); // Lab
        createMapping(drbse2502.getId(), swe205.getId(), tLecSWE3.getId(), tLabSWE3.getId(), fall2026.getId()); // Lab
        createMapping(drbse2502.getId(), swe201.getId(), tLecSWE4.getId(), null, fall2026.getId());             // Theory
        createMapping(drbse2502.getId(), swe204.getId(), tLecSWE5.getId(), null, fall2026.getId());             // Theory

        // Batch 4: DRBSE2503 (SWE) - 3 Lab, 2 Theory
        createMapping(drbse2503.getId(), swe206.getId(), tLecSWE4.getId(), tLabSWE4.getId(), fall2026.getId()); // Lab
        createMapping(drbse2503.getId(), swe210.getId(), tLecSWE5.getId(), tLabSWE5.getId(), fall2026.getId()); // Lab
        createMapping(drbse2503.getId(), swe202.getId(), tLecSWE6.getId(), tLabSWE6.getId(), fall2026.getId()); // Lab (Reusing course for demo)
        createMapping(drbse2503.getId(), swe207.getId(), tLecSWE1.getId(), null, fall2026.getId());             // Theory
        createMapping(drbse2503.getId(), swe208.getId(), tLecSWE2.getId(), null, fall2026.getId());             // Theory

        // Create 5 students for DRB2502 batch
        java.util.List<Section> drb2502Sections = batchService.findSectionsByBatchId(drb2502.getId());
        String sec2502A = !drb2502Sections.isEmpty() ? drb2502Sections.get(0).getId() : null;
        String sec2502B = drb2502Sections.size() > 1 ? drb2502Sections.get(1).getId() : null;

        createStudent("Abebe Bikila", "CS2025001", "abebe.bikila@hilcoeschool.com", drb2502.getId(), sec2502A, 1);
        createStudent("Bethlehem Tadesse", "CS2025002", "bethlehem.tadesse@hilcoeschool.com", drb2502.getId(), sec2502A, 2);
        createStudent("Chala Alemu", "CS2025003", "chala.alemu@hilcoeschool.com", drb2502.getId(), sec2502B, 1);
        createStudent("Danait Gebre", "CS2025004", "danait.gebre@hilcoeschool.com", drb2502.getId(), sec2502B, 2);
        createStudent("Ephrem Solomon", "CS2025005", "ephrem.solomon@hilcoeschool.com", drb2502.getId(), sec2502A, 1);

        // Create 5 students for DRBSE2502 batch
        java.util.List<Section> drbse2502Sections = batchService.findSectionsByBatchId(drbse2502.getId());
        String secSE2502A = !drbse2502Sections.isEmpty() ? drbse2502Sections.get(0).getId() : null;
        String secSE2502B = drbse2502Sections.size() > 1 ? drbse2502Sections.get(1).getId() : null;

        createStudent("Fikru Tefera", "SWE2025001", "fikru.tefera@hilcoeschool.com", drbse2502.getId(), secSE2502A, 1);
        createStudent("Gifty Haile", "SWE2025002", "gifty.haile@hilcoeschool.com", drbse2502.getId(), secSE2502A, 2);
        createStudent("Hannah Worku", "SWE2025003", "hannah.worku@hilcoeschool.com", drbse2502.getId(), secSE2502B, 1);
        createStudent("Isaac Berhanu", "SWE2025004", "isaac.berhanu@hilcoeschool.com", drbse2502.getId(), secSE2502B, 2);
        createStudent("Jemila Nuru", "SWE2025005", "jemila.nuru@hilcoeschool.com", drbse2502.getId(), secSE2502A, 1);

        loggerService.logSystem("SetupService successfully seeded Fall2026 active semester mappings and students.");
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

    private void createStudent(String name, String studentId, String email, String batchId, String sectionId, int labGroup) {
        Student student = new Student();
        student.setName(name);
        student.setStudentId(studentId);
        student.setEmail(email);
        student.setBatchId(batchId);
        student.setSectionId(sectionId);
        student.setLabGroup(labGroup);
        studentService.save(student);
    }

    private void createStudent(String name, String studentId, String email, String batchId, int labGroup) {
        createStudent(name, studentId, email, batchId, null, labGroup);
    }
}
