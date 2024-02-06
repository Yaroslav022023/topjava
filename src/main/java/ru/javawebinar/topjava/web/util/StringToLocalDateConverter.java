package ru.javawebinar.topjava.web.util;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component()
public class StringToLocalDateConverter implements Converter<String, LocalDate> {
    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);

    @Override
    public LocalDate convert(String dateString) {
        return LocalDate.parse(dateString, DATE_FORMATTER);
    }
}
