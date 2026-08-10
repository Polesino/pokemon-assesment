package com.ballastlane.pokemon.domain.model;

import java.util.List;
import java.util.Map;

public record PokemonDetail(
        Long id,
        String name,
        Integer height,
        Integer weight,
        String category,
        String description,
        Map<String, Integer> stats,
        Map<String, String> sprites,
        List<String> skills,
        List<EvolutionNode> evolutionChain
) {
}
