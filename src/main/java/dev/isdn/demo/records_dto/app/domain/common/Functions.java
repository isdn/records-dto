package dev.isdn.demo.records_dto.app.domain.common;

import dev.isdn.demo.records_dto.app.domain.note.NoteDto;
import dev.isdn.demo.records_dto.app.domain.tag.TagDto;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public record Functions() {
    public static final Function<String, Optional<String>> checkTagName =
            (String name) ->
                    Optional.ofNullable(name)
                            .map(String::trim)
                            .filter(Predicate.not(String::isEmpty))
                            .filter(s -> Constants.TAG_NAME_LENGTH >= s.length())
                            .filter(Constants.TAG_NAME_PREDICATE);

    public static final Function<String, Optional<String>> checkTagColor =
            (String color) ->
                    Optional.ofNullable(color)
                            .map(String::trim)
                            .filter(s -> Constants.TAG_COLOR_LENGTH == s.length())
                            .filter(Constants.TAG_COLOR_PREDICATE)
                            .map(s -> s.toUpperCase(Locale.ROOT));

    public static final Function<TagDto, Optional<TagDto>> checkTagDto =
            (TagDto tag) ->
                    Optional.ofNullable(tag).filter(t -> t.id() > 0);

    public static final Function<NoteDto, Optional<NoteDto>> checkNoteDto =
            (NoteDto note) ->
                    Optional.ofNullable(note).filter(n -> n.id() > 0);

    public static final Function<Record, Optional<Record>> checkDtoRecord =
            (Record dto) -> switch (dto) {
                case null -> Optional.empty();
                case NoteDto n && n.id() > 0 -> Optional.of(n);
                case TagDto t && t.id() > 0 -> Optional.of(t);
                default -> Optional.empty();
            };

}
