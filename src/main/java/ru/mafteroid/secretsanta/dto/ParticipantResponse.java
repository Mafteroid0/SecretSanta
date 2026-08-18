package ru.mafteroid.secretsanta.dto;

import java.util.UUID;

public record ParticipantResponse(
        UUID participantId,
        UUID userId,
        String username,
        String displayName,
        boolean owner
) {
}