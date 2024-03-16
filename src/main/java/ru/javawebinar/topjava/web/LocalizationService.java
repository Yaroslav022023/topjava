package ru.javawebinar.topjava.web;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class LocalizationService {
    private final MessageSource messageSource;

    public LocalizationService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getLocalizedMessage(String messageCode, Locale locale) {
        return messageSource.getMessage(messageCode, new Object[]{}, locale);
    }

    public String getMessage(MessageSourceResolvable resolvable, Locale locale) {
        return messageSource.getMessage(resolvable, locale);
    }
}
