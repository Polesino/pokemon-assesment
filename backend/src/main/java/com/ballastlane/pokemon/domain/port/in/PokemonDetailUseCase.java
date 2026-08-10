package com.ballastlane.pokemon.domain.port.in;

import com.ballastlane.pokemon.domain.model.PokemonDetail;

public interface PokemonDetailUseCase {
    PokemonDetail getPokemonDetail(String idOrName);
}
