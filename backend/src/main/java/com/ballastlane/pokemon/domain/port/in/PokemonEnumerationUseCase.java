package com.ballastlane.pokemon.domain.port.in;

import com.ballastlane.pokemon.domain.model.PokemonPage;

public interface PokemonEnumerationUseCase {
    PokemonPage getPaginatedPokemon(int page, int size);
}
