package com.ballastlane.pokemon.domain.port.in;

import com.ballastlane.pokemon.domain.model.PokemonPage;
import com.ballastlane.pokemon.domain.model.PokemonPageRequest;

public interface EnumeratePokemonUseCase {
    PokemonPage enumerate(PokemonPageRequest request);
}
