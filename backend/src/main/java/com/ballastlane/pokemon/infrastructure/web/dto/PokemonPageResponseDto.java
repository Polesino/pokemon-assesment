package com.ballastlane.pokemon.infrastructure.web.dto;

import com.ballastlane.pokemon.domain.model.PokemonPage;
import java.util.List;

public record PokemonPageResponseDto(
        List<PokemonResponseDto> items,
        long totalElements,
        int pageNumber,
        int pageSize
) {
    public static PokemonPageResponseDto fromDomain(PokemonPage page) {
        return new PokemonPageResponseDto(
                page.items().stream()
                        .map(PokemonResponseDto::fromDomain)
                        .toList(),
                page.totalElements(),
                page.pageNumber(),
                page.pageSize()
        );
    }
}
