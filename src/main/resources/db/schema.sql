-- Tables (all independent with ID references only)

CREATE TABLE IF NOT EXISTS semesters (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    start_date DATE NOT NULL,
    weeks INT DEFAULT 16,
    academic_year VARCHAR(20),
    is_generated BOOLEAN DEFAULT 0,
    is_active BOOLEAN DEFAULT 1
);

CREATE TABLE IF NOT EXISTS batches (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    program VARCHAR(100),
    year VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS sections (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    batch_id VARCHAR(36),
    lab_group INT DEFAULT 0,
    student_count INT DEFAULT 0,
    FOREIGN KEY (batch_id) REFERENCES batches(id)
);

CREATE TABLE IF NOT EXISTS courses (
    id VARCHAR(36) PRIMARY KEY,
    code VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(100) UNIQUE NOT NULL,
    has_lab BOOLEAN NOT NULL DEFAULT 0,
    credits INTEGER NOT NULL,
    department VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS teachers (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    department VARCHAR(100),
    type VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS rooms (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    type VARCHAR(20) NOT NULL,
    capacity INT DEFAULT 30,
    level INT DEFAULT 0,
    has_equipment BOOLEAN DEFAULT 0
);

CREATE TABLE IF NOT EXISTS students (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    student_id VARCHAR(20) UNIQUE NOT NULL,
    email VARCHAR(100),
    section_id VARCHAR(36),
    batch_id VARCHAR(36),
    lab_group INT DEFAULT 0,
    FOREIGN KEY (section_id) REFERENCES sections(id)
);

CREATE TABLE IF NOT EXISTS users (
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
    FOREIGN KEY (teacher_id) REFERENCES teachers(id),
    FOREIGN KEY (student_id) REFERENCES students(id)
);

CREATE TABLE IF NOT EXISTS sessions (
    id VARCHAR(36) PRIMARY KEY,
    token VARCHAR(255) UNIQUE NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    ip_address VARCHAR(45),
    user_agent VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Mapping Table (Batch → Courses → Teachers)
CREATE TABLE IF NOT EXISTS batch_course_mappings (
    id VARCHAR(36) PRIMARY KEY,
    batch_id VARCHAR(36) NOT NULL,
    course_id VARCHAR(36) NOT NULL,
    lecture_teacher_id VARCHAR(36) NOT NULL,
    lab_instructor_id VARCHAR(36),
    semester_id VARCHAR(36) NOT NULL,
    FOREIGN KEY (batch_id) REFERENCES batches(id),
    FOREIGN KEY (course_id) REFERENCES courses(id),
    FOREIGN KEY (lecture_teacher_id) REFERENCES teachers(id),
    FOREIGN KEY (lab_instructor_id) REFERENCES teachers(id),
    FOREIGN KEY (semester_id) REFERENCES semesters(id),
    UNIQUE(batch_id, course_id, semester_id)
);

-- Events Table (The Output)
CREATE TABLE IF NOT EXISTS events (
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
    date VARCHAR(20),
    version INT DEFAULT 1,
    status VARCHAR(20) DEFAULT 'SCHEDULED',
    FOREIGN KEY (section_id) REFERENCES sections(id),
    FOREIGN KEY (course_id) REFERENCES courses(id),
    FOREIGN KEY (teacher_id) REFERENCES teachers(id),
    FOREIGN KEY (room_id) REFERENCES rooms(id),
    FOREIGN KEY (semester_id) REFERENCES semesters(id),
    FOREIGN KEY (batch_id) REFERENCES batches(id)
);

-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_event_section ON events(section_id);
CREATE INDEX IF NOT EXISTS idx_event_teacher ON events(teacher_id);
CREATE INDEX IF NOT EXISTS idx_event_room ON events(room_id);
CREATE INDEX IF NOT EXISTS idx_event_semester ON events(semester_id);
CREATE INDEX IF NOT EXISTS idx_event_day ON events(day, week);

-- Auto create admin user
INSERT OR IGNORE INTO users (id, user_id, username, email, password_hash, salt, role) VALUES ('admin-1', 'admin_user', 'admin_user', 'admin@example.com', '6sIe5Scx6eRbLqi2gsPXfLfTkUIB8aYWYLCRjvJzzHw=', '1Yxv7sJO24RNqOdI9ubmng==', 'ADMIN');
