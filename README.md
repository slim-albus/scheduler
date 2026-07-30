# Scheduler Backend

This is the Java Spring Boot backend for the University Timetable & Resource Scheduler System.

## Prerequisites
- **Java 21** (or higher)

## How to Run

1. Open a terminal in this directory (`scheduler`).
2. Run the application using the Gradle wrapper:

### On Windows:
```bash
.\gradlew.bat bootRun
```

### On macOS / Linux:
```bash
./gradlew bootRun
```

The backend server will start running on `http://localhost:8080`.

*(Note: The database is a local SQLite file and will automatically initialize itself on startup.)*

## Instructions

Once both this backend and the Next.js frontend servers are running:

1. Open `http://localhost:3000` in your web browser.
2. The database is pre-seeded with test data via `SetupService` on first boot.
3. **Login Roles**:
   - **Administrator**: Log in with `admin_user` / `password`. Use the Admin Dashboard to click **Generate Schedule** and run the CSP algorithm.
   - **Instructor**: Log in with a teacher's email (e.g., `sami.saad@hilcoeschool.com` / `password`).
   - **Student**: Log in with a student section ID (e.g., `DRBSE2502A-1` / `password`).
4. **Live Validation**: You can view the Live Building Occupation map on the dashboard, which automatically highlights in-use rooms based on the current time and generated schedule.
