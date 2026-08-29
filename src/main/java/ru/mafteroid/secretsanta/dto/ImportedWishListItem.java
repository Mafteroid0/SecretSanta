package ru.mafteroid.secretsanta.dto;

public record ImportedWishListItem(
        String name,
        String description,
        String sourceUrl,
        String externalId
) {
}
