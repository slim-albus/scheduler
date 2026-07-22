-- Seed data from HiLCoE SeedData

-- Semesters
INSERT OR IGNORE INTO semesters (id, name, start_date, end_date, weeks, academic_year, is_generated, is_active)
VALUES ('sem-1', '2026/27 First Semester', '2026-07-21', '2026-11-21', 16, '2026/27', 0, 1);

-- Batches
INSERT OR IGNORE INTO batches (id, name, program, year, is_active)
VALUES ('batch-drbse', 'DRBSE2502', '2026/27 First Semester', '2026/27', 1);

-- Sections
INSERT OR IGNORE INTO sections (id, name, batch_id, student_count, is_active)
VALUES ('sec-drbse-a', 'Section A', 'batch-drbse', 35, 1);
INSERT OR IGNORE INTO sections (id, name, batch_id, student_count, is_active)
VALUES ('sec-drbse-b', 'Section B', 'batch-drbse', 35, 1);

-- Rooms
-- Lecture Rooms (Equipment: TV_BOARD = has_equipment=1, BOARD_ONLY = has_equipment=0)
INSERT OR IGNORE INTO rooms (id, name, type, capacity, building, level, has_equipment, availability_bitmask, is_active)
VALUES ('room-201', '201', 'LECTURE_ROOM', 45, 'Building A', 2, 1, -1, 1);
INSERT OR IGNORE INTO rooms (id, name, type, capacity, building, level, has_equipment, availability_bitmask, is_active)
VALUES ('room-202', '202', 'LECTURE_ROOM', 45, 'Building A', 2, 0, -1, 1);
INSERT OR IGNORE INTO rooms (id, name, type, capacity, building, level, has_equipment, availability_bitmask, is_active)
VALUES ('room-203', '203', 'LECTURE_ROOM', 45, 'Building A', 2, 1, -1, 1);
INSERT OR IGNORE INTO rooms (id, name, type, capacity, building, level, has_equipment, availability_bitmask, is_active)
VALUES ('room-301', '301', 'LECTURE_ROOM', 45, 'Building A', 3, 1, -1, 1);
INSERT OR IGNORE INTO rooms (id, name, type, capacity, building, level, has_equipment, availability_bitmask, is_active)
VALUES ('room-302', '302', 'LECTURE_ROOM', 45, 'Building A', 3, 0, -1, 1);
INSERT OR IGNORE INTO rooms (id, name, type, capacity, building, level, has_equipment, availability_bitmask, is_active)
VALUES ('room-303', '303', 'LECTURE_ROOM', 45, 'Building A', 3, 1, -1, 1);
INSERT OR IGNORE INTO rooms (id, name, type, capacity, building, level, has_equipment, availability_bitmask, is_active)
VALUES ('room-401', '401', 'LECTURE_ROOM', 45, 'Building A', 4, 1, -1, 1);
INSERT OR IGNORE INTO rooms (id, name, type, capacity, building, level, has_equipment, availability_bitmask, is_active)
VALUES ('room-402', '402', 'LECTURE_ROOM', 45, 'Building A', 4, 0, -1, 1);
INSERT OR IGNORE INTO rooms (id, name, type, capacity, building, level, has_equipment, availability_bitmask, is_active)
VALUES ('room-601', '601', 'LECTURE_ROOM', 80, 'Building A', 6, 1, -1, 1);

-- Computer Labs (has_equipment=1)
INSERT OR IGNORE INTO rooms (id, name, type, capacity, building, level, has_equipment, availability_bitmask, is_active)
VALUES ('room-lab-204', 'Lab 204', 'COMPUTER_LAB', 24, 'Building B', 2, 1, -1, 1);
INSERT OR IGNORE INTO rooms (id, name, type, capacity, building, level, has_equipment, availability_bitmask, is_active)
VALUES ('room-lab-304', 'Lab 304', 'COMPUTER_LAB', 24, 'Building B', 3, 1, -1, 1);
INSERT OR IGNORE INTO rooms (id, name, type, capacity, building, level, has_equipment, availability_bitmask, is_active)
VALUES ('room-lab-404', 'Lab 404', 'COMPUTER_LAB', 24, 'Building B', 4, 1, -1, 1);
INSERT OR IGNORE INTO rooms (id, name, type, capacity, building, level, has_equipment, availability_bitmask, is_active)
VALUES ('room-lab-504', 'Lab 504', 'COMPUTER_LAB', 24, 'Building B', 5, 1, -1, 1);

