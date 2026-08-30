package ru.mafteroid.secretsanta.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterForm(
        @NotBlank
        @Size(min = 3, max = 50)
        String username,

        @NotBlank
        @Size(max = 100)
        String displayName,

        @NotBlank
        @Size(min = 8, max = 72)
        String password
) {
}
