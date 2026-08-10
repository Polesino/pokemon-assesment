package com.ballastlane.pokemon.domain.exception;

public class PokemonNotFoundException extends RuntimeException {
    public PokemonNotFoundException(Long pokemonId) {
        super("Pokemon not found: " + pokemonId);
    }

    public PokemonNotFoundException(String message) {
        super(message);
    }
}
