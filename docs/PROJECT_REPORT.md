# Project Report: University Timetable & Resource Scheduler System

**Course**: Object-Oriented Programming in Java  
**Project Title**: Automated University Timetable & Resource Management System  
**Architecture**: Java Spring Boot Backend (SQLite JDBC Persistence) & Next.js Frontend Web Application  

---

## 1. Introduction and Objectives

### 1.1 Introduction
Educational institutions require academic timetables that coordinate course offerings, assigned professors, student sections, and physical campus spaces (lecture halls and lab rooms). Creating valid schedules manually is error-prone, requiring administrators to resolve conflicting room bookings, instructor double-bookings, and section period overlaps across multi-week academic terms.

The **University Timetable & Resource Scheduler System** is an enterprise-grade Java application designed to solve this constraint satisfaction problem. The system combines an object-oriented Java Spring Boot backend, a Constraint Satisfaction Problem (CSP) solver engine with Forward Checking and Minimum Remaining Values (MRV) heuristics, an SQLite relational database via JDBC, and a responsive Next.js web application.

### 1.2 Objectives
* **Object-Oriented Architecture**: Build a maintainable Java application implementing core OOP principles: encapsulation, abstraction, inheritance, polymorphism, and strict separation of concerns.
* **Automated Schedule Generation**: Develop an algorithmic solver capable of assigning courses to rooms and time slots without violating hard operational constraints.
* **Complete Data Lifecycle Management**: Provide administrative REST API endpoints and web views for managing semesters, batches, sections, courses, rooms, teachers, and students.
* **Dynamic Rescheduling & Conflict Resolution**: Allow teachers and administrators to request session rescheduling or book supplementary sessions with real-time conflict checking.
* **Visual Timetables & Building Occupation Mapping**: Render weekly timetable grids for students, teachers, and administrators alongside real-time building room occupation maps.

---

## 2. System Overview

### 2.1 System Architecture
The application uses a decoupled 3-tier architecture:

```text
┌─────────────────────────────────────────────────────────────┐
│                 Next.js 15 Frontend Web Application         │
│   (Timetable Grid, Live Building Map, Reschedule Modals)   │
└──────────────────────────────┬──────────────────────────────┘
                               │ HTTP / REST API (JSON)
                               ▼
┌─────────────────────────────────────────────────────────────┐
│               Java Spring Boot 3.4+ Backend API             │
│ ┌─────────────────────────────────────────────────────────┐ │
│ │ Web & Security: AuthInterceptor, GlobalExceptionHandler │ │
│ ├─────────────────────────────────────────────────────────┤ │
│ │ Controllers: ScheduleController, AdminControllers, Auth │ │
│ ├─────────────────────────────────────────────────────────┤ │
│ │ Services: ScheduleService, AuthService, SetupService    │ │
│ ├─────────────────────────────────────────────────────────┤ │
│ │ Algorithm Engine: CspScheduleGenerator (CSP/MRV)   │ │
│ ├─────────────────────────────────────────────────────────┤ │
│ │ Persistence: JDBC Repositories & SQL Queries            │ │
│ └─────────────────────────────────────────────────────────┘ │
└──────────────────────────────┬──────────────────────────────┘
                               │ JDBC SQL
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                  SQLite Relational Database                 │
│      (events, users, courses, rooms, batches, students)     │
└─────────────────────────────────────────────────────────────┘
```

1. **Presentation Layer (Frontend)**: Developed with Next.js 15 (App Router), TypeScript, and Tailwind CSS. Provides interactive views for timetables, building occupation maps, and administrative resource management.
2. **Application & Service Layer (Backend)**: Built with Java 25 and Spring Boot. Contains REST controllers, JWT-based security interceptors, business services, and the schedule generation algorithm engine.
3. **Data Access Layer (Database)**: SQLite database accessed via Spring `JdbcTemplate` using the Repository pattern.

---

## 3. Class Design and Relationships

