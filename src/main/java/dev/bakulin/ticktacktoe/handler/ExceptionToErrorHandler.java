package dev.bakulin.ticktacktoe.handler;

import dev.bakulin.ticktacktoe.exception.GameplayError;
import dev.bakulin.ticktacktoe.model.ErrorInfo;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.stream.Collectors;

@ControllerAdvice
public class ExceptionToErrorHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ErrorInfo handleValidationError(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getAllErrors()
                .stream()
                .map(er -> ((FieldError) er).getField() + ": " + er.getDefaultMessage())
                .collect(Collectors.joining("."));

        return new ErrorInfo("Request validation error", message);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseBody
    public ErrorInfo handleValidationError(MissingRequestHeaderException ex) {
        return new ErrorInfo("Required header missed", "Header is missed: " + ex.getHeaderName());
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(GameplayError.class)
    @ResponseBody
    public ErrorInfo handleGameplayError(GameplayError ex) {
        return new ErrorInfo(ex.getError(), ex.getDetails());
    }
}
