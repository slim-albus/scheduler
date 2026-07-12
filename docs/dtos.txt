LoginRequest: String username, String password

AuthResponse: String token, String role, Long profileId

CalendarEventDto: eventId, title, dateTime, status, courseCode, courseName, sectionName, teacherName, roomName

RoomOccupancyDto: roomId, roomName, capacity, isOccupied, currentEventTitle, currentTeacherName, currentEventEnd

GenerateScheduleRequest: semesterId, List<CourseAssignment> assignments

CourseAssignment: courseId, sectionId, teacherId, hoursPerWeek

MoveEventRequest: LocalDateTime newDateTime, Long roomId