package dev.isdn.demo.records_dto.app.controller.handlers;

import dev.isdn.demo.records_dto.app.controller.exceptions.NotUpdatedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class NotUpdatedExceptionAdvice {

    @ResponseBody
    @ExceptionHandler(NotUpdatedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String notUpdatedExceptionHandler(NotUpdatedException e) {
        return e.getMessage();
    }

}
