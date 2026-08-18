package ru.mafteroid.secretsanta.dto;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        String displayName
) {
}
