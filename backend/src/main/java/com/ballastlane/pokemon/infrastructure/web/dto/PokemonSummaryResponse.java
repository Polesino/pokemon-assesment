package com.ballastlane.pokemon.infrastructure.web.dto;

import java.util.List;

public record PokemonSummaryResponse(
        Long id,
        String name,
        Integer weight,
        String spriteUrl,
        List<String> skills
) {
}
