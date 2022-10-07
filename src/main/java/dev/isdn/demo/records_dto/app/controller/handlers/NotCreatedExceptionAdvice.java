package dev.isdn.demo.records_dto.app.controller.handlers;

import dev.isdn.demo.records_dto.app.controller.exceptions.NotCreatedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class NotCreatedExceptionAdvice {

    @ResponseBody
    @ExceptionHandler(NotCreatedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String notCreatedExceptionHandler(NotCreatedException e) {
        return e.getMessage();
    }

}
