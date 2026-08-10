package com.ballastlane.pokemon.application.usecase;

import com.ballastlane.pokemon.domain.exception.PokemonNotFoundException;
import com.ballastlane.pokemon.domain.model.PokemonLocal;
import com.ballastlane.pokemon.domain.port.in.PokemonCrudUseCase;
import com.ballastlane.pokemon.domain.port.out.PokemonRepositoryPort;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class PokemonCrudUseCaseImpl implements PokemonCrudUseCase {
    private final PokemonRepositoryPort pokemonRepositoryPort;

    public PokemonCrudUseCaseImpl(PokemonRepositoryPort pokemonRepositoryPort) {
        this.pokemonRepositoryPort = pokemonRepositoryPort;
    }

    @Override
    public PokemonLocal updateLocalPokemon(
            Long id,
            String localizedName,
            String locationMetadata,
            Set<String> tags
    ) {
        PokemonLocal existingPokemon = pokemonRepositoryPort.findById(id)
                .orElseThrow(() -> notFound(id));

        PokemonLocal updatedPokemon = PokemonLocal.builder()
                .id(existingPokemon.getId())
                .name(existingPokemon.getName())
                .height(existingPokemon.getHeight())
                .weight(existingPokemon.getWeight())
                .category(existingPokemon.getCategory())
                .spriteUrl(existingPokemon.getSpriteUrl())
                .localizedName(localizedName != null ? localizedName : existingPokemon.getLocalizedName())
                .locationMetadata(locationMetadata != null ? locationMetadata : existingPokemon.getLocationMetadata())
                .tags(tags != null ? tags : existingPokemon.getTags())
                .build();

        return pokemonRepositoryPort.save(updatedPokemon);
    }

    @Override
    public PokemonLocal getLocalPokemonById(Long id) {
        return pokemonRepositoryPort.findById(id)
                .orElseThrow(() -> notFound(id));
    }

    @Override
    public List<PokemonLocal> getAllLocalPokemon() {
        return pokemonRepositoryPort.findAllLocal();
    }

    @Override
    public void deleteLocalPokemon(Long id) {
        if (!pokemonRepositoryPort.existsById(id)) {
            throw notFound(id);
        }

        pokemonRepositoryPort.deleteById(id);
    }

    private PokemonNotFoundException notFound(Long id) {
        return new PokemonNotFoundException("Local Pokemon with ID " + id + " not found");
    }
}
