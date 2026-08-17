package ru.mafteroid.secretsanta.dto;

import java.time.LocalDateTime;

public record CreateEventForm(
        String name,
        LocalDateTime deadline
) {
}
