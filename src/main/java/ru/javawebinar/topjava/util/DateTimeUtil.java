package ru.javawebinar.topjava.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static boolean isBetweenHalfOpenByTime(LocalTime lt, LocalTime startTime, LocalTime endTime) {
        return lt.compareTo(startTime) >= 0 && lt.compareTo(endTime) < 0;
    }

    public static boolean isBetweenHalfOpenByDate(LocalDate ld, LocalDate startDate, LocalDate endDate) {
        return ld.compareTo(startDate) >= 0 && ld.compareTo(endDate) <= 0;
    }

    public static LocalDate toLocalDateOrMin(String startDate) {
        return startDate == null || startDate.isEmpty() ? LocalDate.MIN : LocalDate.parse(startDate);
    }

    public static LocalDate toLocalDateOrMax(String endDate) {
        return endDate == null || endDate.isEmpty() ? LocalDate.MAX : LocalDate.parse(endDate);
    }

    public static LocalTime toLocalTimeOrMin(String startTime) {
        return startTime == null || startTime.isEmpty() ? LocalTime.MIN : LocalTime.parse(startTime);
    }

    public static LocalTime toLocalTimeOrMax(String endTime) {
        return endTime == null || endTime.isEmpty() ? LocalTime.MAX : LocalTime.parse(endTime);
    }

    public static String toString(LocalDateTime ldt) {
        return ldt == null ? "" : ldt.format(DATE_TIME_FORMATTER);
    }
}