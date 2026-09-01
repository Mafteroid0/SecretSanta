package ru.mafteroid.secretsanta.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
    @NotBlank
    @Size(max = 100)
    String displayName
){}
