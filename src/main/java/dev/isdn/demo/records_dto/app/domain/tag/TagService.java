package dev.isdn.demo.records_dto.app.domain.tag;

import dev.isdn.demo.records_dto.app.domain.common.Functions;
import dev.isdn.demo.records_dto.app.domain.common.Result;
import dev.isdn.demo.records_dto.app.domain.note.NoteDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.function.Predicate;

@Service
public class TagService {

    final private TagRepository repository;

    public TagService(TagRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Optional<TagDto> createTag(String name) {
        return setTagName(new Tag(), name)
                .map(repository::saveAndFlush)
                .flatMap(tag -> repository.findById(tag.getId()));
    }

    @Transactional
    public Optional<TagDto> createTag(String name, String color) {
        return setTagName(new Tag(), name)
                .flatMap(tag -> setTagColor(tag, color))
                .map(repository::saveAndFlush)
                .flatMap(tag -> repository.findById(tag.getId()));
    }

    @Transactional
    public Optional<TagDto> createTag(TagContent content) {
        return content.color() != null ? createTag(content.name(), content.color()) : createTag(content.name());
    }

    @Transactional
    public Optional<TagDto> updateTagName(TagDto tag, String name) {
        return Functions.checkTagDto.apply(tag)
                .flatMap(t -> repository.getTagById(t.id()))
                .flatMap(t -> setTagName(t, name))
                .map(repository::saveAndFlush)
                .flatMap(t -> repository.findById(t.getId()));
    }

    @Transactional
    public Optional<TagDto> updateTagColor(TagDto tag, String color) {
        return Functions.checkTagDto.apply(tag)
                .flatMap(t -> repository.getTagById(t.id()))
                .flatMap(t -> setTagColor(t, color))
                .map(repository::saveAndFlush)
                .flatMap(t -> repository.findById(t.getId()));
    }

    @Transactional
    public Optional<TagDto> updateTagContent(TagDto tag, TagContent content) {
        return Functions.checkTagDto.apply(tag)
                .flatMap(t -> repository.getTagById(t.id()))
                .flatMap(t -> setTagNameAndColor(t, content.name(), content.color()))
                .map(repository::saveAndFlush)
                .flatMap(t -> repository.findById(t.getId()));
    }

    public Optional<TagDto> getTagById(long tagId) {
        return repository.findById(tagId);
    }

    @Transactional(readOnly = true)
    public List<TagDto> getNoteTags(NoteDto note) {
        return Functions.checkNoteDto.apply(note)
                .map(n -> repository.findAllByNoteId(n.id()).toList())
                .orElse(Collections.emptyList());
    }

    @Transactional(readOnly = true)
    public long getNoteTagsCount(long noteId) {
        return repository.countAllByNoteId(noteId);
    }

    @Transactional(readOnly = true)
    public List<TagDto> getAllTags() {
        return repository.fetchAllTags().toList();
    }

    @Transactional
    public Result deleteTag(TagDto tag) {
        return Functions.checkTagDto.apply(tag)
                .map(t -> deleteTagById(t.id()))
                .orElse(new Result.Error());
    }

    @Transactional
    public Result deleteTagById(long tagId) {
        if (repository.existsById(tagId)) {
            repository.deleteById(tagId);
            repository.flush();
            return new Result.Ok();
        }
        return new Result.NoSuchElement(tagId);
    }

    private Optional<Tag> setTagName(Tag tag, String name) {
        return Optional.ofNullable(tag)
                .flatMap(t ->
                        Functions.checkTagName.apply(name)
                                .filter(Predicate.not(repository::existsByName))
                                .map(t::setName)
                );
    }

    private Optional<Tag> setTagColor(Tag tag, String color) {
        return Optional.ofNullable(tag)
                .flatMap(t ->
                        Functions.checkTagColor.apply(color)
                                .map(t::setColor)
                );
    }

    private Optional<Tag> setTagNameAndColor(Tag tag, String name, String color) {
        return Optional.ofNullable(tag)
                .flatMap(t ->
                        Functions.checkTagName.apply(name).isPresent() ?
                            Functions.checkTagColor.apply(color).map(c -> t.setName(name).setColor(c)) :
                            Optional.empty()
                );
    }

}
