package dev.isdn.demo.records_dto;

import dev.isdn.demo.records_dto.app.domain.tag.Tag;
import dev.isdn.demo.records_dto.app.domain.tag.TagDto;
import dev.isdn.demo.records_dto.app.domain.tag.TagRepository;
import dev.isdn.demo.records_dto.app.domain.tag.TagService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(
        properties = {"spring.config.name=test-config"
                // ,"spring.jpa.show-sql=true"
                // ,"spring.jpa.properties.hibernate.format_sql=true"
        },
        classes = {App.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TagTest {

    @Autowired
    TagService tagService;

    @Autowired
    TagRepository tagRepository;

    @AfterEach
    void tearDown() {
        tagRepository.deleteAllInBatch();
    }

    @Test
    @Order(1)
    @DisplayName("Test tags equality")
    void testTagEquals() {
        Optional<TagDto> tagDto = tagService.createTag("test_tag");
        assertThat(tagDto).as("tag creation")
                .isPresent().get()
                .satisfies(t -> assertThat(t.id()).isGreaterThan(0));
        long tagId = tagDto.orElseThrow().id();

        Tag tag1 = tagRepository.getTagById(tagId).orElseThrow();
        Tag tag2 = tagRepository.getTagById(tagId).orElseThrow();

        assertThat(tag1).as("tag1 equals tag2").isEqualTo(tag2);
        assertThat(tag2).as("tag2 equals tag1").isEqualTo(tag1);
    }

    @Test
    @Order(2)
    @DisplayName("Test tags equals contract")
    void testTagEqualsContract() {
        Optional<TagDto> tagDto1 = tagService.createTag("test_tag1");
        Optional<TagDto> tagDto2 = tagService.createTag("test_tag2");
        Optional<TagDto> tagDto3 = tagService.createTag("test_tag3");
        long tagId1 = tagDto1.orElseThrow().id();
        long tagId2 = tagDto2.orElseThrow().id();
        long tagId3 = tagDto3.orElseThrow().id();

        Tag tag11 = tagRepository.getTagById(tagId1).orElseThrow();
        Tag tag12 = tagRepository.getTagById(tagId1).orElseThrow();
        Tag tag13 = tagRepository.getTagById(tagId1).orElseThrow();
        Tag tag21 = tagRepository.getTagById(tagId2).orElseThrow();
        Tag tag22 = tagRepository.getTagById(tagId2).orElseThrow();
        Tag tag31 = tagRepository.getTagById(tagId3).orElseThrow();

        assertThat(tag11).as("Non-nullity").isNotEqualTo(null);
        assertThat(tag21).as("Non-nullity").isNotEqualTo(null);

        assertThat(tag11).as("Reflexive").isEqualTo(tag11);
        assertThat(tag21).as("Reflexive").isEqualTo(tag21);

        assertThat(tag11).as("Symmetric").isEqualTo(tag12).isNotEqualTo(tag21);
        assertThat(tag12).as("Symmetric").isEqualTo(tag11).isNotEqualTo(tag22);
        assertThat(tag21).as("Symmetric").isNotEqualTo(tag11).isEqualTo(tag22);
        assertThat(tag22).as("Symmetric").isNotEqualTo(tag12).isEqualTo(tag21);

        assertThat(tag11).as("Transitive").isEqualTo(tag12);
        assertThat(tag12).as("Transitive").isEqualTo(tag13);
        assertThat(tag11).as("Transitive").isEqualTo(tag13);

        assertThat(tag11).as("Transitive").isNotEqualTo(tag21);
        assertThat(tag21).as("Transitive").isNotEqualTo(tag31);
        assertThat(tag11).as("Transitive").isNotEqualTo(tag31);

        IntStream.range(0, 2000).forEach(i -> {
            assertThat(tag11).as("Consistency").isEqualTo(tag12);
            assertThat(tag21).as("Consistency").isNotEqualTo(tag31);
        });
    }

    @Test
    @Order(3)
    @DisplayName("Test tags equality in a set")
    void testTagEqualsInSet() {
        Optional<TagDto> tagDto1 = tagService.createTag("test_tag1");
        Optional<TagDto> tagDto2 = tagService.createTag("test_tag2");
        Optional<TagDto> tagDto3 = tagService.createTag("test_tag3");
        long tagId1 = tagDto1.orElseThrow().id();
        long tagId2 = tagDto2.orElseThrow().id();
        long tagId3 = tagDto3.orElseThrow().id();

        Tag tag1 = tagRepository.getTagById(tagId1).orElseThrow();
        Tag tag2 = tagRepository.getTagById(tagId2).orElseThrow();
        Tag tag21 = tagRepository.getTagById(tagId2).orElseThrow();

        Set<Tag> tags = new HashSet<>();
        tags.add(tag1);
        tags.add(tag2);
        tags.add(tag21);

        Tag tagRef1 = tagRepository.getTagById(tagId1).orElseThrow();
        Tag tagRef2 = tagRepository.getTagById(tagId2).orElseThrow();
        Tag tagRef3 = tagRepository.getTagById(tagId3).orElseThrow();

        assertThat(tags)
                .as("set size is 2").hasSize(2)
                .as("set contains only tag1 and tag2").containsExactlyInAnyOrder(tagRef1, tagRef2)
                .as("does not contain tag3").doesNotContain(tagRef3);
    }

}