The system codebase is divided into clear functional packages adhering to object-oriented software engineering principles.

```text
app.scheduler/
├── config/             # Spring MVC & Auth Interceptor Configuration
├── controllers/        # REST API Endpoints & Request Handlers
│   └── admin/          # Administrative Entity Controllers
├── exceptions/         # Application Exceptions & Global Handler
├── generator/          # Algorithmic Schedule Generation Engine
├── models/             # Domain Entities & Data Transfer Objects
│   └── dtos/           # Request / Response DTO Payload Models
├── repositories/       # Data Access Interfaces & JDBC Implementations
│   └── impl/           # SQLite JDBC Implementations
├── services/           # Core Business Logic & Domain Services
└── utils/              # Token Management, Hashing & SQL Definitions
```

### 3.1 Domain Model Layer (`app.scheduler.models`)

All domain models enforce strict encapsulation using private fields, explicit constructors, getters, and setters.

* **`Event`**: Represents a scheduled class session. Encapsulates `id`, `type` (`LECTURE` / `LAB`), `topic`, `sectionId`, `courseId`, `teacherId`, `roomId`, `semesterId`, `batchId`, `labGroup`, `day` (1-6), `period` (1-5), `week`, `date`, `version`, and `status`.
* **`User`**: Encapsulates user authentication details (`id`, `username`, `passwordHash`, `salt`, `role` (`ADMIN`/`TEACHER`/`STUDENT`), `teacherId`, `studentId`, `active`).
* **`Student`**: Represents a registered student (`id`, `name`, `studentId`, `email`, `sectionId`, `batchId`, `labGroup`).
* **`Teacher`**: Represents an instructor (`id`, `name`, `email`, `department`, `type` (`LECTURE`/`LAB`)).
* **`Course`**: Represents an academic course (`id`, `name`, `code`, `department`, `credits`, `hasLab`).
* **`Room`**: Encapsulates a campus physical space (`id`, `name`, `type` (`LECTURE`/`LAB`/`LIBRARY`), `capacity`, `level`, `hasEquipment`).
* **`Batch`**: Represents an academic cohort (`id`, `name`, `program`, `sectionCount`, `year`).
* **`Section`**: Represents a subset of a batch (`id`, `name`, `batchId`, `studentCount`).
* **`Semester`**: Represents an academic term (`id`, `name`, `startDate`, `weeks`, `academicYear`, `active`, `generated`).
* **`BatchCourseMapping`**: Maps courses to batches, assigned lecture teachers, lab instructors, and semesters.
* **`Session`**: Tracks active JWT authentication tokens and expiration timestamps.
* **Data Transfer Objects (`models.dtos`)**: `AuthRequest`, `AuthResponse`, `AuthMeDto`, `EventDto`, `SlotDto`, `RoomOccupationDto`.

### 3.2 Data Access Layer (`app.scheduler.repositories`)

The persistence layer applies the **Repository Pattern** to separate SQL data access from business logic.

* **`Repository<T>` (Generic Interface)**: Base repository abstraction defining standard operations (`save`, `findById`, `findAll`, `update`, `delete`).
* **Domain Interfaces**: `EventRepository`, `UserRepository`, `StudentRepository`, `TeacherRepository`, `CourseRepository`, `RoomRepository`, `BatchRepository`, `SemesterRepository`, `SessionRepository`, `BatchCourseMappingRepository`.
* **JDBC Implementations (`repositories.impl`)**: `JdbcEventRepository`, `JdbcUserRepository`, `JdbcStudentRepository`, `JdbcTeacherRepository`, `JdbcCourseRepository`, `JdbcRoomRepository`, `JdbcBatchRepository`, `JdbcSemesterRepository`, `JdbcSessionRepository`, `JdbcBatchCourseMappingRepository`. Each uses Spring `JdbcTemplate` with custom `RowMapper` implementations.
* **`SQLQueries`**: Centralized utility class containing parameterized SQL statements preventing SQL injection attacks.
* **SQLite Database Engine**: Uses `database.db` via `org.sqlite.JDBC` with `SQLiteDialect`. Schema auto-generation is disabled in favor of deterministic custom initialization using `classpath:db/schema.sql`.

