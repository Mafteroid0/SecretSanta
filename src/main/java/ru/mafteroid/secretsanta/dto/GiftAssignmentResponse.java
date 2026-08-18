package ru.mafteroid.secretsanta.dto;

import java.util.UUID;

public record GiftAssignmentResponse(
        UUID userId,
        String username,
        String displayName
) {
}