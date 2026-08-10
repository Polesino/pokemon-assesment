package com.ballastlane.pokemon.application.usecase;

import com.ballastlane.pokemon.domain.model.PokemonDetail;
import com.ballastlane.pokemon.domain.model.PokemonLocal;
import com.ballastlane.pokemon.domain.port.in.PokemonSyncUseCase;
import com.ballastlane.pokemon.domain.port.out.PokeApiPort;
import com.ballastlane.pokemon.domain.port.out.PokemonRepositoryPort;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class PokemonSyncUseCaseImpl implements PokemonSyncUseCase {
    private final PokeApiPort pokeApiPort;
    private final PokemonRepositoryPort pokemonRepositoryPort;

    public PokemonSyncUseCaseImpl(PokeApiPort pokeApiPort, PokemonRepositoryPort pokemonRepositoryPort) {
        this.pokeApiPort = pokeApiPort;
        this.pokemonRepositoryPort = pokemonRepositoryPort;
    }

    @Override
    public PokemonLocal syncPokemon(
            String idOrName,
            String localizedName,
            String locationMetadata,
            Set<String> tags
    ) {
        if (idOrName == null || idOrName.isBlank()) {
            throw new IllegalArgumentException("Pokemon name or id must not be blank");
        }

        PokemonDetail pokemonDetail = pokeApiPort.fetchPokemonDetail(idOrName);
        PokemonLocal pokemonLocal = PokemonLocal.builder()
                .id(pokemonDetail.id())
                .name(pokemonDetail.name())
                .height(pokemonDetail.height())
                .weight(pokemonDetail.weight())
                .category(pokemonDetail.category())
                .spriteUrl(pokemonDetail.sprites().get("front_default"))
                .localizedName(localizedName)
                .locationMetadata(locationMetadata)
                .tags(tags)
                .build();

        return pokemonRepositoryPort.save(pokemonLocal);
    }
}
