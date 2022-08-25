package ru.westlarry.userAccount.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.westlarry.userAccount.exception.CommonApiException;
import ru.westlarry.userAccount.exception.InsufficientFundsException;
import ru.westlarry.userAccount.exception.NonUniqueException;
import ru.westlarry.userAccount.exception.UserNotFoundException;
import ru.westlarry.userAccount.request.ApiError;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({UserNotFoundException.class, CommonApiException.class, NonUniqueException.class, InsufficientFundsException.class})
    public ResponseEntity<ApiError> handleException(Exception e) {
        if (e instanceof UserNotFoundException || e instanceof CommonApiException) {
            logger.error("ERROR {} " + e.getMessage());
            return new ResponseEntity<>(new ApiError(HttpStatus.NOT_FOUND.value(), e.getMessage()), HttpStatus.NOT_FOUND);
        }
        if (e instanceof NonUniqueException) {
            logger.error("ERROR {} " + e.getMessage());
            return new ResponseEntity<>(new ApiError(HttpStatus.CONFLICT.value(), e.getMessage()), HttpStatus.CONFLICT);
        }
        if (e instanceof InsufficientFundsException) {
            logger.error("ERROR {} " + e.getMessage());
            return new ResponseEntity<>(new ApiError(HttpStatus.FORBIDDEN.value(), e.getMessage()), HttpStatus.FORBIDDEN);
        }
        logger.error("ERROR {} " + e.getMessage());
        return new ResponseEntity<>(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
