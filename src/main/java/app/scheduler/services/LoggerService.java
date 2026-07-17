package app.scheduler.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LoggerService {

    private static final Logger logger = LoggerFactory.getLogger(LoggerService.class);

    /**
     * Log a simple informational message.
     */
    public void info(String message) {
        logger.info(message);
    }

    /**
     * Log an action performed by a specific component (e.g., a Controller).
     */
    public void logAction(String component, String action) {
        logger.info("[{}] performed action: {}", component, action);
    }

    /**
     * Log an error message.
     */
    public void error(String message) {
        logger.error(message);
    }

    /**
     * Log an error with an exception stack trace.
     */
    public void error(String component, String message, Throwable throwable) {
        logger.error("[{}] Error: {}", component, message, throwable);
    }

    /**
     * Simple sysout fallback if standard logger isn't preferred.
     */
    public void consoleLog(String message) {
        System.out.println("[" + LocalDateTime.now() + "] CONSOLE LOG: " + message);
    }
}
