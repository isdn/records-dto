package dev.isdn.demo.records_dto.app.domain.note;

import dev.isdn.demo.records_dto.app.domain.common.Functions;
import dev.isdn.demo.records_dto.app.domain.common.Result;
import dev.isdn.demo.records_dto.app.domain.tag.TagDto;
import dev.isdn.demo.records_dto.app.domain.tag.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class NoteService {

    final private NoteRepository repository;

    final private TagRepository tagRepository;

    public NoteService(NoteRepository repository, TagRepository tagRepository) {
        this.repository = repository;
        this.tagRepository = tagRepository;
    }

    @Transactional
    public Optional<NoteDto> createNote(String content) {
        Note note = new Note();
        return Optional.ofNullable(content)
                .map(note::setContent)
                .map(repository::saveAndFlush)
                .flatMap(n -> repository.findById(n.getId()));
    }

    @Transactional
    public Optional<NoteDto> createNote(NoteContent content) {
        return createNote(content.content());
    }

    @Transactional
    public Optional<NoteDto> updateNoteContent(NoteDto note, String content) {
        return Functions.checkNoteDto.apply(note)
                .flatMap(n -> repository.getNoteById(n.id()))
                .flatMap(n ->
                        Optional.ofNullable(content)
                                .map(n::setContent)
                                .map(repository::saveAndFlush)
                )
                .flatMap(n -> repository.findById(n.getId()));
    }

    @Transactional
    public Optional<NoteDto> updateNoteContent(NoteDto note, NoteContent content) {
        return updateNoteContent(note, content.content());
    }

    public Optional<NoteDto> getNoteById(long noteId) {
        return repository.findById(noteId);
    }

    @Transactional(readOnly = true)
    public List<NoteDto> getTagNotes(TagDto tag) {
        return Functions.checkTagDto.apply(tag)
                .map(t -> repository.findAllByTagId(t.id()).toList())
                .orElse(Collections.emptyList());
    }

    @Transactional(readOnly = true)
    public long getTagNotesCount(long tagId) {
        return repository.countAllByTagId(tagId);
    }

    @Transactional(readOnly = true)
    public List<NoteDto> getAllNotes() {
        return repository.fetchAllNotes().toList();
    }

    @Transactional
    public Result deleteNote(NoteDto note) {
        return Functions.checkNoteDto.apply(note)
                .map(n -> deleteNoteById(n.id()))
                .orElse(new Result.Error());
    }

    @Transactional
    public Result deleteNoteById(long noteId) {
        if (repository.existsById(noteId)) {
            repository.deleteById(noteId);
            repository.flush();
            return new Result.Ok();
        }
        return new Result.NoSuchElement(noteId);
    }

    @Transactional
    public Optional<NoteDto> addTagToNote(NoteDto note, TagDto tag) {
        return Functions.checkNoteDto.apply(note)
                .flatMap(n -> repository.getNoteById(n.id()))
                .flatMap(n ->
                        Functions.checkTagDto.apply(tag)
                                .flatMap(t -> tagRepository.getTagById(t.id()))
                                .filter(t -> ! n.getTags().contains(t))
                                .map(n::addTag)
                                .map(repository::saveAndFlush)
                )
                .flatMap(n -> repository.findById(n.getId()));
    }

    @Transactional
    public Optional<NoteDto> deleteTagFromNote(NoteDto note, TagDto tag) {
        return Functions.checkNoteDto.apply(note)
                .flatMap(n -> repository.getNoteById(n.id()))
                .flatMap(n ->
                        Functions.checkTagDto.apply(tag)
                                .flatMap(t -> tagRepository.getTagById(t.id()))
                                .filter(t -> n.getTags().contains(t))
                                .map(n::deleteTag)
                                .map(repository::saveAndFlush)
                )
                .flatMap(n -> repository.findById(n.getId()));
    }

}
