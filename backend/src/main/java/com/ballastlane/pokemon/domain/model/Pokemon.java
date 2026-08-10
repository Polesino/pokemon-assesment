package com.ballastlane.pokemon.domain.model;

import java.util.List;

public record Pokemon(
        Long id,
        String name,
        Integer height,
        Integer weight,
        String category,
        String spriteUrl,
        List<String> skills
) {
    public Pokemon(
            Long id,
            String name,
            Integer height,
            Integer weight,
            String category,
            String spriteUrl,
            String localizedName,
            String locationMetadata,
            List<String> tags,
            List<String> skills
    ) {
        this(id, name, height, weight, category, spriteUrl, skills);
    }
}
