package app.scheduler.utils;

public class SQLQueries {
    // BATCH
    public static final String BATCH_FIND_BY_ID = "SELECT * FROM batches WHERE id = ?";
    public static final String BATCH_FIND_ALL = "SELECT * FROM batches";
    public static final String BATCH_DELETE = "DELETE FROM batches WHERE id = ?";
    public static final String BATCH_FIND_BY_SEMESTER_ID = "SELECT DISTINCT b.* FROM batches b JOIN batch_course_mappings m ON b.id = m.batch_id WHERE m.semester_id = ?";
    public static final String BATCH_FIND_BY_PROGRAM = "SELECT * FROM batches WHERE program = ?";
    public static final String BATCH_INSERT = "INSERT INTO batches (id, name, program, year) VALUES (?, ?, ?, ?)";
    public static final String BATCH_UPDATE = "UPDATE batches SET name = ?, program = ?, year = ? WHERE id = ?";

    // BATCH COURSE MAPPING
    public static final String BATCHCOURSEMAPPING_FIND_BY_ID = "SELECT * FROM batch_course_mappings WHERE id = ?";
    public static final String BATCHCOURSEMAPPING_FIND_ALL = "SELECT * FROM batch_course_mappings";
    public static final String BATCHCOURSEMAPPING_DELETE = "DELETE FROM batch_course_mappings WHERE id = ?";
    public static final String BATCHCOURSEMAPPING_FIND_BY_BATCH_ID = "SELECT * FROM batch_course_mappings WHERE batch_id = ?";
    public static final String BATCHCOURSEMAPPING_FIND_BY_SEMESTER_ID = "SELECT * FROM batch_course_mappings WHERE semester_id = ?";
    public static final String BATCHCOURSEMAPPING_FIND_BY_BATCH_ID_AND_COURSE_ID_AND_SEMESTER_ID = "SELECT * FROM batch_course_mappings WHERE batch_id = ? AND course_id = ? AND semester_id = ?";
    public static final String BATCHCOURSEMAPPING_INSERT = "INSERT INTO batch_course_mappings (id, batch_id, course_id, lecture_teacher_id, lab_instructor_id, semester_id) VALUES (?, ?, ?, ?, ?, ?)";
    public static final String BATCHCOURSEMAPPING_UPDATE = "UPDATE batch_course_mappings SET batch_id = ?, course_id = ?, lecture_teacher_id = ?, lab_instructor_id = ?, semester_id = ? WHERE id = ?";

    // COURSE
    public static final String COURSE_FIND_BY_ID = "SELECT * FROM courses WHERE id = ?";
    public static final String COURSE_FIND_ALL = "SELECT * FROM courses";
    public static final String COURSE_DELETE = "DELETE FROM courses WHERE id = ?";
    public static final String COURSE_INSERT = "INSERT INTO courses (id, code, name, has_lab, credits, department) VALUES (?, ?, ?, ?, ?, ?)";
    public static final String COURSE_UPDATE = "UPDATE courses SET code = ?, name = ?, has_lab = ?, credits = ?, department = ? WHERE id = ?";

    // EVENT
    public static final String EVENT_FIND_BY_ID = "SELECT * FROM events WHERE id = ?";
    public static final String EVENT_FIND_ALL = "SELECT * FROM events";
    public static final String EVENT_DELETE = "DELETE FROM events WHERE id = ?";
    public static final String EVENT_FIND_BY_SECTION_ID = "SELECT * FROM events WHERE section_id = ?";
    public static final String EVENT_FIND_BY_TEACHER_ID = "SELECT * FROM events WHERE teacher_id = ?";
    public static final String EVENT_FIND_BY_ROOM_ID = "SELECT * FROM events WHERE room_id = ?";
    public static final String EVENT_FIND_BY_SEMESTER_ID = "SELECT * FROM events WHERE semester_id = ?";
    public static final String EVENT_FIND_BY_SECTION_ID_AND_WEEK = "SELECT * FROM events WHERE section_id = ? AND week = ?";
    public static final String EVENT_FIND_BY_TEACHER_ID_AND_DAY = "SELECT * FROM events WHERE teacher_id = ? AND day = ?";
    public static final String EVENT_INSERT = "INSERT INTO events (id, type, topic, section_id, course_id, teacher_id, room_id, semester_id, batch_id, lab_group, day, period, week, date, version, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    public static final String EVENT_UPDATE = "UPDATE events SET type = ?, topic = ?, section_id = ?, course_id = ?, teacher_id = ?, room_id = ?, semester_id = ?, batch_id = ?, lab_group = ?, day = ?, period = ?, week = ?, date = ?, version = ?, status = ? WHERE id = ?";

    // ROOM
    public static final String ROOM_FIND_BY_ID = "SELECT * FROM rooms WHERE id = ?";
    public static final String ROOM_FIND_ALL = "SELECT * FROM rooms";
    public static final String ROOM_DELETE = "DELETE FROM rooms WHERE id = ?";
    public static final String ROOM_INSERT = "INSERT INTO rooms (id, name, type, capacity, level, has_equipment) VALUES (?, ?, ?, ?, ?, ?)";
    public static final String ROOM_UPDATE = "UPDATE rooms SET name = ?, type = ?, capacity = ?, level = ?, has_equipment = ? WHERE id = ?";

