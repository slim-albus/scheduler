package app.scheduler.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateUtils {
    
    public static LocalDateTime parseSqliteTimestamp(String val) {
        if (val == null || val.trim().isEmpty()) {
            return null;
        }
        try {
            if (val.contains("T")) {
                return LocalDateTime.parse(val);
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return LocalDateTime.parse(val, formatter);
        } catch (DateTimeParseException e) {
            try {
                // Handle cases where it might be an epoch or other string format
                return java.sql.Timestamp.valueOf(val).toLocalDateTime();
            } catch (Exception ex) {
                return null;
            }
        }
    }

    public static java.time.LocalDate parseSqliteDate(String val) {
        if (val == null || val.trim().isEmpty()) return null;
        if (val.contains(" ")) val = val.split(" ")[0];
        else if (val.contains("T")) val = val.split("T")[0];
        try {
            return java.time.LocalDate.parse(val);
        } catch (Exception e) {
            return null;
        }
    }

    public static String formatSqliteTimestamp(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static String formatSqliteDate(java.time.LocalDate date) {
        if (date == null) return null;
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
