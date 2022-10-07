package dev.isdn.demo.records_dto;

import dev.isdn.demo.records_dto.app.domain.note.NoteDto;
import dev.isdn.demo.records_dto.app.domain.note.NoteRepository;
import dev.isdn.demo.records_dto.app.domain.note.NoteService;
import dev.isdn.demo.records_dto.app.domain.tag.TagDto;
import dev.isdn.demo.records_dto.app.domain.tag.TagRepository;
import dev.isdn.demo.records_dto.app.domain.tag.TagService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        properties = {"spring.config.name=test-config"
                //,"spring.jpa.show-sql=true"
                //,"spring.jpa.properties.hibernate.format_sql=true"
                // ,"spring.jpa.hibernate.ddl-auto=create"
        },
        classes = {App.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NoteTagServicesTest {

    @Autowired
    TagRepository tagRepository;

    @Autowired
    TagService tagService;

    @Autowired
    NoteRepository noteRepository;

    @Autowired
    NoteService noteService;

    @AfterEach
    void tearDown() {
        tagRepository.deleteAllInBatch();
        noteRepository.deleteAllInBatch();
    }

    @Test
    @Order(1)
    @DisplayName("Test add tag to note")
    void testAddTagToNote() {
        int tagsNum = 50;
        String prefix = "test_tag_";
        List<String> tagNames = List.of("test_tag_100", "test_tag_200");
        IntStream.rangeClosed(1, tagsNum).forEach(i -> tagService.createTag(prefix + i));
        List<TagDto> tags = tagService.getAllTags();
        assertThat(tags).as("tags list size is " + tagsNum).hasSize(tagsNum);

        Optional<NoteDto> note1 = noteService.createNote("test note 1");
        Optional<NoteDto> note2 = noteService.createNote("test note 2");
        Optional<NoteDto> note3 = noteService.createNote("test note 3");
        Optional<TagDto> tag1 = tagService.createTag(tagNames.get(0));
        Optional<TagDto> tag2 = tagService.createTag(tagNames.get(1));

        tags.forEach(t -> assertThat(noteService.addTagToNote(note1.orElseThrow(), t)).isPresent());

        assertThat(noteService.addTagToNote(note2.orElseThrow(), tag1.orElseThrow())).isPresent();
        assertThat(noteService.addTagToNote(note2.orElseThrow(), tag1.orElseThrow())).isEmpty();
        assertThat(noteService.addTagToNote(note2.orElseThrow(), tag2.orElseThrow())).isPresent();
        assertThat(noteService.addTagToNote(note2.orElseThrow(), tag2.orElseThrow())).isEmpty();

        assertThat(tagService.getAllTags()).hasSize(tagsNum + 2);

        assertThat(noteService.getNoteById(note1.orElseThrow().id()))
                .as("check note1")
                .isPresent().get()
                .satisfies(n ->
                        assertThat(tagService.getNoteTags(n))
                                .as("check note1 tags")
                                .hasSize(tagsNum)
                                .filteredOn(t ->
                                        t.name().equals(prefix + "1")
                                                || t.name().equals(prefix + "10")
                                                || t.name().equals(prefix + "50")
                                ).hasSize(3)
                );
        assertThat(noteService.getNoteById(note2.orElseThrow().id()))
                .as("check note2")
                .isPresent().get()
                .satisfies(n ->
                    assertThat(tagService.getNoteTags(n))
                            .as("check note2 tags")
                            .hasSize(2)
                            .filteredOn(t ->
                                    t.name().equals(tagNames.get(0)) || t.name().equals(tagNames.get(1))
                            ).hasSize(2)
                );
        assertThat(noteService.getNoteById(note3.orElseThrow().id()))
                .as("check note3")
                .isPresent().get()
                .satisfies(n ->
                        assertThat(tagService.getNoteTags(n))
                                .as("check note3 tags")
                                .hasSize(0)
                );
    }

    @Test
    @Order(2)
    @DisplayName("Test get note tags")
    void testGetNoteTags() {
        int tagsNum = 50;
        String prefix = "test_tag_";
        IntStream.rangeClosed(1, tagsNum).forEach(i -> tagService.createTag(prefix + i));
        List<TagDto> tags = tagService.getAllTags();
        assertThat(tags).as("tags list size is " + tagsNum).hasSize(tagsNum);

        Optional<NoteDto> note1 = noteService.createNote("test note 1");
        Optional<NoteDto> note2 = noteService.createNote("test note 2");
        Optional<NoteDto> note3 = noteService.createNote("test note 3");

        tags.forEach(t -> noteService.addTagToNote(note1.orElseThrow(), t));
        assertThat(tagService.getNoteTags(note1.orElseThrow())).hasSize(tagsNum);

        Optional<TagDto> tag1 = tagService.createTag("test_tag_100");
        Optional<TagDto> tag2 = tagService.createTag("test_tag_200");

        noteService.addTagToNote(note2.orElseThrow(), tag1.orElseThrow());
        noteService.addTagToNote(note2.orElseThrow(), tag2.orElseThrow());
        assertThat(tagService.getNoteTags(note2.orElseThrow())).hasSize(2);

        assertThat(tagService.getAllTags()).hasSize(tagsNum + 2);

        assertThat(tagService.getNoteTags(note1.orElseThrow()))
                .as("check note1")
                .hasSize(tagsNum)
                .containsExactlyInAnyOrderElementsOf(tags);
        assertThat(tagService.getNoteTags(note2.orElseThrow()))
                .as("check note2")
                .hasSize(2)
                .containsExactlyInAnyOrderElementsOf(List.of(tag1.orElseThrow(), tag2.orElseThrow()));
        assertThat(tagService.getNoteTags(note3.orElseThrow()))
                .as("check note3")
                .hasSize(0);
    }

    @Test
    @Order(3)
    @DisplayName("Test get count of note tags")
    void testGetNoteTagsCount() {
        int tagsNum = 50;
        String prefix = "test_tag_";
        IntStream.rangeClosed(1, tagsNum).forEach(i -> tagService.createTag(prefix + i));
        List<TagDto> tags = tagService.getAllTags();
        assertThat(tags).as("tags list size is " + tagsNum).hasSize(tagsNum);

        Optional<NoteDto> note1 = noteService.createNote("test note 1");
        Optional<NoteDto> note2 = noteService.createNote("test note 2");
        Optional<NoteDto> note3 = noteService.createNote("test note 3");

        tags.forEach(t -> noteService.addTagToNote(note1.orElseThrow(), t));
        assertThat(tagService.getNoteTags(note1.orElseThrow())).hasSize(tagsNum);

        Optional<TagDto> tag1 = tagService.createTag("test_tag_100");
        Optional<TagDto> tag2 = tagService.createTag("test_tag_200");

        noteService.addTagToNote(note2.orElseThrow(), tag1.orElseThrow());
        noteService.addTagToNote(note2.orElseThrow(), tag2.orElseThrow());
        assertThat(tagService.getNoteTags(note2.orElseThrow())).hasSize(2);

        assertThat(tagService.getAllTags()).hasSize(tagsNum + 2);

        assertThat(tagService.getNoteTagsCount(note1.orElseThrow().id()))
                .as("check note1")
                .isEqualTo(tagsNum);
        assertThat(tagService.getNoteTagsCount(note2.orElseThrow().id()))
                .as("check note2")
                .isEqualTo(2);
        assertThat(tagService.getNoteTagsCount(note3.orElseThrow().id()))
                .as("check note3")
                .isEqualTo(0);
    }

    @Test
    @Order(4)
    @DisplayName("Test delete tag from note")
    void testDeleteTagFromNote() {
        int tagsNum = 50;
        String prefix = "test_tag_";
        IntStream.rangeClosed(1, tagsNum).forEach(i -> tagService.createTag(prefix + i));
        List<TagDto> tags = tagService.getAllTags();
        assertThat(tags).as("tags list size is " + tagsNum).hasSize(tagsNum);

        List<TagDto> tagsToDelete = tags.stream().filter(t ->
            t.name().equals(prefix + "1")
                    || t.name().equals(prefix + "10")
                    || t.name().equals(prefix + "30")
                    || t.name().equals(prefix + "50")
        ).toList();

        Optional<NoteDto> note1 = noteService.createNote("test note 1");
        Optional<NoteDto> note2 = noteService.createNote("test note 2");
        Optional<NoteDto> note3 = noteService.createNote("test note 3");
        Optional<TagDto> tag1 = tagService.createTag("test_tag_100");
        Optional<TagDto> tag2 = tagService.createTag("test_tag_200");

        tags.forEach(t -> noteService.addTagToNote(note1.orElseThrow(), t));
        assertThat(tagService.getNoteTags(note1.orElseThrow())).hasSize(tagsNum);

        noteService.addTagToNote(note2.orElseThrow(), tag1.orElseThrow());
        noteService.addTagToNote(note2.orElseThrow(), tag2.orElseThrow());
        assertThat(tagService.getNoteTags(note2.orElseThrow())).hasSize(2);

        assertThat(tagService.getNoteTags(note3.orElseThrow())).hasSize(0);

        tagsToDelete.forEach(t -> assertThat(noteService.deleteTagFromNote(note1.orElseThrow(), t)).isPresent());

        assertThat(noteService.getNoteById(note1.orElseThrow().id()))
                .as("check note1")
                .isPresent().get()
                .satisfies(n ->
                        assertThat(tagService.getNoteTags(n))
                                .as("check note1 tags")
                                .hasSize(tagsNum - tagsToDelete.size())
                                .doesNotContainAnyElementsOf(tagsToDelete)
                                .filteredOn(t ->
                                        t.name().equals(prefix + "2")
                                                || t.name().equals(prefix + "11")
                                                || t.name().equals(prefix + "31")
                                                || t.name().equals(prefix + "49")
                                ).hasSize(4)
                );

        assertThat(noteService.deleteTagFromNote(note2.orElseThrow(), tag1.orElseThrow())).isPresent();
        assertThat(noteService.deleteTagFromNote(note2.orElseThrow(), tag1.orElseThrow())).isEmpty();
        assertThat(noteService.deleteTagFromNote(note2.orElseThrow(), tag2.orElseThrow())).isPresent();
        assertThat(noteService.deleteTagFromNote(note2.orElseThrow(), tag2.orElseThrow())).isEmpty();

        assertThat(noteService.getNoteById(note2.orElseThrow().id()))
                .as("check note2")
                .isPresent().get()
                .satisfies(n ->
                        assertThat(tagService.getNoteTags(n))
                                .as("check note2 tags")
                                .hasSize(0)
                );

        assertThat(tagService.getNoteTags(note3.orElseThrow())).hasSize(0);

        noteService.addTagToNote(note1.orElseThrow(), tag2.orElseThrow());
        assertThat(tagService.getNoteTags(note1.orElseThrow())).hasSize(tagsNum - tagsToDelete.size() + 1);

        noteService.addTagToNote(note2.orElseThrow(), tag1.orElseThrow());
        noteService.addTagToNote(note2.orElseThrow(), tag2.orElseThrow());
        assertThat(tagService.getNoteTags(note2.orElseThrow())).hasSize(2);

        noteService.addTagToNote(note3.orElseThrow(), tag2.orElseThrow());
        assertThat(tagService.getNoteTags(note3.orElseThrow())).hasSize(1);

        assertThat(noteService.deleteTagFromNote(note2.orElseThrow(), tag2.orElseThrow())).isPresent();

        assertThat(tagService.getNoteTags(note1.orElseThrow())).hasSize(tagsNum - tagsToDelete.size() + 1);
        assertThat(tagService.getNoteTags(note2.orElseThrow())).hasSize(1);
        assertThat(tagService.getNoteTags(note3.orElseThrow())).hasSize(1);

        assertThat(tagService.getAllTags()).hasSize(tagsNum + 2);
    }

    @Test
    @Order(5)
    @DisplayName("Test get count of notes attached to a tag")
    void testGetTagNotesCount() {
        Optional<NoteDto> note1 = noteService.createNote("test note 1");
        Optional<NoteDto> note2 = noteService.createNote("test note 2");
        Optional<NoteDto> note3 = noteService.createNote("test note 3");

        Optional<TagDto> tag1 = tagService.createTag("test_tag_100");
        Optional<TagDto> tag2 = tagService.createTag("test_tag_200");
        Optional<TagDto> tag3 = tagService.createTag("test_tag_300");
        Optional<TagDto> tag4 = tagService.createTag("test_tag_400");

        noteService.addTagToNote(note1.orElseThrow(), tag2.orElseThrow());
        noteService.addTagToNote(note2.orElseThrow(), tag1.orElseThrow());
        noteService.addTagToNote(note2.orElseThrow(), tag2.orElseThrow());
        noteService.addTagToNote(note2.orElseThrow(), tag3.orElseThrow());
        noteService.addTagToNote(note3.orElseThrow(), tag2.orElseThrow());
        noteService.addTagToNote(note3.orElseThrow(), tag3.orElseThrow());

        assertThat(noteService.getTagNotesCount(tag1.orElseThrow().id()))
                .as("check tag1")
                .isEqualTo(1);
        assertThat(noteService.getTagNotesCount(tag2.orElseThrow().id()))
                .as("check tag2")
                .isEqualTo(3);
        assertThat(noteService.getTagNotesCount(tag3.orElseThrow().id()))
                .as("check tag3")
                .isEqualTo(2);
        assertThat(noteService.getTagNotesCount(tag4.orElseThrow().id()))
                .as("check tag4")
                .isEqualTo(0);
    }

    @Test
    @Order(6)
    @DisplayName("Test get notes attached to a tag")
    void testGetTagNotes() {
        Optional<NoteDto> note1 = noteService.createNote("test note 1");
        Optional<NoteDto> note2 = noteService.createNote("test note 2");
        Optional<NoteDto> note3 = noteService.createNote("test note 3");

        Optional<TagDto> tag1 = tagService.createTag("test_tag_100");
        Optional<TagDto> tag2 = tagService.createTag("test_tag_200");
        Optional<TagDto> tag3 = tagService.createTag("test_tag_300");
        Optional<TagDto> tag4 = tagService.createTag("test_tag_400");

        noteService.addTagToNote(note1.orElseThrow(), tag2.orElseThrow());
        noteService.addTagToNote(note2.orElseThrow(), tag1.orElseThrow());
        noteService.addTagToNote(note2.orElseThrow(), tag2.orElseThrow());
        noteService.addTagToNote(note2.orElseThrow(), tag3.orElseThrow());
        noteService.addTagToNote(note3.orElseThrow(), tag2.orElseThrow());
        noteService.addTagToNote(note3.orElseThrow(), tag3.orElseThrow());

        assertThat(noteService.getTagNotes(tag1.orElseThrow()))
                .as("check tag1")
                .hasSize(1)
                .containsExactly(note2.orElseThrow());
        assertThat(noteService.getTagNotes(tag2.orElseThrow()))
                .as("check tag2")
                .hasSize(3)
                .containsExactlyInAnyOrder(note1.orElseThrow(), note2.orElseThrow(), note3.orElseThrow());
        assertThat(noteService.getTagNotes(tag3.orElseThrow()))
                .as("check tag3")
                .hasSize(2)
                .containsExactlyInAnyOrder(note2.orElseThrow(), note3.orElseThrow());
        assertThat(noteService.getTagNotes(tag4.orElseThrow()))
                .as("check tag4")
                .hasSize(0);
    }

}
