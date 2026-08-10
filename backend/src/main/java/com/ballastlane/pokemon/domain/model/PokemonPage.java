package com.ballastlane.pokemon.domain.model;

import java.util.List;

public record PokemonPage(
        List<Pokemon> items,
        long totalElements,
        int pageNumber,
        int pageSize
) {
}
