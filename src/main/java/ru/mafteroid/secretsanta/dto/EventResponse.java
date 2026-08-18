package ru.mafteroid.secretsanta.dto;

import java.time.Instant;
import java.util.UUID;

public record EventResponse(
        UUID id,
        String name,
        Instant deadline,
        boolean started,
        UUID ownerId
) {
}
