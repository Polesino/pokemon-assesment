package com.ballastlane.pokemon.application.usecase;

import com.ballastlane.pokemon.domain.model.PokemonDetail;
import com.ballastlane.pokemon.domain.port.in.PokemonDetailUseCase;
import com.ballastlane.pokemon.domain.port.out.PokeApiPort;
import org.springframework.stereotype.Component;

@Component
public class PokemonDetailUseCaseImpl implements PokemonDetailUseCase {
    private final PokeApiPort pokeApiPort;

    public PokemonDetailUseCaseImpl(PokeApiPort pokeApiPort) {
        this.pokeApiPort = pokeApiPort;
    }

    @Override
    public PokemonDetail getPokemonDetail(String idOrName) {
        if (idOrName == null || idOrName.isBlank()) {
            throw new IllegalArgumentException("Pokemon name or id must not be blank");
        }

        return pokeApiPort.fetchPokemonDetail(idOrName);
    }
}
