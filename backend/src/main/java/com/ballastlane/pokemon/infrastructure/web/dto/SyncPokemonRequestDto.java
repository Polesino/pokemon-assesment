package com.ballastlane.pokemon.infrastructure.web.dto;

import java.util.Set;

public record SyncPokemonRequestDto(
        String localizedName,
        String locationMetadata,
        Set<String> tags
) {
}
