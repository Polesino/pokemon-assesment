package com.ballastlane.pokemon.domain.port.out;

import com.ballastlane.pokemon.domain.model.PokemonLocal;
import java.util.List;
import java.util.Optional;

public interface PokemonRepositoryPort {
    PokemonLocal save(PokemonLocal pokemonLocal);

    Optional<PokemonLocal> findById(Long id);

    List<PokemonLocal> findAllLocal();

    boolean existsById(Long id);

    void deleteById(Long id);
}
