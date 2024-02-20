package ru.javawebinar.topjava.web;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.util.stream.Collectors;

@Component
public class HandlerException {

    public ResponseEntity<String> handleException(BindingResult result) {
        String errorFieldsMsg = result.getFieldErrors().stream()
                .map(fe -> String.format("[%s] %s", fe.getField(), fe.getDefaultMessage()))
                .collect(Collectors.joining("<br>"));
        return ResponseEntity.unprocessableEntity().body(errorFieldsMsg);
    }
}
