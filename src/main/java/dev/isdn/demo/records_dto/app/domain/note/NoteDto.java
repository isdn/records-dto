package dev.isdn.demo.records_dto.app.domain.note;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public record NoteDto(
    @JsonProperty("id") long id,
    @JsonProperty("created") long created,
    @JsonProperty("modified") long modified,
    @JsonProperty("content") String content
) {
    @JsonCreator
    public NoteDto(long id, long created, long modified, String content) {
        Objects.requireNonNull(content);
        this.id = id;
        this.created = created;
        this.modified = modified;
        this.content = content;
    }
}
