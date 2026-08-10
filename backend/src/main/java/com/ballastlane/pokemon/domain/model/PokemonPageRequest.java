package com.ballastlane.pokemon.domain.model;

public record PokemonPageRequest(int page, int size) {
    public PokemonPageRequest {
        if (page < 0) {
            throw new IllegalArgumentException("page must be greater than or equal to zero");
        }
        if (size < 1) {
            throw new IllegalArgumentException("size must be greater than zero");
        }
    }
}
