package ru.mafteroid.secretsanta.dto;

import java.time.LocalDateTime;

public record CreateEventRequest(
        String name,
        LocalDateTime deadline
) {
}
