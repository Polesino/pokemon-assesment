package com.ballastlane.pokemon.domain.port.in;

import com.ballastlane.pokemon.domain.model.PokemonLocal;
import java.util.Set;

public interface PokemonSyncUseCase {
    PokemonLocal syncPokemon(
            String idOrName,
            String localizedName,
            String locationMetadata,
            Set<String> tags
    );
}