### 3.3 Business Logic Layer (`app.scheduler.services`)

Business services coordinate data access, execute domain rules, and handle application logic.

* **`ScheduleService`**: Core service for retrieving timetables, checking slot availability, booking events, and handling class rescheduling with validation.
* **`AuthService`**: Manages user authentication, password verification via salt hashing, JWT token creation, auto-registration of students/teachers, and session validation.
* **`SetupService`**: Implements `CommandLineRunner` to seed initial semesters, batches, sections, rooms, teachers, courses, mappings, and student records upon startup.
* **`BatchService`**: Manages batch creation and automatically generates sections (`Section A`, `Section B`) based on batch section counts.
* **`SemesterService`**, **`CourseService`**, **`RoomService`**, **`TeacherService`**, **`StudentService`**, **`BatchCourseMappingService`**: Service wrappers handling CRUD operations and business rules for domain entities.
* **`LoggerService`**: Centralized system logger for auditing admin actions, authentication events, and schedule modifications.

### 3.4 Web & Security Layer (`app.scheduler.controllers` & `config`)

* **`ScheduleController`**: Public and authenticated endpoints for fetching section/teacher schedules, searching available slots, viewing live room maps, booking, cancelling, and rescheduling classes.
* **`AuthController`**: Handles login (`/api/auth/login`) and current session user profile (`/api/auth/me`).
* **Admin Controllers (`controllers.admin`)**: `AdminScheduleController`, `BatchController`, `BatchCourseMappingController`, `CourseController`, `RoomController`, `SemesterController`, `StudentController`, `TeacherController`.
* **`AuthInterceptor`**: Implements `HandlerInterceptor` to validate JWT Bearer tokens on protected `/api/**` endpoints and populate the authenticated `User` object into `HttpServletRequest`.
* **`WebMvcConfig`**: Implements `WebMvcConfigurer` to register `AuthInterceptor` and configure CORS headers for frontend integration.
* **`GlobalExceptionHandler`**: Annotations-based `@ControllerAdvice` for handling uncaught exceptions and returning structured JSON error responses.

---

## 4. Features Implemented

### 4.1 Algorithmic Schedule Generation Engine

Automated schedule generation is implemented in `CspScheduleGenerator.java` (which implements the `ScheduleGenerator` interface).

#### 4.1.1 Problem Formulation & Class Instances
The generator converts `BatchCourseMapping` entries into discrete `ClassInstance` objects:
- **Theory Sessions**: Each course mapping produces theory session instances for each section in the batch.
- **Lab Groups**: Courses requiring labs (`hasLab = true`) split section student counts into 2 distinct lab groups (`Group 1` and `Group 2`) to fit specialized laboratory room capacities.

#### 4.1.2 Algorithmic Solvers & Heuristics
The generator uses a multi-stage **Constraint Satisfaction Problem (CSP) Solver**:
1. **Minimum Remaining Values (MRV) Heuristic**: Selects the most constrained `ClassInstance` first (the class with the smallest number of valid candidate slots remaining).
2. **Forward Checking & Pruning**: After placing a class in a slot, the solver evaluates remaining unassigned classes. If any class has 0 remaining valid slots, the branch is immediately pruned.
3. **Backtracking Search**: If a branch reaches a dead end, the solver backtracks and attempts alternative room/time assignments up to a search node safety limit (`MAX_SEARCH_NODES = 500,000`).
4. **Progressive Relaxation**: The algorithm attempts placement across 3 relaxation levels:
   - *Level 0 (Strict)*: Daily section load $\le 3$, course spreading enforced (no duplicate theory course on the same day).
   - *Level 1 (Relaxed Load)*: Daily section load $\le 5$, course spreading enforced.
   - *Level 2 (Fully Relaxed)*: Daily section load $\le 5$, course spreading relaxed.
