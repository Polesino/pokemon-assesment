package com.ballastlane.pokemon.domain.port.out;

import com.ballastlane.pokemon.domain.model.PokemonPage;
import com.ballastlane.pokemon.domain.model.PokemonPageRequest;

public interface PokemonCatalogPort {
    PokemonPage findPage(PokemonPageRequest request);
}
