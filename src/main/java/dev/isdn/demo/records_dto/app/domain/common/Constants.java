package dev.isdn.demo.records_dto.app.domain.common;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public record Constants() {

    public static final int TAG_NAME_LENGTH = 128;
    public static final int TAG_COLOR_LENGTH = 6;
    public static final String DEFAULT_COLOR = "000000";
    public static final Predicate<String> TAG_NAME_PREDICATE = Pattern.compile("^[\\p{Alnum}-_+*&?<>@]+$").asPredicate();
    public static final Predicate<String> TAG_COLOR_PREDICATE = Pattern.compile("^[0-9a-fA-F]+$").asPredicate();

}