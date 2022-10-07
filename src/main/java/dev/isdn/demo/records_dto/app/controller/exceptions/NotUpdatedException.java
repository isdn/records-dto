package dev.isdn.demo.records_dto.app.controller.exceptions;

public class NotUpdatedException extends RuntimeException {

    public NotUpdatedException(String msg) {
        super("Item was not updated: " + msg);
    }
}
