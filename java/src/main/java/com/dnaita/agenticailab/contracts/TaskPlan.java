package com.dnaita.agenticailab.contracts;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * A validated plan for completing a software engineering task.
 *
 * <p>Jakarta Bean Validation annotations define constraints. A caller must
 * invoke a Validator (or Spring validation integration) to enforce them.</p>
 */
public record TaskPlan(
        @NotBlank
        @Size(min = 3, max = 120)
        String title,

        @NotEmpty
        @Size(max = 8)
        List<@NotBlank String> steps,

        @Size(max = 8)
        List<@NotBlank String> risks
) {
    public TaskPlan {
        title = title == null ? null : title.trim();
        steps = steps == null
                ? null
                : steps.stream()
                        .map(step -> step == null ? "" : step.trim())
                        .toList();
        risks = risks == null
                ? List.of()
                : risks.stream()
                        .map(risk -> risk == null ? "" : risk.trim())
                        .toList();
    }
}
