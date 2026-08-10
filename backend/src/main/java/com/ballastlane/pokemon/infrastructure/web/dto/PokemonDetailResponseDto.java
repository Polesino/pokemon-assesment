package com.ballastlane.pokemon.infrastructure.web.dto;

import com.ballastlane.pokemon.domain.model.EvolutionNode;
import com.ballastlane.pokemon.domain.model.PokemonDetail;
import java.util.List;
import java.util.Map;

public record PokemonDetailResponseDto(
        Long id,
        String name,
        Integer height,
        Integer weight,
        String category,
        String description,
        Map<String, Integer> stats,
        Map<String, String> sprites,
        List<String> skills,
        List<EvolutionNodeResponseDto> evolutionChain
) {
    public static PokemonDetailResponseDto fromDomain(PokemonDetail pokemonDetail) {
        return new PokemonDetailResponseDto(
                pokemonDetail.id(),
                pokemonDetail.name(),
                pokemonDetail.height(),
                pokemonDetail.weight(),
                pokemonDetail.category(),
                pokemonDetail.description(),
                pokemonDetail.stats(),
                pokemonDetail.sprites(),
                pokemonDetail.skills(),
                pokemonDetail.evolutionChain().stream()
                        .map(EvolutionNodeResponseDto::fromDomain)
                        .toList()
        );
    }

    public record EvolutionNodeResponseDto(
            String speciesName,
            Integer minLevel,
            String trigger,
            List<EvolutionNodeResponseDto> evolvesTo
    ) {
        private static EvolutionNodeResponseDto fromDomain(EvolutionNode evolutionNode) {
            return new EvolutionNodeResponseDto(
                    evolutionNode.speciesName(),
                    evolutionNode.minLevel(),
                    evolutionNode.trigger(),
                    evolutionNode.evolvesTo().stream()
                            .map(EvolutionNodeResponseDto::fromDomain)
                            .toList()
            );
        }
    }
}