5. **Randomized Greedy Fallback**: Executes randomized greedy search passes to find optimal initial placements.

#### 4.1.3 Hard Constraint Validation
- **Room Type Match**: `LAB` sessions require `LAB` rooms; `THEORY` sessions require `LECTURE` rooms.
- **Room Capacity**: Room capacity must be $\ge$ class instance student size.
- **No Teacher Overlaps**: An instructor cannot be assigned to two classes at the same day and period.
- **No Section / Lab Group Overlaps**: A section or lab group cannot have multiple classes scheduled in the same time slot.
- **No Room Overlaps**: A physical room cannot host multiple classes in the same period.

#### 4.1.4 Date Calculation & Multi-Week Expansion
Once a valid weekly template is generated (Days 0-5, Periods 0-4), the generator expands the template across all weeks of the semester (default 16 weeks), computing exact `date` values starting from the semester start date aligned to Monday.

### 4.2 Full CRUD Management
Administrative endpoints and frontend modals allow full management for:
- Semesters (active semester selection, term dates, weeks)
- Batches & Sections (section student count configuration)
- Courses (credits, department, lab requirement flag)
- Rooms (building level, capacity, room type, equipment)
- Teachers & Students (department, assigned sections, lab group assignments)

### 4.3 Interactive Timetable Grid
The frontend `TimetableGrid` component renders a 6-day (Monday–Saturday), 5-period weekly timetable matrix:
- Displays course code, course name, instructor name, room number, and lab group badges.
- Supports role-specific views (Student section schedule, Teacher assigned classes, Admin full overview).
- Provides instant period selection for class cancellation, restoration, or rescheduling.

### 4.4 Live Building Room Occupation Mapping
- **Backend**: `ScheduleService.getLiveRoomOccupation()` determines the exact status of every room (`FREE` vs `OCCUPIED`) for any given date and time.
- **Frontend**: `SidebarMap` displays interactive campus building levels and color-coded room cards (Green = Free, Red = Occupied) with live occupancy details.

### 4.5 Dynamic Slot Search & Rescheduling
When a teacher or admin opens the reschedule modal:
1. The frontend queries `/api/schedule/slots/available`.
2. The backend checks future periods where the section and teacher are free and an appropriate room type (`LECTURE` or `LAB`) is available.
3. The user selects from descriptive date options, updating the event in real-time.

### 4.6 Authentication & Security
- Password storage utilizes salt generation and BCrypt hashing (`PasswordHasher`).
- Stateless authentication via JWT tokens generated by `TokenManager` and validated by `AuthInterceptor`.
- Role-based UI rendering for `ADMIN`, `TEACHER`, and `STUDENT` accounts.

---

## 5. Contribution Breakdown

The project responsibilities were distributed across 4 team members:

| Member | Primary Role | Key Responsibilities |
| :--- | :--- | :--- |
| **Member 1** | Core Models & Data Persistence | Designed OOP domain classes (Event, Course, Student, Room) and implemented the SQLite JDBC database layer. |
| **Member 2** | Schedule Generation Engine | Developed the CSP solver algorithm to automatically generate valid, conflict-free multi-week timetables. |
| **Member 3** | REST API & Security Logic | Built Spring Boot business services, REST controllers, JWT authentication interceptors, and error handling. |
| **Member 4** | Next.js Frontend Application | Developed the interactive web dashboard, Timetable Grid, Live Building Map, and integrated the backend API. |

---

## 6. Conclusion

The **University Timetable & Resource Scheduler System** successfully fulfills the requirements of the Java Object-Oriented Programming final project. 

By combining object-oriented design principles (encapsulation, abstraction, inheritance, polymorphism, and modular layered structure) with an automated CSP solver, SQLite JDBC persistence, and a responsive Next.js frontend, the project demonstrates a complete software solution for university schedule management and resource optimization.