-- Courses
INSERT OR IGNORE INTO courses (id, code, name, has_lab, credits, lecture_hours_per_week, lab_hours_per_week, midterm_duration, final_duration, department, is_active)
VALUES ('course-se2222', 'SE2222', 'Web', 1, 3, 3, 2, 60, 120, 'Software Engineering', 1);
INSERT OR IGNORE INTO courses (id, code, name, has_lab, credits, lecture_hours_per_week, lab_hours_per_week, midterm_duration, final_duration, department, is_active)
VALUES ('course-se1221', 'SE1221', 'OOP', 1, 3, 3, 2, 60, 120, 'Software Engineering', 1);
INSERT OR IGNORE INTO courses (id, code, name, has_lab, credits, lecture_hours_per_week, lab_hours_per_week, midterm_duration, final_duration, department, is_active)
VALUES ('course-cc2131', 'CC2131', 'Math', 0, 3, 3, 0, 60, 120, 'Common Courses', 1);
INSERT OR IGNORE INTO courses (id, code, name, has_lab, credits, lecture_hours_per_week, lab_hours_per_week, midterm_duration, final_duration, department, is_active)
VALUES ('course-cc0193', 'CC0193', 'Econ', 0, 3, 3, 0, 60, 120, 'Common Courses', 1);
INSERT OR IGNORE INTO courses (id, code, name, has_lab, credits, lecture_hours_per_week, lab_hours_per_week, midterm_duration, final_duration, department, is_active)
VALUES ('course-cc0197', 'CC0197', 'Global', 0, 3, 3, 0, 60, 120, 'Common Courses', 1);

-- Teachers Availability Bitmasks
-- Kibrom (lecturer): Tue morning, Thu morning, Fri morning.
-- Total = 224 + 229376 + 7340032 = 7569632
INSERT OR IGNORE INTO teachers (id, name, email, department, type, availability_bitmask, is_active)
VALUES ('t-kibrom', 'Kibrom', 'kibrom@acse.local', 'Software Engineering', 'LECTURER', 7569632, 1);

-- Abelti (lab_instructor): Tue morning, Thu morning, Sat morning.
-- Total = 224 + 229376 + 234881024 = 235110624
INSERT OR IGNORE INTO teachers (id, name, email, department, type, availability_bitmask, is_active)
VALUES ('t-abelti', 'Abelti', 'abelti@acse.local', 'Software Engineering', 'LAB_INSTRUCTOR', 235110624, 1);

-- Nesredin (lecturer): Tue morning, Sat morning.
-- Total = 234881248
INSERT OR IGNORE INTO teachers (id, name, email, department, type, availability_bitmask, is_active)
VALUES ('t-nesredin', 'Nesredin', 'nesredin@acse.local', 'Software Engineering', 'LECTURER', 234881248, 1);

-- Betsi (lab_instructor): Tue mixed (M1-M3, A1-A2), Thu morning (M1-M3)
-- Total = 992 + 229376 = 230368
INSERT OR IGNORE INTO teachers (id, name, email, department, type, availability_bitmask, is_active)
VALUES ('t-betsi', 'Betsi', 'betsi@acse.local', 'Software Engineering', 'LAB_INSTRUCTOR', 230368, 1);

-- Gech (lecturer): Fri morning, Sat morning.
-- Total = 242221056
INSERT OR IGNORE INTO teachers (id, name, email, department, type, availability_bitmask, is_active)
VALUES ('t-gech', 'Gech', 'gech@acse.local', 'Common Courses', 'LECTURER', 242221056, 1);

-- Tewlde (lecturer): Mon morning, Sat morning, Thu afternoon, Fri afternoon
-- Total = 7 + 234881024 + 786432 + 25165824 = 260833287
INSERT OR IGNORE INTO teachers (id, name, email, department, type, availability_bitmask, is_active)
VALUES ('t-tewlde', 'Tewlde', 'tewlde@acse.local', 'Common Courses', 'LECTURER', 260833287, 1);

-- Yirga (lecturer): Mon, Tue, Thu, Fri afternoon
-- Total = 24 + 768 + 786432 + 25165824 = 25953048
INSERT OR IGNORE INTO teachers (id, name, email, department, type, availability_bitmask, is_active)
VALUES ('t-yirga', 'Yirga', 'yirga@acse.local', 'Common Courses', 'LECTURER', 25953048, 1);

