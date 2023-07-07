package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.utility.Generated;

import javax.validation.ValidationException;

@Generated
@Slf4j
@RestControllerAdvice
public class ErrorHandler {


    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleObjectNotFound(ObjectNotFoundException e) {
        log.warn(e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbidden(ForbiddenException e) {
        log.warn(e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleAlreadyExist(ObjectAlreadyExistException e) {
        log.warn(e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidateErrors(MethodArgumentNotValidException e) {
        log.warn(e.getFieldError().getDefaultMessage());
        return new ErrorResponse(e.getFieldError().getDefaultMessage());
    }

    @ExceptionHandler({ValidationException.class, UnknownStateException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidateErrors(RuntimeException e) {
        log.warn(e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(Throwable e) {
        log.warn(e.getMessage());
        return new ErrorResponse(e.getMessage());
    }
}
