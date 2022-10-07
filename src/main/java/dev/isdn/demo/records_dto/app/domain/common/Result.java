package dev.isdn.demo.records_dto.app.domain.common;

public sealed interface Result {
    record Ok() implements Result {};
    record NoSuchElement(long element) implements Result {};
    record Error() implements Result {};
}
