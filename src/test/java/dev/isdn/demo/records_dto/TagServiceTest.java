package dev.isdn.demo.records_dto;

import dev.isdn.demo.records_dto.app.domain.common.Constants;
import dev.isdn.demo.records_dto.app.domain.common.Result;
import dev.isdn.demo.records_dto.app.domain.tag.*;
import dev.isdn.demo.records_dto.app.domain.tag.Tag;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
public class TagServiceTest {

    @Autowired
    TagRepository tagRepository;

    @Autowired
    TagService tagService;

    @AfterEach
    void tearDown() {
        tagRepository.deleteAllInBatch();
    }

    @Test
    @Order(1)
    @DisplayName("Test tags creation")
    void testCreateTag() {
        String name1 = "test_tag1", name2 = "test_tag2";
        String color = "FFFFFF";
        Optional<TagDto> tagDto1 = tagService.createTag(name1);
        assertThat(tagDto1).as("tag creation with default color")
                .isPresent().get()
                .satisfies(t -> {
                            assertThat(t.id()).isGreaterThan(0);
                            assertThat(t.name()).isEqualTo(name1);
                            assertThat(t.color()).isEqualTo(Constants.DEFAULT_COLOR);
                        });
        Optional<TagDto> tagDto2 = tagService.createTag(name2, color);
        assertThat(tagDto2).as("tag creation with color")
                .isPresent().get()
                .satisfies(t -> {
                    assertThat(t.id()).isGreaterThan(0);
                    assertThat(t.name()).isEqualTo(name2);
                    assertThat(t.color()).isEqualTo(color);
                });

        long tagId = tagDto2.orElseThrow().id();
        Tag tag = tagRepository.getTagById(tagId).orElseThrow();
        assertThat(tag).as("created tag verification")
                .satisfies(t -> {
                    assertThat(t.getName()).isEqualTo(name2);
                    assertThat(t.getColor()).isEqualTo(color);
                });
    }

    @Test
    @Order(2)
    @DisplayName("Test tag names uniqueness")
    void testTagUnique() {
        String name = "test_tag";
        Optional<TagDto> tagDto = tagService.createTag(name);
        assertThat(tagDto)
                .isPresent().get()
                .satisfies(t -> assertThat(t.id()).isGreaterThan(0));
        Optional<TagDto> tagDtoDuplicate = tagService.createTag(name);
        assertThat(tagDtoDuplicate).as("tag with a duplicate name was not created").isEmpty();
    }

    @Test
    @Order(3)
    @DisplayName("Test tag name update")
    void testUpdateTagName() {
        String name = "test_tag", newName = "new_test_tag";
        String color = "FFFFFF";
        Optional<TagDto> tagDto, newTagDto;
        tagDto = tagService.createTag(name, color);
        assertThat(tagDto)
                .isPresent().get()
                .satisfies(t -> {
                    assertThat(t.id()).isGreaterThan(0);
                    assertThat(t.name()).isEqualTo(name);
                });
        long tagId = tagDto.orElseThrow().id();

        newTagDto = tagService.updateTagName(tagDto.orElseThrow(), newName);
        assertThat(newTagDto).as("new name was set")
                .isPresent().get()
                .satisfies(t -> {
                    assertThat(t.id()).isEqualTo(tagId);
                    assertThat(t.name()).isEqualTo(newName);
                    assertThat(t.color()).isEqualTo(color);
                });

        Tag tag = tagRepository.getTagById(tagId).orElseThrow();
        assertThat(tag).as("updated tag verification")
                .satisfies(t -> {
                    assertThat(t.getName()).isEqualTo(newName);
                    assertThat(t.getColor()).isEqualTo(color);
                });
    }

    @Test
    @Order(4)
    @DisplayName("Test tag color update")
    void testUpdateTagColor() {
        String name = "test_tag";
        String color = "FFFFFF", newColor = "AAAAAA";
        Optional<TagDto> tagDto = tagService.createTag(name, color);
        assertThat(tagDto)
                .isPresent().get()
                .satisfies(t -> {
                    assertThat(t.id()).isGreaterThan(0);
                    assertThat(t.color()).isEqualTo(color);
                });

        long tagId = tagDto.orElseThrow().id();

        Optional<TagDto> newTagDto = tagService.updateTagColor(tagDto.orElseThrow(), newColor);
        assertThat(newTagDto).as("new color was set")
                .isPresent().get()
                .satisfies(t -> {
                    assertThat(t.id()).isEqualTo(tagId);
                    assertThat(t.name()).isEqualTo(name);
                    assertThat(t.color()).isEqualTo(newColor);
                });

        Tag tag = tagRepository.getTagById(tagId).orElseThrow();
        assertThat(tag).as("updated tag verification")
                .satisfies(t -> {
                    assertThat(t.getName()).isEqualTo(name);
                    assertThat(t.getColor()).isEqualTo(newColor);
                });
    }

