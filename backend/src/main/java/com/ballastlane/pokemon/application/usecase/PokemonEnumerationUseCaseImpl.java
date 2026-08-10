package com.ballastlane.pokemon.application.usecase;

import com.ballastlane.pokemon.domain.model.PokemonPage;
import com.ballastlane.pokemon.domain.port.in.PokemonEnumerationUseCase;
import com.ballastlane.pokemon.domain.port.out.PokeApiPort;
import org.springframework.stereotype.Component;

@Component
public class PokemonEnumerationUseCaseImpl implements PokemonEnumerationUseCase {
    private final PokeApiPort pokeApiPort;

    public PokemonEnumerationUseCaseImpl(PokeApiPort pokeApiPort) {
        this.pokeApiPort = pokeApiPort;
    }

    @Override
    public PokemonPage getPaginatedPokemon(int page, int size) {
        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException("Page must be non-negative and size must be greater than zero");
        }

        return pokeApiPort.fetchPaginatedPokemon(page, size);
    }
}
