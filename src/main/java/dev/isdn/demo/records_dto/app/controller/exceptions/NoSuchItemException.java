package dev.isdn.demo.records_dto.app.controller.exceptions;

public class NoSuchItemException extends RuntimeException {

    public NoSuchItemException(String msg) {
        super("No such item: " + msg);
    }
}