    // SECTION
    public static final String SECTION_FIND_BY_ID = "SELECT * FROM sections WHERE id = ?";
    public static final String SECTION_FIND_ALL = "SELECT * FROM sections";
    public static final String SECTION_DELETE = "DELETE FROM sections WHERE id = ?";
    public static final String SECTION_FIND_BY_BATCH_ID = "SELECT * FROM sections WHERE batch_id = ?";
    public static final String SECTION_FIND_BY_SEMESTER_ID = "SELECT DISTINCT s.* FROM sections s JOIN batches b ON s.batch_id = b.id JOIN batch_course_mappings m ON b.id = m.batch_id WHERE m.semester_id = ?";
    public static final String SECTION_INSERT = "INSERT INTO sections (id, name, batch_id, student_count) VALUES (?, ?, ?, ?)";
    public static final String SECTION_UPDATE = "UPDATE sections SET name = ?, batch_id = ?, student_count = ? WHERE id = ?";

    // SEMESTER
    public static final String SEMESTER_FIND_BY_ID = "SELECT * FROM semesters WHERE id = ?";
    public static final String SEMESTER_FIND_ALL = "SELECT * FROM semesters";
    public static final String SEMESTER_DELETE = "DELETE FROM semesters WHERE id = ?";
    public static final String SEMESTER_INSERT = "INSERT INTO semesters (id, name, start_date, weeks, academic_year, is_generated, is_active) VALUES (?, ?, ?, ?, ?, ?, ?)";
    public static final String SEMESTER_UPDATE = "UPDATE semesters SET name = ?, start_date = ?, weeks = ?, academic_year = ?, is_generated = ?, is_active = ? WHERE id = ?";
    public static final String SEMESTER_SET_ALL_INACTIVE = "UPDATE semesters SET is_active = 0";
    public static final String SEMESTER_SET_ACTIVE = "UPDATE semesters SET is_active = 1 WHERE id = ?";

    // SESSION
    public static final String SESSION_FIND_BY_ID = "SELECT * FROM sessions WHERE id = ?";
    public static final String SESSION_FIND_ALL = "SELECT * FROM sessions";
    public static final String SESSION_DELETE = "DELETE FROM sessions WHERE id = ?";
    public static final String SESSION_FIND_BY_TOKEN = "SELECT * FROM sessions WHERE token = ?";
    public static final String SESSION_INSERT = "INSERT INTO sessions (id, token, user_id, ip_address, user_agent, expires_at) VALUES (?, ?, ?, ?, ?, ?)";
    public static final String SESSION_UPDATE = "UPDATE sessions SET token = ?, user_id = ?, ip_address = ?, user_agent = ?, expires_at = ? WHERE id = ?";

    // STUDENT
    public static final String STUDENT_FIND_BY_ID = "SELECT * FROM students WHERE id = ?";
    public static final String STUDENT_FIND_ALL = "SELECT * FROM students";
    public static final String STUDENT_DELETE = "DELETE FROM students WHERE id = ?";
    public static final String STUDENT_INSERT = "INSERT INTO students (id, name, student_id, email, section_id, batch_id, lab_group) VALUES (?, ?, ?, ?, ?, ?, ?)";
    public static final String STUDENT_UPDATE = "UPDATE students SET name = ?, student_id = ?, email = ?, section_id = ?, batch_id = ?, lab_group = ? WHERE id = ?";

    // TEACHER
    public static final String TEACHER_FIND_BY_ID = "SELECT * FROM teachers WHERE id = ?";
    public static final String TEACHER_FIND_ALL = "SELECT * FROM teachers";
    public static final String TEACHER_DELETE = "DELETE FROM teachers WHERE id = ?";
    public static final String TEACHER_INSERT = "INSERT INTO teachers (id, name, email, department, type) VALUES (?, ?, ?, ?, ?)";
    public static final String TEACHER_UPDATE = "UPDATE teachers SET name = ?, email = ?, department = ?, type = ? WHERE id = ?";

    // USER
    public static final String USER_FIND_BY_ID = "SELECT * FROM users WHERE id = ?";
    public static final String USER_FIND_ALL = "SELECT * FROM users";
    public static final String USER_DELETE = "DELETE FROM users WHERE id = ?";
    public static final String USER_FIND_BY_USERNAME = "SELECT * FROM users WHERE username = ?";
    public static final String USER_INSERT = "INSERT INTO users (id, user_id, username, email, password_hash, salt, role, teacher_id, student_id, is_active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    public static final String USER_UPDATE = "UPDATE users SET user_id = ?, username = ?, email = ?, password_hash = ?, salt = ?, role = ?, teacher_id = ?, student_id = ?, is_active = ? WHERE id = ?";
}
