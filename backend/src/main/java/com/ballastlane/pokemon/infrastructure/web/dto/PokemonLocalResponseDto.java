package com.ballastlane.pokemon.infrastructure.web.dto;

import com.ballastlane.pokemon.domain.model.PokemonLocal;
import java.util.Set;

public record PokemonLocalResponseDto(
        Long id,
        String name,
        Integer height,
        Integer weight,
        String category,
        String spriteUrl,
        String localizedName,
        String locationMetadata,
        Set<String> tags
) {
    public static PokemonLocalResponseDto fromDomain(PokemonLocal pokemonLocal) {
        return new PokemonLocalResponseDto(
                pokemonLocal.getId(),
                pokemonLocal.getName(),
                pokemonLocal.getHeight(),
                pokemonLocal.getWeight(),
                pokemonLocal.getCategory(),
                pokemonLocal.getSpriteUrl(),
                pokemonLocal.getLocalizedName(),
                pokemonLocal.getLocationMetadata(),
                pokemonLocal.getTags()
        );
    }
}
