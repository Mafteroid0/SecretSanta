package ru.mafteroid.secretsanta.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record CreateEventRequest(
        @NotBlank
        @Size(max=120)
        String name,

        @NotNull
        LocalDateTime deadline
) {
}
