package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import ru.javawebinar.topjava.AuthorizedUser;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.Objects;

import static ru.javawebinar.topjava.util.exception.ErrorType.DATA_ERROR;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView defaultErrorHandler(HttpServletRequest req, Exception e, Locale locale) {
        log.error("Exception at request " + req.getRequestURL(), e);
        ModelAndView mav = new ModelAndView();
        mav.setViewName("exception");

        if (e instanceof DataIntegrityViolationException dive) {
            conflict(mav, dive, locale);
        } else {
            internalError(mav, locale);
        }

        // Interceptor is not invoked, put userTo
        AuthorizedUser authorizedUser = SecurityUtil.safeGet();
        if (authorizedUser != null) {
            mav.addObject("userTo", authorizedUser.getUserTo());
        }
        return mav;
    }

    private void conflict(ModelAndView mav, DataIntegrityViolationException dive, Locale locale) {
        mav.setStatus(HttpStatus.CONFLICT);
        if (Objects.requireNonNull(dive.getMessage()).contains("users_unique_email_idx")) {
            mav.addObject("typeMessage", getLocalizedMessage(DATA_ERROR.getCode(), locale));
            mav.addObject("details", getLocalizedMessage("user.duplicateEmailError", locale));
        }
    }

    private void internalError(ModelAndView mav, Locale locale) {
        mav.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        mav.addObject("typeMessage", getLocalizedMessage("common.appError", locale));
    }

    private String getLocalizedMessage(String messageCode, Locale locale) {
        return messageSource.getMessage(messageCode, new Object[]{}, locale);
    }
}
