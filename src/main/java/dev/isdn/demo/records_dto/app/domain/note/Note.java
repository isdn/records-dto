package dev.isdn.demo.records_dto.app.domain.note;

import dev.isdn.demo.records_dto.app.domain.common.AbstractEntity;
import dev.isdn.demo.records_dto.app.domain.tag.Tag;
import org.hibernate.annotations.Nationalized;
import javax.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "notes")
@Table(name = "notes")
public class Note extends AbstractEntity {

    @Column(nullable = false)
    private long created;

    @Column(nullable = false)
    private long modified;

    @Lob
    @Nationalized
    @Column(nullable = false)
    private String content;

    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade = { CascadeType.MERGE }
    )
    @JoinTable(
            name = "note_tag",
            joinColumns = @JoinColumn(name = "note_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id")
    )
    private Set<Tag> tags = new HashSet<>();

    protected Note() {
        this.created = this.modified = Instant.now().getEpochSecond();
        this.content = "";
    }

    public long getCreated() {
        return created;
    }

    public long getModified() {
        return modified;
    }

    public String getContent() {
        return content;
    }

    protected Set<Tag> getTags() {
        return tags;
    }

    protected Note setContent(String content) {
        this.modified = Instant.now().getEpochSecond();
        this.content = content;
        return this;
    }

    protected Note addTag(Tag tag) {
        this.tags.add(tag);
        return this;
    }

    protected Note deleteTag(Tag tag) {
        this.tags.remove(tag);
        return this;
    }

}
