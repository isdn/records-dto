package dev.isdn.demo.records_dto.app.domain.tag;

import dev.isdn.demo.records_dto.app.domain.common.AbstractEntity;
import dev.isdn.demo.records_dto.app.domain.common.Constants;
import dev.isdn.demo.records_dto.app.domain.note.Note;
import org.hibernate.annotations.Nationalized;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "tags")
@Table(name = "tags")
public class Tag extends AbstractEntity {

    @Nationalized
    @Column(nullable = false, unique = true, length = Constants.TAG_NAME_LENGTH)
    private String name;

    @Column(nullable = true, length = Constants.TAG_COLOR_LENGTH)
    private String color = Constants.DEFAULT_COLOR;

    @ManyToMany(
            mappedBy = "tags",
            fetch = FetchType.LAZY,
            cascade = { CascadeType.MERGE }
    )
    private Set<Note> notes = new HashSet<>();

    protected Tag() {
        this.name = "";
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    protected Set<Note> getNotes() {
        return notes;
    }

    protected Tag setName(String name) {
        this.name = name;
        return this;
    }

    protected Tag setColor(String color) {
        this.color = color;
        return this;
    }

}
