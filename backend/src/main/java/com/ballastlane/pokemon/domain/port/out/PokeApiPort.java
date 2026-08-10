package com.ballastlane.pokemon.domain.port.out;

import com.ballastlane.pokemon.domain.model.PokemonDetail;
import com.ballastlane.pokemon.domain.model.PokemonPage;

public interface PokeApiPort {
    PokemonPage fetchPaginatedPokemon(int page, int size);

    PokemonDetail fetchPokemonDetail(String idOrName);
}
