interface UserRepository {
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    void save(User user);
    void deleteById(Long id);
}

interface SessionRepository {
    void save(Session session);
    Optional<Session> findByToken(String token);
    void deleteByToken(String token);
    void deleteExpired();
}

interface StudentRepository {
    Optional<Student> findById(Long id);
    Optional<Student> findByUserId(Long userId);
    List<Student> findAll();
    void save(Student student);
    void deleteById(Long id);
}

interface TeacherRepository {
    Optional<Teacher> findById(Long id);
    Optional<Teacher> findByUserId(Long userId);
    List<Teacher> findAll();
    void save(Teacher teacher);
    void deleteById(Long id);
}

interface CourseRepository {
    Optional<Course> findById(Long id);
    List<Course> findAll();
    void save(Course course);
    void deleteById(Long id);
}

interface SemesterRepository {
    Optional<Semester> findById(Long id);
    List<Semester> findAll();
    List<Semester> findByStatus(boolean isActive);
    void save(Semester semester);
    void deleteById(Long id);
}

interface SectionRepository {
    Optional<Section> findById(Long id);
    List<Section> findAll();
    List<Section> findBySemesterId(Long semesterId);
    void save(Section section);
    void deleteById(Long id);
}

interface RoomRepository {
    Optional<Room> findById(Long id);
    List<Room> findAll();
    void save(Room room);
    void deleteById(Long id);
}

interface EventRepository {
    Optional<Event> findById(Long id);
    List<Event> findAll();
    List<Event> findBySectionId(Long sectionId);
    List<Event> findByTeacherId(Long teacherId);
    List<Event> findEventsAtTimestamp(LocalDateTime dateTime);
    void save(Event event);
    void deleteById(Long id);
}