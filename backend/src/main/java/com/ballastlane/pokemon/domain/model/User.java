package com.ballastlane.pokemon.domain.model;

public record User(
        Long id,
        String username,
        String password,
        String role
) {
}