    @Test
    @Order(5)
    @DisplayName("Test tag content update")
    void testUpdateTagContent() {
        String name = "test_tag", newName = "test_tag_updated";
        String color = "FFFFFF", newColor = "AAAAAA";
        TagContent tagContent = new TagContent(newName, newColor);
        Optional<TagDto> tagDto = tagService.createTag(name, color);
        assertThat(tagDto)
                .isPresent().get()
                .satisfies(t -> {
                    assertThat(t.id()).isGreaterThan(0);
                    assertThat(t.color()).isEqualTo(color);
                    assertThat(t.name()).isEqualTo(name);
                });

        long tagId = tagDto.orElseThrow().id();

        Optional<TagDto> newTagDto = tagService.updateTagContent(tagDto.orElseThrow(), tagContent);
        assertThat(newTagDto).as("tag was updated")
                .isPresent().get()
                .satisfies(t -> {
                    assertThat(t.id()).isEqualTo(tagId);
                    assertThat(t.name()).isEqualTo(newName);
                    assertThat(t.color()).isEqualTo(newColor);
                });

        Tag tag = tagRepository.getTagById(tagId).orElseThrow();
        assertThat(tag).as("updated tag verification")
                .satisfies(t -> {
                    assertThat(t.getName()).isEqualTo(newName);
                    assertThat(t.getColor()).isEqualTo(newColor);
                });
    }

    @Test
    @Order(6)
    @DisplayName("Test set invalid tag name")
    void testSetNotValidTagName() {
        List<String> notValidNames = Arrays.asList(
                "",
                null,
                " ",
                "  ",
                "\t",
                "\n",
                "test\ntest",
                "LR1JOgpJw8HPLdUj6mWz5VzBfF2HRGZESl4kjzWgOwy8mosT9uJURXs0H8e4V7Lpx8PS8fn1gw6Lnss6IXJsxRNr40qS7ZFyoyEthggiBIpHX7DjG4S2UjRE5qTZQHwGp",
                "test#",
                "☢︎",
                "☢︎test",
                ";"
                );
        Optional<TagDto> tagDto = tagService.createTag("test_tag");

        notValidNames.forEach(n ->
                assertThat(tagService.updateTagName(tagDto.orElseThrow(), n))
                    .as("new name was not set")
                    .isEmpty()
        );
    }

    @Test
    @Order(7)
    @DisplayName("Test set invalid tag color")
    void testSetNotValidTagColor() {
        List<String> notValidColors = Arrays.asList(
                "",
                null,
                " ",
                "  ",
                "\t",
                "\n",
                "testtest",
                "test\n",
                "^111",
                "ff>eea",
                "aabb",
                "aabbc?",
                "11222",
                "$$",
                "☢︎",
                "--",
                ";"
        );
        Optional<TagDto> tagDto = tagService.createTag("test_tag");

        notValidColors.forEach(c ->
                assertThat(tagService.updateTagColor(tagDto.orElseThrow(), c))
                        .as("new color was not set")
                        .isEmpty()
        );
    }

    @Test
    @Order(8)
    @DisplayName("Test get tag by ID")
    void testGetTagById() {
        Optional<TagDto> tagDto = tagService.createTag("test_tag");
        long tagId = tagDto.orElseThrow().id();

        assertThat(tagService.getTagById(tagId)).as("tag should be the same")
                .isPresent().get()
                .isEqualTo(tagDto.orElseThrow());
    }

    @Test
    @Order(9)
    @DisplayName("Test get all tags")
    void testGetAllTags() {
        int tagsNum = 50;
        String prefix = "test_tag_";
        IntStream.rangeClosed(1, tagsNum).forEach(i -> tagService.createTag(prefix + i));

        List<TagDto> tags = tagService.getAllTags();
        assertThat(tags).as("tags list size is " + tagsNum)
                .hasSize(tagsNum)
                .as("tags list contains some specific elements")
                .filteredOn(t ->
                        t.name().equals(prefix + "1")
                                || t.name().equals(prefix + "20")
                                || t.name().equals(prefix + "50")
                ).hasSize(3);
    }

    @Test
    @Order(10)
    @DisplayName("Test delete tag by ID")
    void testDeleteTagById() {
        int tagsNum = 50;
        IntStream.range(0, tagsNum).forEach(i -> tagService.createTag("test_tag_" + i));
        TagDto tag = tagService.getAllTags().get(tagsNum - 1);

        long failId = tag.id() + 1;
        Result failedResult = tagService.deleteTagById(failId);
        if (failedResult instanceof Result.NoSuchElement actualResult) {
            assertThat(actualResult.element()).isEqualTo(failId);
        } else {
            fail("deleted element with ID " + failId);
        }

        Result result = tagService.deleteTagById(tag.id());
        assertThat(result).as("result should be Ok").isExactlyInstanceOf(Result.Ok.class);
        assertThat(tagService.getTagById(tag.id())).isEmpty();
        assertThat(tagService.getAllTags()).hasSize(tagsNum - 1);

        Result anotherResult = tagService.deleteTagById(tag.id());
        assertThat(anotherResult).as("this attempt should return NoSuchElement")
                .isExactlyInstanceOf(Result.NoSuchElement.class);
    }

    @Test
    @Order(11)
    @DisplayName("Test delete tag")
    void testDeleteTag() {
        int tagsNum = 50;
        IntStream.range(0, tagsNum).forEach(i -> tagService.createTag("test_tag_" + i));
        List<TagDto> tags = tagService.getAllTags();
        TagDto tag = tags.get(tagsNum - 1);

        Result result = tagService.deleteTag(tag);
        assertThat(result).as("result should be Ok").isExactlyInstanceOf(Result.Ok.class);
        assertThat(tagService.getTagById(tag.id())).isEmpty();

        Result anotherResult = tagService.deleteTag(tag);
        assertThat(anotherResult).as("result should be NoSuchElement")
                .isExactlyInstanceOf(Result.NoSuchElement.class)
                .extracting("element").isEqualTo(tag.id());

        Result nullResult = tagService.deleteTag(null);
        assertThat(nullResult).as("result should be Error").isExactlyInstanceOf(Result.Error.class);
    }

}
