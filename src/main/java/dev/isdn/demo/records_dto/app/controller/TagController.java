package dev.isdn.demo.records_dto.app.controller;

import dev.isdn.demo.records_dto.app.controller.exceptions.NoSuchItemException;
import dev.isdn.demo.records_dto.app.controller.exceptions.NotCreatedException;
import dev.isdn.demo.records_dto.app.controller.exceptions.NotUpdatedException;
import dev.isdn.demo.records_dto.app.controller.exceptions.ResultErrorException;
import dev.isdn.demo.records_dto.app.domain.common.Result;
import dev.isdn.demo.records_dto.app.domain.note.NoteDto;
import dev.isdn.demo.records_dto.app.domain.note.NoteService;
import dev.isdn.demo.records_dto.app.domain.tag.TagContent;
import dev.isdn.demo.records_dto.app.domain.tag.TagDto;
import dev.isdn.demo.records_dto.app.domain.tag.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class TagController {

    private final static String PREFIX = "/";
    private final static String VERSION = "v1";
    private final NoteService noteService;
    private final TagService tagService;

    public TagController(NoteService noteService, TagService tagService) {
        this.noteService = noteService;
        this.tagService = tagService;
    }

    @GetMapping(PREFIX + VERSION + "/tags")
    List<TagDto> getAllTags() {
        return tagService.getAllTags();
    }

    @GetMapping(PREFIX + VERSION + "/tags/{id}")
    TagDto getTag(@PathVariable long id) {
        return tagService.getTagById(id).orElseThrow(() -> new NoSuchItemException("tag " + id));
    }

    @GetMapping(PREFIX + VERSION + "/tags/{id}/notes")
    List<NoteDto> getTagNotes(@PathVariable long id) {
        TagDto tag = tagService.getTagById(id).orElseThrow(() -> new NoSuchItemException("tag " + id));
        return noteService.getTagNotes(tag);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(PREFIX + VERSION + "/tags")
    TagDto createTag(@RequestBody TagContent content) {
        return tagService.createTag(content).orElseThrow(NotCreatedException::new);
    }

    @PutMapping(PREFIX + VERSION + "/tags/{id}")
    TagDto updateTagContent(@PathVariable long id, @RequestBody TagContent content) {
        TagDto tag = tagService.getTagById(id).orElseThrow(() -> new NoSuchItemException("tag " + id));
        return tagService.updateTagContent(tag, content).orElseThrow(() -> new NotUpdatedException("tag " + id));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(PREFIX + VERSION + "/tags/{id}")
    void deleteTag(@PathVariable long id) {
        Result result = tagService.deleteTagById(id);
        switch (result) {
            case Result.Ok ignored -> {}
            case Result.Error res -> throw new ResultErrorException();
            case Result.NoSuchElement res -> throw new NoSuchItemException("tag " + res.element());
        }
    }

}
