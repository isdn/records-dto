package dev.isdn.demo.records_dto.app.domain.note;

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
public interface NoteRepository extends JpaRepository<Note, Long> {

    void deleteById(long id);

    @Transactional(readOnly = true)
    boolean existsById(long id);

    @Transactional(readOnly = true)
    Optional<Note> getNoteById(long id);

    @QueryHints(value = {
            @QueryHint(name = READ_ONLY, value = "true")
    })
    @Transactional(readOnly = true)
    @Query(value = "SELECT new dev.isdn.demo.records_dto.app.domain.note.NoteDto(n.id, n.created, n.modified, n.content) FROM notes n WHERE n.id = :id")
    Optional<NoteDto> findById(@Param("id") long id);

    @QueryHints(value = {
            @QueryHint(name = HINT_FETCH_SIZE, value = "100"),
            @QueryHint(name = READ_ONLY, value = "true")
    })
    @Transactional(readOnly = true)
    @Query(value = "SELECT new dev.isdn.demo.records_dto.app.domain.note.NoteDto(n.id, n.created, n.modified, n.content) FROM notes n INNER JOIN n.tags t WHERE t.id = :tagId")
    Stream<NoteDto> findAllByTagId(@Param("tagId") long tagId);

    @QueryHints(value = {
            @QueryHint(name = HINT_FETCH_SIZE, value = "100"),
            @QueryHint(name = READ_ONLY, value = "true")
    })
    @Transactional(readOnly = true)
    @Query(value = "SELECT new dev.isdn.demo.records_dto.app.domain.note.NoteDto(n.id, n.created, n.modified, n.content) FROM notes n")
    Stream<NoteDto> fetchAllNotes();

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE notes n SET n.content = :content, n.modified = :modified WHERE n.id = :id")
    int updateNoteContentById(@Param("id") long id, @Param("content") String content, @Param("modified") long modified);

    @QueryHints(value = {
            @QueryHint(name = READ_ONLY, value = "true")
    })
    @Transactional(readOnly = true)
    @Query("SELECT COUNT(n) FROM notes n INNER JOIN n.tags t WHERE t.id = :tagId")
    long countAllByTagId(@Param("tagId") long tagId);
}
