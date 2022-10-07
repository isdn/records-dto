package dev.isdn.demo.records_dto.app.controller;

import dev.isdn.demo.records_dto.app.controller.exceptions.NoSuchItemException;
import dev.isdn.demo.records_dto.app.controller.exceptions.NotCreatedException;
import dev.isdn.demo.records_dto.app.controller.exceptions.NotUpdatedException;
import dev.isdn.demo.records_dto.app.controller.exceptions.ResultErrorException;
import dev.isdn.demo.records_dto.app.domain.common.Result;
import dev.isdn.demo.records_dto.app.domain.note.NoteContent;
import dev.isdn.demo.records_dto.app.domain.note.NoteDto;
import dev.isdn.demo.records_dto.app.domain.note.NoteService;
import dev.isdn.demo.records_dto.app.domain.tag.TagDto;
import dev.isdn.demo.records_dto.app.domain.tag.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class NoteController {

    private final static String PREFIX = "/";
    private final static String VERSION = "v1";
    private final NoteService noteService;
    private final TagService tagService;

    public NoteController(NoteService noteService, TagService tagService) {
        this.noteService = noteService;
        this.tagService = tagService;
    }

    @GetMapping(PREFIX + VERSION + "/notes")
    List<NoteDto> getAllNotes() {
        return noteService.getAllNotes();
    }

    @GetMapping(PREFIX + VERSION + "/notes/{id}")
    NoteDto getNote(@PathVariable long id) {
        return noteService.getNoteById(id).orElseThrow(() -> new NoSuchItemException("note " + id));
    }

    @GetMapping(PREFIX + VERSION + "/notes/{id}/tags")
    List<TagDto> getNoteTags(@PathVariable long id) {
        NoteDto note = noteService.getNoteById(id).orElseThrow(() -> new NoSuchItemException("note " + id));
        return tagService.getNoteTags(note);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = PREFIX + VERSION + "/notes")
    NoteDto createNote(@RequestBody NoteContent content) {
        return noteService.createNote(content).orElseThrow(NotCreatedException::new);
    }

    @PutMapping(PREFIX + VERSION + "/notes/{id}")
    NoteDto updateNoteContent(@PathVariable long id, @RequestBody NoteContent content) {
        NoteDto note = noteService.getNoteById(id).orElseThrow(() -> new NoSuchItemException("note " + id));
        return noteService.updateNoteContent(note, content).orElseThrow(() -> new NotUpdatedException("note " + id));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(PREFIX + VERSION + "/notes/{id}")
    void deleteNote(@PathVariable long id) {
        Result result = noteService.deleteNoteById(id);
        switch (result) {
            case Result.Ok ignored -> {}
            case Result.Error res -> throw new ResultErrorException();
            case Result.NoSuchElement res -> throw new NoSuchItemException("note " + res.element());
        }
    }

    @PutMapping(PREFIX + VERSION + "/notes/{id}/tags/{tagId}")
    NoteDto addTagToNote(@PathVariable long id, @PathVariable long tagId) {
        NoteDto note = noteService.getNoteById(id).orElseThrow(() -> new NoSuchItemException("note " + id));
        TagDto tag = tagService.getTagById(tagId).orElseThrow(() -> new NoSuchItemException("tag " + tagId));
        return noteService.addTagToNote(note, tag).orElseThrow(() -> new NotUpdatedException("note " + id));
    }

    @DeleteMapping(PREFIX + VERSION + "/notes/{id}/tags/{tagId}")
    NoteDto deleteTagFromNote(@PathVariable long id, @PathVariable long tagId) {
        NoteDto note = noteService.getNoteById(id).orElseThrow(() -> new NoSuchItemException("note " + id));
        TagDto tag = tagService.getTagById(tagId).orElseThrow(() -> new NoSuchItemException("tag " + tagId));
        return noteService.deleteTagFromNote(note, tag).orElseThrow(() -> new NotUpdatedException("note " + id));
    }

}
