-- Tables (all independent with ID references only)

CREATE TABLE IF NOT EXISTS semester (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(20) UNIQUE NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    weeks INT DEFAULT 16,
    academic_year VARCHAR(20),
    is_active BOOLEAN DEFAULT 1
);

CREATE TABLE IF NOT EXISTS batch (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    program VARCHAR(100),
    year VARCHAR(20),
    semester_id VARCHAR(36),
    is_active BOOLEAN DEFAULT 1,
    FOREIGN KEY (semester_id) REFERENCES semester(id)
);

CREATE TABLE IF NOT EXISTS section (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    batch_id VARCHAR(36),
    lab_group INT DEFAULT 0,
    student_count INT DEFAULT 0,
    semester_id VARCHAR(36),
    is_active BOOLEAN DEFAULT 1,
    FOREIGN KEY (batch_id) REFERENCES batch(id)
);

CREATE TABLE IF NOT EXISTS course (
    id VARCHAR(36) PRIMARY KEY,
    code VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    has_lab BOOLEAN DEFAULT 0,
    credits INT DEFAULT 3,
    lecture_hours_per_week INT DEFAULT 2,
    lab_hours_per_week INT DEFAULT 1,
    midterm_duration INT DEFAULT 60,
    final_duration INT DEFAULT 120,
    department VARCHAR(100),
    is_active BOOLEAN DEFAULT 1
);

CREATE TABLE IF NOT EXISTS teacher (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    department VARCHAR(100),
    type VARCHAR(20) NOT NULL,
    availability_bitmask BIGINT DEFAULT -1,
    max_classes_per_day INT DEFAULT 5,
    is_active BOOLEAN DEFAULT 1
);

CREATE TABLE IF NOT EXISTS room (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    type VARCHAR(20) NOT NULL,
    capacity INT DEFAULT 30,
    building VARCHAR(50),
    level INT DEFAULT 0,
    has_tv BOOLEAN DEFAULT 0,
    has_projector BOOLEAN DEFAULT 0,
    availability_bitmask BIGINT DEFAULT -1,
    is_active BOOLEAN DEFAULT 1
);

CREATE TABLE IF NOT EXISTS student (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    student_id VARCHAR(20) UNIQUE NOT NULL,
    email VARCHAR(100),
    section_id VARCHAR(36),
    batch_id VARCHAR(36),
    lab_group INT DEFAULT 0,
    program VARCHAR(100),
    year VARCHAR(20),
    semester_id VARCHAR(36),
    is_active BOOLEAN DEFAULT 1,
    FOREIGN KEY (section_id) REFERENCES section(id)
);

CREATE TABLE IF NOT EXISTS user (
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
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (teacher_id) REFERENCES teacher(id),
    FOREIGN KEY (student_id) REFERENCES student(id)
);

CREATE TABLE IF NOT EXISTS session (
    id VARCHAR(36) PRIMARY KEY,
    token VARCHAR(255) UNIQUE NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    ip_address VARCHAR(45),
    user_agent VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    is_active BOOLEAN DEFAULT 1,
    FOREIGN KEY (user_id) REFERENCES user(id)
);

-- Mapping Table (Batch → Courses → Teachers)
CREATE TABLE IF NOT EXISTS batch_course_mapping (
    id VARCHAR(36) PRIMARY KEY,
    batch_id VARCHAR(36) NOT NULL,
    course_id VARCHAR(36) NOT NULL,
    lecture_teacher_id VARCHAR(36) NOT NULL,
    lab_instructor_id VARCHAR(36),
    is_required BOOLEAN DEFAULT 1,
    semester_id VARCHAR(36) NOT NULL,
    FOREIGN KEY (batch_id) REFERENCES batch(id),
    FOREIGN KEY (course_id) REFERENCES course(id),
    FOREIGN KEY (lecture_teacher_id) REFERENCES teacher(id),
    FOREIGN KEY (lab_instructor_id) REFERENCES teacher(id),
    FOREIGN KEY (semester_id) REFERENCES semester(id),
    UNIQUE(batch_id, course_id)
);

-- Events Table (The Output)
CREATE TABLE IF NOT EXISTS event (
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
    start_date_time TIMESTAMP NOT NULL,
    end_date_time TIMESTAMP NOT NULL,
    duration_minutes INT DEFAULT 90,
    instance INT DEFAULT 0,
    created_by VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version INT DEFAULT 1,
    status VARCHAR(20) DEFAULT 'SCHEDULED',
    FOREIGN KEY (section_id) REFERENCES section(id),
    FOREIGN KEY (course_id) REFERENCES course(id),
    FOREIGN KEY (teacher_id) REFERENCES teacher(id),
    FOREIGN KEY (room_id) REFERENCES room(id),
    FOREIGN KEY (semester_id) REFERENCES semester(id),
    FOREIGN KEY (batch_id) REFERENCES batch(id)
);

-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_event_section ON event(section_id);
CREATE INDEX IF NOT EXISTS idx_event_teacher ON event(teacher_id);
CREATE INDEX IF NOT EXISTS idx_event_room ON event(room_id);
CREATE INDEX IF NOT EXISTS idx_event_semester ON event(semester_id);
CREATE INDEX IF NOT EXISTS idx_event_day ON event(day, week);
