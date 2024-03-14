package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.javawebinar.topjava.util.ValidationUtil;
import ru.javawebinar.topjava.util.exception.ErrorInfo;
import ru.javawebinar.topjava.util.exception.ErrorType;
import ru.javawebinar.topjava.util.exception.IllegalRequestDataException;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static ru.javawebinar.topjava.util.exception.ErrorType.*;

@RestControllerAdvice(annotations = RestController.class)
@Order(Ordered.HIGHEST_PRECEDENCE + 5)
public class ExceptionInfoHandler {
    private static final Logger log = LoggerFactory.getLogger(ExceptionInfoHandler.class);
    private final MessageSource messageSource;

    public ExceptionInfoHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    //  http://stackoverflow.com/a/22358422/548473
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(NotFoundException.class)
    public ErrorInfo notFoundError(HttpServletRequest req, NotFoundException e, Locale locale) {
        String typeMessage = getLocalizedMessage(DATA_NOT_FOUND.getCode(), new Object[]{}, locale);
        return logAndGetErrorInfo(req, e, false, DATA_NOT_FOUND, typeMessage, locale);
    }

    @ResponseStatus(HttpStatus.CONFLICT)  // 409
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorInfo conflict(HttpServletRequest req, DataIntegrityViolationException e, Locale locale) {
        String typeMessage = getLocalizedMessage(DATA_ERROR.getCode(), new Object[]{}, locale);
        return logAndGetErrorInfo(req, e, true, DATA_ERROR, typeMessage, locale);
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)  // 422
    @ExceptionHandler({IllegalRequestDataException.class, MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class, BindException.class, MethodArgumentNotValidException.class})
    public ErrorInfo validationError(HttpServletRequest req, Exception e, Locale locale) {
        String typeMessage = getLocalizedMessage(VALIDATION_ERROR.getCode(), new Object[]{}, locale);
        return logAndGetErrorInfo(req, e, false, VALIDATION_ERROR, typeMessage, locale);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorInfo internalError(HttpServletRequest req, Exception e, Locale locale) {
        String typeMessage = getLocalizedMessage(APP_ERROR.getCode(), new Object[]{}, locale);
        return logAndGetErrorInfo(req, e, true, APP_ERROR, typeMessage, locale);
    }

    private String getLocalizedMessage(String messageCode, Object[] args, Locale locale) {
        return messageSource.getMessage(messageCode, args, locale);
    }

    //    https://stackoverflow.com/questions/538870/should-private-helper-methods-be-static-if-they-can-be-static
    private ErrorInfo logAndGetErrorInfo(HttpServletRequest req, Exception e, boolean logException,
                                         ErrorType errorType, String typeMessage, Locale locale) {
        Set<String> details = new HashSet<>();
        generateMessage(e, details, locale, typeMessage);
        logging(req, logException, errorType, ValidationUtil.getRootCause(e), details);
        return new ErrorInfo(req.getRequestURL(), errorType, typeMessage, details);
    }

    private void generateMessage(Exception e, Set<String> details, Locale locale, String typeMessage) {
        if (e instanceof BindException bindException) {
            formattingValidationError(bindException, details, locale);
        } else if (e instanceof DataIntegrityViolationException dive) {
            formattingConflictError(dive, details, locale, typeMessage);
        } else {
            details.add(typeMessage);
        }
    }

    private void formattingValidationError(BindException bindException, Set<String> details, Locale locale) {
        bindException.getFieldErrors().forEach(fieldError ->
                details.add("[" + fieldError.getField() + "] " + messageSource.getMessage(fieldError, locale)));
    }

    private void formattingConflictError(DataIntegrityViolationException dive, Set<String> details,
                                         Locale locale, String typeMessage) {
        String constraint = dive.getMessage();
        if (constraint.contains("meal_unique_user_datetime_idx")) {
            details.add(getLocalizedMessage("meal.duplicateError", new Object[]{}, locale));
        } else if (constraint.contains("users_unique_email_idx")) {
            details.add(getLocalizedMessage("user.duplicateEmailError", new Object[]{}, locale));
        } else {
            details.add(typeMessage);
        }
    }

    private void logging(HttpServletRequest req, boolean logException, ErrorType errorType,
                         Throwable rootCause, Set<String> details) {
        if (logException) {
            log.error("{} at request {}: ", errorType.toString(), req.getRequestURL(), rootCause);
        } else {
            log.warn("{} at request {}: {}", errorType.toString(), req.getRequestURL(), details);
        }
    }
}