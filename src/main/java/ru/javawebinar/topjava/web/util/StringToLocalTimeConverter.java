package ru.javawebinar.topjava.web.util;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Component("stringToLocalTimeConverter")
public class StringToLocalTimeConverter implements Converter<String, LocalTime> {
    private static final String TIME_PATTERN = "HH:mm";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_PATTERN);

    @Override
    public LocalTime convert(String timeString) {
        return LocalTime.parse(timeString, TIME_FORMATTER);
    }
}
