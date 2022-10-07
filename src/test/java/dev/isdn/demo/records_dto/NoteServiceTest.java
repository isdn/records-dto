package dev.isdn.demo.records_dto;

import dev.isdn.demo.records_dto.app.domain.note.Note;
import dev.isdn.demo.records_dto.app.domain.note.NoteDto;
import dev.isdn.demo.records_dto.app.domain.note.NoteRepository;
import dev.isdn.demo.records_dto.app.domain.note.NoteService;
import dev.isdn.demo.records_dto.app.domain.common.Result;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;


@SpringBootTest(
        properties = {"spring.config.name=test-config"
                //,"spring.jpa.show-sql=true"
                //,"spring.jpa.properties.hibernate.format_sql=true"
        },
        classes = {App.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NoteServiceTest {

    @Autowired
    NoteRepository noteRepository;

    @Autowired
    NoteService noteService;

    @AfterEach
    void tearDown() {
        noteRepository.deleteAllInBatch();
    }

    @Test
    @Order(1)
    @DisplayName("Test note creation")
    void testCreateNote() {
        String content = "test note content";
        Optional<NoteDto> noteDto = noteService.createNote(content);
        assertThat(noteDto).as("note creation check")
                .isPresent().get()
                .satisfies(n -> {
                    assertThat(n.id()).isGreaterThan(0);
                    assertThat(n.content()).isEqualTo(content);
                    assertThat(n.created()).isEqualTo(n.modified()).isGreaterThan(0);
                });

        long noteId = noteDto.orElseThrow().id();
        Note note = noteRepository.getNoteById(noteId).orElseThrow();
        assertThat(note).as("created note verification")
                .satisfies(n ->
                        assertThat(n.getContent()).isEqualTo(content)
                );
    }

    @Test
    @Order(2)
    @DisplayName("Test note content update")
    void testUpdateNoteContent() {
        String content = "test note content", newContent = "new test note content";
        Optional<NoteDto> noteDto = noteService.createNote(content);
        assertThat(noteDto).as("note creation check")
                .isPresent().get()
                .satisfies(n -> {
                    assertThat(n.id()).isGreaterThan(0);
                    assertThat(n.content()).isEqualTo(content);
                });

        long noteId = noteDto.orElseThrow().id();

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Optional<NoteDto> newNoteDto = noteService.updateNoteContent(noteDto.orElseThrow(), newContent);

        assertThat(newNoteDto).as("new content was set")
                .isPresent().get()
                .satisfies(n -> {
                    assertThat(n.id()).isEqualTo(noteId);
                    assertThat(n.content()).isEqualTo(newContent);
                    assertThat(n.created()).isEqualTo(noteDto.orElseThrow().created());
                    assertThat(n.modified()).isGreaterThan(noteDto.orElseThrow().modified());
                });

        Note note = noteRepository.getNoteById(noteId).orElseThrow();
        assertThat(note).as("updated note verification").satisfies(n -> {
            assertThat(n.getContent()).isEqualTo(newContent);
            assertThat(n.getCreated()).isEqualTo(noteDto.orElseThrow().created());
            assertThat(n.getModified()).isEqualTo(newNoteDto.orElseThrow().modified());
        });
    }


    @Test
    @Order(3)
    @DisplayName("Test note content update with invalid args")
    void testSetNotValidNoteContent() {
        String content = "test note content", newContent = "new test note content";
        String nullContent = null;
        Optional<NoteDto> noteDto = noteService.createNote(content);
        Optional<NoteDto> notValidNoteDto = Optional.of(new NoteDto(12345, 12345, 12345, content));

        assertThat(noteService.updateNoteContent(notValidNoteDto.orElseThrow(), newContent))
                .as("passing not valid DTO record")
                .isEmpty();
        assertThat(noteService.updateNoteContent(null, content))
                .as("passing null as DTO record")
                .isEmpty();

        assertThat(noteDto).as("note creation")
                .isPresent().get()
                .satisfies(n -> {
                    assertThat(n.id()).isGreaterThan(0);
                    assertThat(n.content()).isEqualTo(content);
                });
        assertThat(noteService.updateNoteContent(noteDto.orElseThrow(), nullContent))
                .as("passing null as content")
                .isEmpty();
        assertThat(noteService.updateNoteContent(noteDto.orElseThrow(), newContent))
                .as("normal update")
                .isPresent();
        assertThat(noteService.updateNoteContent(noteDto.orElseThrow(), newContent))
                .as("passing the same content")
                .isPresent().get()
                .satisfies(n -> {
                    assertThat(n.id()).isGreaterThan(0);
                    assertThat(n.content()).isEqualTo(newContent);
                });
    }

    @Test
    @Order(4)
    @DisplayName("Test get note by ID")
    void testGetNoteById() {
        Optional<NoteDto> noteDto = noteService.createNote("test note content");
        long noteId = noteDto.orElseThrow().id();

        assertThat(noteService.getNoteById(noteId)).as("note should be the same")
                .isPresent().get()
                .isEqualTo(noteDto.orElseThrow());
    }

    @Test
    @Order(5)
    @DisplayName("Test get all notes")
    void testGetAllNotes() {
        int notesNum = 50;
        String prefix = "test note content ";
        IntStream.rangeClosed(1, notesNum).forEach(i -> noteService.createNote(prefix + i));

        List<NoteDto> notes = noteService.getAllNotes();
        assertThat(notes).as("notes list size is " + notesNum)
                .hasSize(notesNum)
                .as("notes list contains some specific elements")
                .filteredOn(n ->
                        n.content().equals(prefix + "1")
                                || n.content().equals(prefix + "20")
                                || n.content().equals(prefix + "50")
                ).hasSize(3);
    }

    @Test
    @Order(6)
    @DisplayName("Test delete note by ID")
    void testDeleteNoteById() {
        int notesNum = 50;
        String prefix = "test note content ";
        IntStream.rangeClosed(1, notesNum).forEach(i -> noteService.createNote(prefix + i));
        NoteDto note = noteService.getAllNotes().get(notesNum - 1);

        long failId = note.id() + 1;
        Result failedResult = noteService.deleteNoteById(failId);
        if (failedResult instanceof Result.NoSuchElement actualResult) {
            assertThat(actualResult.element()).isEqualTo(failId);
        } else {
            fail("deleted element with ID " + failId);
        }

        Result result = noteService.deleteNoteById(note.id());
        assertThat(result).as("result should be Ok").isExactlyInstanceOf(Result.Ok.class);
        assertThat(noteService.getNoteById(note.id())).isEmpty();
        assertThat(noteService.getAllNotes()).hasSize(notesNum - 1);

        Result anotherResult = noteService.deleteNoteById(note.id());
        assertThat(anotherResult).as("this attempt should return NoSuchElement")
                .isExactlyInstanceOf(Result.NoSuchElement.class);
    }

    @Test
    @Order(7)
    @DisplayName("Test delete note")
    void testDeleteNote() {
        int notesNum = 50;
        String prefix = "test note content ";
        IntStream.rangeClosed(1, notesNum).forEach(i -> noteService.createNote(prefix + i));
        NoteDto note = noteService.getAllNotes().get(notesNum - 1);

        Result result = noteService.deleteNote(note);
        assertThat(result).as("result should be Ok").isExactlyInstanceOf(Result.Ok.class);
        assertThat(noteService.getNoteById(note.id())).isEmpty();

        Result anotherResult = noteService.deleteNote(note);
        assertThat(anotherResult).as("result should be NoSuchElement")
                .isExactlyInstanceOf(Result.NoSuchElement.class)
                .extracting("element").isEqualTo(note.id());

        Result nullResult = noteService.deleteNote(null);
        assertThat(nullResult).as("result should be Error").isExactlyInstanceOf(Result.Error.class);
    }

}
