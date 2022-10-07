package dev.isdn.demo.records_dto;

import dev.isdn.demo.records_dto.app.domain.common.SequenceGenerator;
import org.junit.jupiter.api.*;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SequenceGeneratorTest {

    private SequenceGenerator generator;

    @BeforeEach
    void setUp() {
        this.generator = new SequenceGenerator();
    }

    @Test
    @Order(1)
    @DisplayName("Test if numbers in a sequence are unique")
    void testUniqueNumbers(TestReporter testReporter, TestInfo testInfo) {
        int amount = 2_000_000;
        int amountOfSame = 8;
        Long[] numsGenerated = new Long[amount+amountOfSame];

        IntStream.range(0, amount).forEach(i -> numsGenerated[i] = generator.getNumber());
        Arrays.fill(numsGenerated, amount, amount+amountOfSame, generator.getNumber());

        Set<Long> numsChecked = new HashSet<>(Arrays.asList(numsGenerated));

        assertThat(numsChecked).hasSize(amount + 1);
        assertThat(numsGenerated).hasSize(numsChecked.size() + amountOfSame - 1);

        testReporter.publishEntry(testInfo.getDisplayName() + ": generated "
                + numsGenerated.length + " numbers, " + numsChecked.size() + " are unique");
    }

    @Test
    @Order(2)
    @DisplayName("Test if all numbers are positive")
    void testPositiveNumbers(TestReporter testReporter, TestInfo testInfo) {
        int amount = 10_000_000;
        Set<Long> results = new HashSet<>();

        IntStream.range(0, amount).forEach(i -> {
            long n = generator.getNumber();
            if (n > 0) results.add(n);
        });

        assertThat(results).hasSize(amount);

        testReporter.publishEntry(testInfo.getDisplayName() + ": generated "
                + amount + " positive numbers");
    }

    @Test
    @Order(3)
    @DisplayName("Test if sequences are unique in two instances")
    void testTwoInstances(TestReporter testReporter, TestInfo testInfo) {
        int amount = 50_000;
        SequenceGenerator generator1 = new SequenceGenerator();
        SequenceGenerator generator2 = new SequenceGenerator();

        Long[] nums1 = new Long[amount];
        Long[] nums2 = new Long[amount];

        IntStream.range(0, amount).forEach(i -> {
            nums1[i] = generator1.getNumber();
            nums2[i] = generator2.getNumber();
        });

        Set<Long> results = new HashSet<>(Arrays.asList(nums1));
        results.retainAll(List.of(nums2));

        assertThat(results).isEmpty();

        testReporter.publishEntry(testInfo.getDisplayName() + ": generated two sequences of "
                + amount + " unique numbers");
    }

    @Test
    @Order(4)
    @DisplayName("Estimate generation speed")
    void measureGenerationSpeed(TestReporter testReporter, TestInfo testInfo) {
        int amount = 50_000_000;

        long startTime = Instant.now().toEpochMilli();
        IntStream.range(0, amount).forEach($ -> generator.getNumber());
        long resultTime = Instant.now().toEpochMilli() - startTime;

        testReporter.publishEntry(testInfo.getDisplayName() + ": generated "
                + amount + " numbers, took: " + resultTime + " milliseconds. Approximately " + amount/resultTime + " numbers/ms.");
    }

}
