package com.ballastlane.pokemon.infrastructure.web.dto;

import com.ballastlane.pokemon.domain.model.Pokemon;
import java.util.List;

public record PokemonResponseDto(
        Long id,
        String name,
        Integer height,
        Integer weight,
        String category,
        String spriteUrl,
        List<String> skills
) {
    public static PokemonResponseDto fromDomain(Pokemon pokemon) {
        return new PokemonResponseDto(
                pokemon.id(),
                pokemon.name(),
                pokemon.height(),
                pokemon.weight(),
                pokemon.category(),
                pokemon.spriteUrl(),
                pokemon.skills()
        );
    }
}
