package dev.isdn.demo.records_dto.app.domain.tag;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record TagContent(
        @JsonProperty("name") String name,
        @JsonProperty("color") String color
) {
    @JsonCreator
    public TagContent(String name, String color) {
        this.name = name;
        this.color = color;
    }
}
