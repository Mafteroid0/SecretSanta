package ru.mafteroid.secretsanta.dto;

import java.util.UUID;

public record WishlistItemResponse(
        UUID id,
        String name,
        String description
) {
}
