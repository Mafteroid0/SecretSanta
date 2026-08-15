package ru.mafteroid.secretsanta.dto;

public record RegisterForm(
        String username,
        String displayName,
        String password
) {
}
