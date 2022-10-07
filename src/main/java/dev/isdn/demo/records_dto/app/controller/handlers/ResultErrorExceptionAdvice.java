package dev.isdn.demo.records_dto.app.controller.handlers;

import dev.isdn.demo.records_dto.app.controller.exceptions.ResultErrorException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ResultErrorExceptionAdvice {

    @ResponseBody
    @ExceptionHandler(ResultErrorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    void resultErrorExceptionHandler() {}
}