-- Batch Course Mappings
INSERT OR IGNORE INTO batch_course_mappings (id, batch_id, course_id, lecture_teacher_id, lab_instructor_id, is_required, semester_id)
VALUES ('map-web', 'batch-drbse', 'course-se2222', 't-kibrom', 't-abelti', 1, 'sem-1');
INSERT OR IGNORE INTO batch_course_mappings (id, batch_id, course_id, lecture_teacher_id, lab_instructor_id, is_required, semester_id)
VALUES ('map-oop', 'batch-drbse', 'course-se1221', 't-nesredin', 't-betsi', 1, 'sem-1');
INSERT OR IGNORE INTO batch_course_mappings (id, batch_id, course_id, lecture_teacher_id, lab_instructor_id, is_required, semester_id)
VALUES ('map-math', 'batch-drbse', 'course-cc2131', 't-gech', NULL, 1, 'sem-1');
INSERT OR IGNORE INTO batch_course_mappings (id, batch_id, course_id, lecture_teacher_id, lab_instructor_id, is_required, semester_id)
VALUES ('map-econ', 'batch-drbse', 'course-cc0193', 't-tewlde', NULL, 1, 'sem-1');
INSERT OR IGNORE INTO batch_course_mappings (id, batch_id, course_id, lecture_teacher_id, lab_instructor_id, is_required, semester_id)
VALUES ('map-global', 'batch-drbse', 'course-cc0197', 't-yirga', NULL, 1, 'sem-1');

-- Insert Users for Teachers
INSERT OR IGNORE INTO users (id, user_id, username, email, password_hash, salt, role, teacher_id) VALUES ('u-kibrom', 'kibrom', 'kibrom', 'kibrom@acse.local', '6sIe5Scx6eRbLqi2gsPXfLfTkUIB8aYWYLCRjvJzzHw=', '1Yxv7sJO24RNqOdI9ubmng==', 'TEACHER', 't-kibrom');
INSERT OR IGNORE INTO users (id, user_id, username, email, password_hash, salt, role, teacher_id) VALUES ('u-abelti', 'abelti', 'abelti', 'abelti@acse.local', '6sIe5Scx6eRbLqi2gsPXfLfTkUIB8aYWYLCRjvJzzHw=', '1Yxv7sJO24RNqOdI9ubmng==', 'TEACHER', 't-abelti');
INSERT OR IGNORE INTO users (id, user_id, username, email, password_hash, salt, role, teacher_id) VALUES ('u-nesredin', 'nesredin', 'nesredin', 'nesredin@acse.local', '6sIe5Scx6eRbLqi2gsPXfLfTkUIB8aYWYLCRjvJzzHw=', '1Yxv7sJO24RNqOdI9ubmng==', 'TEACHER', 't-nesredin');
INSERT OR IGNORE INTO users (id, user_id, username, email, password_hash, salt, role, teacher_id) VALUES ('u-betsi', 'betsi', 'betsi', 'betsi@acse.local', '6sIe5Scx6eRbLqi2gsPXfLfTkUIB8aYWYLCRjvJzzHw=', '1Yxv7sJO24RNqOdI9ubmng==', 'TEACHER', 't-betsi');
INSERT OR IGNORE INTO users (id, user_id, username, email, password_hash, salt, role, teacher_id) VALUES ('u-gech', 'gech', 'gech', 'gech@acse.local', '6sIe5Scx6eRbLqi2gsPXfLfTkUIB8aYWYLCRjvJzzHw=', '1Yxv7sJO24RNqOdI9ubmng==', 'TEACHER', 't-gech');
INSERT OR IGNORE INTO users (id, user_id, username, email, password_hash, salt, role, teacher_id) VALUES ('u-tewlde', 'tewlde', 'tewlde', 'tewlde@acse.local', '6sIe5Scx6eRbLqi2gsPXfLfTkUIB8aYWYLCRjvJzzHw=', '1Yxv7sJO24RNqOdI9ubmng==', 'TEACHER', 't-tewlde');
INSERT OR IGNORE INTO users (id, user_id, username, email, password_hash, salt, role, teacher_id) VALUES ('u-yirga', 'yirga', 'yirga', 'yirga@acse.local', '6sIe5Scx6eRbLqi2gsPXfLfTkUIB8aYWYLCRjvJzzHw=', '1Yxv7sJO24RNqOdI9ubmng==', 'TEACHER', 't-yirga');
