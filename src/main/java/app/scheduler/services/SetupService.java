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

    // fix constructor to create services instead of repos
    public SetupService(SemesterService semesterService,
                        BatchService batchService,
                        CourseService courseService,
                        RoomService roomService,
                        TeacherService teacherService) {
        this.semesterService = semesterService;
        this.batchService = batchService;
        this.courseService = courseService;
        this.roomService = roomService;
        this.teacherService = teacherService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!semesterService.findAll().isEmpty()) {
            System.out.println("Database already seeded. Skipping SetupService.");
            return;
        }

        System.out.println("Starting SetupService to seed database...");
        // create 3 semesters, 4 batches, 10 courses, 15 rooms, 10 teachers

        createSemester("Fall2026", LocalDate.of(2025, 8, 1), "2026");
        createSemester("Spring2027", LocalDate.of(2026, 1, 15), "2027");
        createSemester("Summer2027", LocalDate.of(2027, 5, 20), "2027");

        createBatch("DRB2502", "COMPUTER_SCIENCE", 2, "2025");
        createBatch("DRB2503", "COMPUTER_SCIENCE", 2, "2025");
        createBatch("DRBSE2502", "SOFTWARE_ENGINEERING", 2, "2025");
        createBatch("DRBSE2503", "SOFTWARE_ENGINEERING", 2, "2025");

        createRoom("LR201","LECTURE",50,2,false);
        createRoom("LR202","LECTURE",50,2,true);
        createRoom("LR203","LECTURE",50,2,true);
        createRoom("LR204","LAB",50,2,true);

        createRoom("LR303","LECTURE",50,3,false);
        createRoom("LR302","LECTURE",50,3,false);
        createRoom("LR301","LECTURE",50,3,false);
        createRoom("LR304","LAB",50,3,true);


        createRoom("LR402","LECTURE",50,4,false);
        createRoom("LR401","LECTURE",50,4,false);
        createRoom("LR404","LAB",50,4,true);

        
        createRoom("LR504","LAB",50,5,true);

        // createRoom("LR501","LECTURE",50,5,false);
        // createRoom("LR502","LECTURE",50,5,false);
        // createRoom("LR503","LECTURE",50,5,false);

        createRoom("LR601","LECTURE",50,2,false);

        createRoom("LIBRARY","LIBRARY",50,5,false);

        // create 6 Lecture teachers and 5 lab teachers of alternating department cs ans swe and emails in lower case and _ between words name@hilcoeschool.com and name as two words first last
        createTeacher("Mohamed Abdalla", "mohamed.abdalla@hilcoeschool.com", "COMPUTER_SCIENCE", "LECTURE");
        createTeacher("Sami Saad", "sami.saad@hilcoeschool.com", "SOFTWARE_ENGINEERING", "LECTURE");
        createTeacher("Khaled Omar", "khaled.omar@hilcoeschool.com", "COMPUTER_SCIENCE", "LECTURE");
        createTeacher("Ali Ahmed", "ali.ahmed@hilcoeschool.com", "SOFTWARE_ENGINEERING", "LECTURE");
        createTeacher("Hassan Ibrahim", "hassan.ibrahim@hilcoeschool.com", "COMPUTER_SCIENCE", "LECTURE");
        createTeacher("Ahmed Mohamed", "ahmed.mohamed@hilcoeschool.com", "SOFTWARE_ENGINEERING", "LECTURE");

        createTeacher("Omar Mostafa", "omar.mostafa@hilcoeschool.com", "COMPUTER_SCIENCE", "LAB");
        createTeacher("Tamer Hamed", "tamer.hamed@hilcoeschool.com", "SOFTWARE_ENGINEERING", "LAB");
        createTeacher("Karim Mostafa", "karim.mostafa@hilcoeschool.com", "COMPUTER_SCIENCE", "LAB");
        createTeacher("Osama Samir", "osama.samir@hilcoeschool.com", "SOFTWARE_ENGINEERING", "LAB");
        createTeacher("Karim Samir", "karim.samir@hilcoeschool.com", "COMPUTER_SCIENCE", "LAB");

        // create 20 CS/SWE courses with unique codes and names, and more courses without labs
        createCourse("Computer Science Fundamentals", "CS101", "COMPUTER_SCIENCE", 3, false);
        createCourse("Programming Concepts", "CS102", "COMPUTER_SCIENCE", 3, true);
        createCourse("Data Structures", "CS103", "COMPUTER_SCIENCE", 3, true);
        createCourse("Algorithms", "CS104", "COMPUTER_SCIENCE", 3, true);
        createCourse("Database Systems", "CS105", "COMPUTER_SCIENCE", 3, true);
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

        // software engineering courses
        createCourse("Software Engineering Principles", "SWE201", "SOFTWARE_ENGINEERING", 3, false);
        createCourse("Software Testing", "SWE202", "SOFTWARE_ENGINEERING", 3, true);
        createCourse("Software Architecture", "SWE203", "SOFTWARE_ENGINEERING", 3, true);
        createCourse("Project Management", "SWE204", "SOFTWARE_ENGINEERING", 3, false);
        createCourse("Quality Assurance", "SWE205", "SOFTWARE_ENGINEERING", 3, true);
        createCourse("Configuration Management", "SWE206", "SOFTWARE_ENGINEERING", 3, true);
        createCourse("Process Improvement", "SWE207", "SOFTWARE_ENGINEERING", 3, false);
        createCourse("Software Metrics", "SWE208", "SOFTWARE_ENGINEERING", 3, false);
        createCourse("Requirements Engineering", "SWE209", "SOFTWARE_ENGINEERING", 3, false);
        createCourse("Software Security", "SWE210", "SOFTWARE_ENGINEERING", 3, true);
    }



    // create methods with params to create data for courses,batches,teachers,rooms,and semesters using setters and getters on the new object and pass it to the service.(one)
    // and use the methods to insert multiple data of each type
    private void createSemester(String name, LocalDate startDate,String academicYear) {
        Semester semester = new Semester();
        semester.setName(name);
        semester.setStartDate(startDate);
        semester.setActive(false);
        semester.setGenerated(false);
        semester.setWeeks(16);
        semester.setAcademicYear(academicYear);
        semesterService.save(semester);
    }

    private void createBatch(String name,String program,int sectionCount,String year) {
        Batch batch = new Batch();
        batch.setName(name);
        batch.setYear(year);
        batch.setProgram(program);
        batch.setSectionCount(sectionCount);
        batchService.save(batch);

    }

    private void createCourse(String name, String code, String department, int credits,boolean hasLab) {
        Course course = new Course();
        course.setName(name);
        course.setCode(code);
        course.setDepartment(department);
        course.setCredits(credits);
        course.setHasLab(hasLab);
        courseService.save(course);
    }

    private void createRoom(String name, String type, int capacity,int level,boolean hasEquipment) {
        Room room = new Room();
        room.setName(name);
        room.setType(type);
        room.setCapacity(capacity);
        room.setLevel(level);
        room.setHasEquipment(hasEquipment);
        roomService.save(room);
    }

    private void createTeacher(String name, String email, String department, String type) {
        Teacher teacher = new Teacher();
        teacher.setName(name);
        teacher.setEmail(email);
        teacher.setDepartment(department);
        teacher.setType(type);
        teacherService.save(teacher);
    }

        
    
    
        


    
}
