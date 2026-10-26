package com.dnaita.agenticailab.contracts;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class TaskPlanTest {

    private static final ValidatorFactory VALIDATOR_FACTORY = Validation.buildDefaultValidatorFactory();
    private static final Validator VALIDATOR = VALIDATOR_FACTORY.getValidator();

    @AfterAll
    static void closeValidatorFactory() {
        VALIDATOR_FACTORY.close();
    }

    @Test
    void acceptsValidPlanAndTrimsText() {
        TaskPlan plan = new TaskPlan(
                "  Add API retry handling  ",
                List.of(" Define retryable errors ", "Implement bounded retries"),
                null);

        assertEquals("Add API retry handling", plan.title());
        assertEquals("Define retryable errors", plan.steps().getFirst());
        assertEquals(List.of(), plan.risks());
        assertTrue(VALIDATOR.validate(plan).isEmpty());
    }

    @Test
    void rejectsTitleShorterThanThreeCharacters() {
        TaskPlan plan = new TaskPlan("  ", List.of("Run tests"), List.of());

        assertHasViolations(plan);
    }

    @Test
    void rejectsEmptySteps() {
        TaskPlan plan = new TaskPlan("Implement retries", List.of(), List.of());

        assertHasViolations(plan);
    }

    @Test
    void rejectsBlankStep() {
        TaskPlan plan = new TaskPlan("Implement retries", List.of("   "), List.of());

        assertHasViolations(plan);
    }

    @Test
    void rejectsMoreThanEightSteps() {
        List<String> steps = IntStream.rangeClosed(1, 9)
                .mapToObj(number -> "Step " + number)
                .toList();
        TaskPlan plan = new TaskPlan("Implement retries", steps, List.of());

        assertHasViolations(plan);
    }

    @Test
    void rejectsMoreThanEightRisks() {
        List<String> risks = IntStream.rangeClosed(1, 9)
                .mapToObj(number -> "Risk " + number)
                .toList();
        TaskPlan plan = new TaskPlan("Implement retries", List.of("Run tests"), risks);

        assertHasViolations(plan);
    }

    @Test
    void rejectsBlankRisk() {
        TaskPlan plan = new TaskPlan("Implement retries", List.of("Run tests"), List.of(" "));

        assertHasViolations(plan);
    }

    @Test
    void rejectsTwoCharacterTitle() {
        TaskPlan plan = new TaskPlan(
                "Go",
                List.of("Run tests"),
                List.of());

        assertHasViolations(plan);
    }

     @Test
    void acceptsTitleAtMaximumLength() {
        String title = "A".repeat(120);
        TaskPlan plan = new TaskPlan(
                title,
                List.of("Run tests"),
                List.of());

        assertTrue(VALIDATOR.validate(plan).isEmpty());
    }

    private static void assertHasViolations(TaskPlan plan) {
        Set<ConstraintViolation<TaskPlan>> violations = VALIDATOR.validate(plan);
        assertFalse(violations.isEmpty(), "Expected validation to reject the plan");
    }
}
