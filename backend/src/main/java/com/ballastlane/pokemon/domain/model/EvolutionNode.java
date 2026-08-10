package com.ballastlane.pokemon.domain.model;

import java.util.List;

public record EvolutionNode(
        String speciesName,
        Integer minLevel,
        String trigger,
        List<EvolutionNode> evolvesTo
) {
}
