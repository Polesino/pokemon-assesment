package com.ballastlane.pokemon.domain.port.in;

import com.ballastlane.pokemon.domain.model.PokemonLocal;
import java.util.List;
import java.util.Set;

public interface PokemonCrudUseCase {
    PokemonLocal updateLocalPokemon(
            Long id,
            String localizedName,
            String locationMetadata,
            Set<String> tags
    );

    PokemonLocal getLocalPokemonById(Long id);

    List<PokemonLocal> getAllLocalPokemon();

    void deleteLocalPokemon(Long id);
}
