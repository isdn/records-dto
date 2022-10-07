package dev.isdn.demo.records_dto.app.domain.tag;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public record TagDto(
    @JsonProperty("id") long id,
    @JsonProperty("name") String name,
    @JsonProperty("color") String color
) {
    @JsonCreator
    public TagDto(long id, String name, String color) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(color);
        this.id = id;
        this.name = name;
        this.color = color;
    }
}
