package ru.mafteroid.secretsanta.dto;

import jakarta.validation.constraints.NotBlank;

public record ImportWishlistRequest(
        @NotBlank
        String url
) {
}
