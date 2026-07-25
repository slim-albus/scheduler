package app.scheduler.services;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class LoggerService {

    private static final String RESET = "\u001B[0m";
    private static final String BLUE = "\u001B[34m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String CYAN = "\u001B[36m";

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    private void print(String color, String domain, String message) {
        String time = LocalDateTime.now().format(formatter);
        System.out.printf("%s[%s] [%s] %s%s%n", color, time, domain, message, RESET);
    }

    public void logAuth(String message) {
        print(BLUE, "AUTH", message);
    }

    public void logAdmin(String message) {
        print(CYAN, "ADMIN", message);
    }

    public void logSchedule(String message) {
        print(GREEN, "SCHEDULE", message);
    }

    public void logMap(String message) {
        print(YELLOW, "MAP", message);
    }

    public void logSystem(String message) {
        print(RESET, "SYSTEM", message);
    }

    public void logError(String message) {
        print(RED, "ERROR", message);
    }

    // Keep the old ones just in case there are still references while we refactor
    public void info(String message) {
        print(GREEN, "INFO", message);
    }

    public void error(String message) {
        print(RED, "ERROR", message);
    }
}
