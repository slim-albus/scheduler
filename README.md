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
