package app.scheduler.utils;

import app.scheduler.models.Semester;
import java.time.LocalDateTime;

public class TimeUtils {
    public static LocalDateTime getStartTime(int day, int period, Semester semester, int week) {
        // Dummy implementation for now
        return LocalDateTime.now();
    }
    
    public static String getPeriodLabel(int period) {
        String[] labels = {"P1 (8:00-9:30)", "P2 (9:45-11:15)", 
                          "P3 (11:30-13:00)", "P4 (14:00-15:30)", 
                          "P5 (15:45-17:15)"};
        if (period >= 0 && period < labels.length) return labels[period];
        return "Unknown";
    }
}
