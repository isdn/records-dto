package dev.isdn.demo.records_dto.app.controller.exceptions;

public class NotCreatedException extends RuntimeException {

    public NotCreatedException() {
        super("Item was not created");
    }
}
