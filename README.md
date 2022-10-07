##### Java records as DTOs

**This is not production-ready code.**

This project demonstrates one more way how to use records as DTOs (data transfer objects).
It uses an in-memory H2 database, so no additional services are required.

Entities:
- `Tag`
- `Note`

DTOs:
- `TagDto`
- `NoteDto`

Content objects:
- `TagContent`
- `NoteContent`

Models are used only within the service layer.  
The service layer consumes/produces DTOs and content objects.  
The content objects are purposed to transfer an object's content (like tag color and name).  
DTOs and content objects are immutable Java records.

To demonstrate the process, I created very simple REST controllers (`NoteController` and `TagController`).
For example:
```java
    @PutMapping(PREFIX + VERSION + "/tags/{id}")
    TagDto updateTagContent(@PathVariable long id, @RequestBody TagContent content) {
        TagDto tag = tagService.getTagById(id).orElseThrow(() -> new NoSuchItemException("tag " + id));
        return tagService.updateTagContent(tag, content).orElseThrow(() -> new NotUpdatedException("tag " + id));
    }
```

Here is the service method:
```java
    @Transactional
    public Optional<TagDto> updateTagContent(TagDto tag, TagContent content) {
        return Functions.checkTagDto.apply(tag)
                .flatMap(t -> repository.getTagById(t.id()))
                .flatMap(t -> setTagNameAndColor(t, content.name(), content.color()))
                .map(repository::saveAndFlush)
                .flatMap(t -> repository.findById(t.getId()));
    }
```
A DTO is received, checked, and "transformed" into a model. Then, the model properties are updated.
After that, an updated DTO is fetched and returned.

Here is the corresponding repository method:
```java
    @QueryHints(value = {
            @QueryHint(name = READ_ONLY, value = "true")
    })
    @Transactional(readOnly = true)
    @Query(value = "SELECT new dev.isdn.demo.records_dto.app.domain.tag.TagDto(t.id, t.name, t.color) FROM tags t WHERE t.id = :id")
    Optional<TagDto> findById(@Param("id") long id);
```

So, there is no need for special "mapper" functions to convert DTOs/models.


##### Build and run

```bash
mvn clean test
```

```bash
mvn clean package -DskipTests
java --enable-preview -jar target/records-dto-demo-0.1.jar
```
or
```bash
mvn clean spring-boot:run
```

##### Operations

List of notes:
```bash
curl -i http://127.0.0.1:8080/v1/notes
```

Create notes:
```bash
curl -i -H 'Content-type:application/json' -d '{"content":"test"}' -X POST http://127.0.0.1:8080/v1/notes
curl -i -H 'Content-type:application/json' -d '{"content":"another test"}' -X POST http://127.0.0.1:8080/v1/notes
curl -i -H 'Content-type:application/json' -d '{"content":""}' -X POST http://127.0.0.1:8080/v1/notes
```

Delete a note:
```bash
curl -i -X DELETE http://127.0.0.1:8080/v1/notes/766714176451034115
```

Get a note:
```bash
curl -i http://127.0.0.1:8080/v1/notes/2119985630772393670
```

Update a note:
```bash
curl -i -H 'Content-type:application/json' -d '{"content":"test updated"}' -X PUT http://127.0.0.1:8080/v1/notes/8465407150649195493
```

Get note tags:
```bash
curl -i http://127.0.0.1:8080/v1/notes/8465407150649195493/tags
```

List of tags:
```bash
curl -i http://127.0.0.1:8080/v1/tags
```

Create tags:
```bash
curl -i -H 'Content-type:application/json' -d '{"name":"test1", "color":"ffffff"}' -X POST http://127.0.0.1:8080/v1/tags
curl -i -H 'Content-type:application/json' -d '{"name":"test2"}' -X POST http://127.0.0.1:8080/v1/tags
curl -i -H 'Content-type:application/json' -d '{"name":"test3"}' -X POST http://127.0.0.1:8080/v1/tags
```

Delete a tag:
```bash
curl -i -X DELETE http://127.0.0.1:8080/v1/tags/614426635004842295
```

Get a tag:
```bash
curl -i http://127.0.0.1:8080/v1/tags/4760561560720237306
```

Update a tag:
```bash
curl -i -H 'Content-type:application/json' -d '{"name":"test3_updated", "color": "AAaaAA"}' -X PUT http://127.0.0.1:8080/v1/tags/4760561560720237306
```

Get tag notes:
```bash
curl -i http://127.0.0.1:8080/v1/tags/4760561560720237306/notes
```

Add tags to a note:
```bash
curl -i -X PUT http://127.0.0.1:8080/v1/notes/8465407150649195493/tags/4760561560720237306
curl -i -X PUT http://127.0.0.1:8080/v1/notes/8465407150649195493/tags/362170154209224171
```

Delete a tag from a note:
```bash
curl -i -X DELETE http://127.0.0.1:8080/v1/notes/8465407150649195493/tags/362170154209224171
```

