package dev.isdn.demo.records_dto.app.domain.note;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record NoteContent(
        @JsonProperty("content") String content
) {
    @JsonCreator
    public NoteContent(String content) {
        this.content = content;
    }
}
