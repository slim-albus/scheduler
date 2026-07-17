AuthService

authenticate(username, password): Verifies username, matches BCrypt credentials.

createSession(userId): Generates a random secure token, sets expiration, and saves it.

invalidateSession(token): Deletes active token session.

verifySession(token): Checks if a token exists and is not expired.

UserService

getUserWithProfile(userId): Fetches the User entity and looks up their corresponding Student or Teacher profile ID map.

createUser(username, plainPassword, role): Hashes password and handles onboarding setup.

EventService

getEventsForStudent(studentId): Aggregates student section events.

getLiveOccupancy(dateTime): Correlates all rooms with active events at a specific time.

rescheduleEvent(eventId, targetTime, roomId): Executes conflict verification before updating database records.

changeEventStatus(eventId, status): Sets state variables (e.g., "CANCELLED").

ScheduleGenerationService

autoGenerateSemesterGrid(semesterId, assignments): Processes the nested looping matrix (Days × Time slots × Rooms) to build out clean schedules safely.