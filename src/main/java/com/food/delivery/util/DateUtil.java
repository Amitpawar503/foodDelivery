package com.food.delivery.util;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public final class DateUtil {

    private DateUtil() {
        // Utility class - prevent instantiation
    }

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_TIME_FORMATTER) : null;
    }

    public static String formatDate(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_FORMATTER) : null;
    }

    public static String formatTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(TIME_FORMATTER) : null;
    }

    public static LocalDateTime parseDateTime(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTimeString, DATE_TIME_FORMATTER);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date time format. Expected: yyyy-MM-dd HH:mm:ss");
        }
    }

    public static LocalDate parseDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dateString, DATE_FORMATTER);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format. Expected: yyyy-MM-dd");
        }
    }

    public static boolean isDateInRange(LocalDateTime date, LocalDateTime start, LocalDateTime end) {
        if (date == null) {
            return false;
        }
        
        if (start != null && date.isBefore(start)) {
            return false;
        }
        
        if (end != null && date.isAfter(end)) {
            return false;
        }
        
        return true;
    }

    public static long daysBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(start, end);
    }

    public static long hoursBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return 0;
        }
        return ChronoUnit.HOURS.between(start, end);
    }

    public static long minutesBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return 0;
        }
        return ChronoUnit.MINUTES.between(start, end);
    }

    public static boolean isExpired(LocalDateTime expiryDate) {
        if (expiryDate == null) {
            return false; // No expiry means never expires
        }
        return LocalDateTime.now().isAfter(expiryDate);
    }

    public static boolean isExpiringSoon(LocalDateTime expiryDate, long hoursThreshold) {
        if (expiryDate == null) {
            return false;
        }
        LocalDateTime threshold = LocalDateTime.now().plusHours(hoursThreshold);
        return expiryDate.isBefore(threshold);
    }

    public static LocalDateTime getStartOfDay(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.toLocalDate().atStartOfDay() : null;
    }

    public static LocalDateTime getEndOfDay(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.toLocalDate().atTime(23, 59, 59) : null;
    }

    public static LocalDateTime getStartOfWeek(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.toLocalDate()
                .minusDays(dateTime.getDayOfWeek().getValue() - 1)
                .atStartOfDay();
    }

    public static LocalDateTime getEndOfWeek(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.toLocalDate()
                .plusDays(7 - dateTime.getDayOfWeek().getValue())
                .atTime(23, 59, 59);
    }

    public static LocalDateTime getStartOfMonth(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.toLocalDate().withDayOfMonth(1).atStartOfDay() : null;
    }

    public static LocalDateTime getEndOfMonth(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.toLocalDate()
                .withDayOfMonth(dateTime.toLocalDate().lengthOfMonth())
                .atTime(23, 59, 59);
    }

    public static String getRelativeTimeString(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "Unknown";
        }

        LocalDateTime now = LocalDateTime.now();
        long minutes = ChronoUnit.MINUTES.between(dateTime, now);
        long hours = ChronoUnit.HOURS.between(dateTime, now);
        long days = ChronoUnit.DAYS.between(dateTime, now);

        if (minutes < 1) {
            return "Just now";
        } else if (minutes < 60) {
            return minutes + " minute" + (minutes == 1 ? "" : "s") + " ago";
        } else if (hours < 24) {
            return hours + " hour" + (hours == 1 ? "" : "s") + " ago";
        } else if (days < 7) {
            return days + " day" + (days == 1 ? "" : "s") + " ago";
        } else {
            return formatDate(dateTime);
        }
    }
}
