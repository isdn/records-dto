package dev.isdn.demo.records_dto.app.domain.tag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.QueryHint;
import java.util.Optional;
import java.util.stream.Stream;

import static org.hibernate.annotations.QueryHints.READ_ONLY;
import static org.hibernate.jpa.QueryHints.HINT_FETCH_SIZE;

@Transactional
public interface TagRepository extends JpaRepository<Tag, Long> {

    void deleteById(long id);

    @Transactional(readOnly = true)
    boolean existsById(long id);

    @Transactional(readOnly = true)
    boolean existsByName(String name);

    @Transactional(readOnly = true)
    Optional<Tag> getTagById(long id);

    @QueryHints(value = {
            @QueryHint(name = READ_ONLY, value = "true")
    })
    @Transactional(readOnly = true)
    @Query(value = "SELECT new dev.isdn.demo.records_dto.app.domain.tag.TagDto(t.id, t.name, t.color) FROM tags t WHERE t.id = :id")
    Optional<TagDto> findById(@Param("id") long id);

    @QueryHints(value = {
            @QueryHint(name = HINT_FETCH_SIZE, value = "100"),
            @QueryHint(name = READ_ONLY, value = "true")
    })
    @Transactional(readOnly = true)
    @Query(value = "SELECT new dev.isdn.demo.records_dto.app.domain.tag.TagDto(t.id, t.name, t.color) FROM tags t INNER JOIN t.notes n WHERE n.id = :noteId")
    Stream<TagDto> findAllByNoteId(@Param("noteId") long noteId);

    @QueryHints(value = {
            @QueryHint(name = HINT_FETCH_SIZE, value = "100"),
            @QueryHint(name = READ_ONLY, value = "true")
    })
    @Transactional(readOnly = true)
    @Query(value = "SELECT new dev.isdn.demo.records_dto.app.domain.tag.TagDto(t.id, t.name, t.color) FROM tags t")
    Stream<TagDto> fetchAllTags();

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE tags t SET t.name = :name WHERE t.id = :id")
    int updateTagNameById(@Param("id") long id, @Param("name") String name);

    @QueryHints(value = {
            @QueryHint(name = READ_ONLY, value = "true")
    })
    @Transactional(readOnly = true)
    @Query("SELECT COUNT(t) FROM tags t INNER JOIN t.notes n WHERE n.id = :noteId")
    long countAllByNoteId(@Param("noteId") long noteId);
}
