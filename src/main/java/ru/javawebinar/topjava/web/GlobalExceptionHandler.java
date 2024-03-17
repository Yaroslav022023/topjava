package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import ru.javawebinar.topjava.AuthorizedUser;
import ru.javawebinar.topjava.util.ValidationUtil;
import ru.javawebinar.topjava.util.exception.ErrorType;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static ru.javawebinar.topjava.util.exception.ErrorType.APP_ERROR;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ModelAndView defaultErrorHandler(HttpServletRequest req, Exception e) {
        Throwable rootCause = ValidationUtil.getRootCause(e);
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        Map<String, Object> details = new HashMap<>(Map.of(
                "exception", rootCause,
                "message", rootCause.toString(),
                "status", httpStatus
        ));
        return logAndCreateModel(req, e, true, APP_ERROR, details, "exception", httpStatus);
    }

    private ModelAndView logAndCreateModel(HttpServletRequest req, Exception e, boolean logException,
                                           ErrorType errorType, Map<String, Object> details, String viewName,
                                           HttpStatus httpStatus) {
        logging(req, logException, errorType, ValidationUtil.getRootCause(e), details);
        ModelAndView mav = new ModelAndView(viewName, details);
        mav.setStatus(httpStatus);

        AuthorizedUser authorizedUser = SecurityUtil.safeGet();
        if (authorizedUser != null) {
            mav.addObject("userTo", authorizedUser.getUserTo());
        }
        return mav;
    }

    private void logging(HttpServletRequest req, boolean logException, ErrorType errorType,
                         Throwable rootCause, Map<String, Object> details) {
        if (logException) {
            log.error("{} at request {}: ", errorType.toString(), req.getRequestURL(), rootCause);
        } else {
            log.warn("{} at request {}: {}", errorType.toString(), req.getRequestURL(), details);
        }
    }
}
