package dev.isdn.demo.records_dto.app.controller.handlers;

import dev.isdn.demo.records_dto.app.controller.exceptions.NoSuchItemException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class NoSuchItemExceptionAdvice {

    @ResponseBody
    @ExceptionHandler(NoSuchItemException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String noSuchItemExceptionHandler(NoSuchItemException e) {
        return e.getMessage();
    }
}
