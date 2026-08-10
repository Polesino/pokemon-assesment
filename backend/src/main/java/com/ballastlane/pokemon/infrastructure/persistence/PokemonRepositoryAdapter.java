package com.ballastlane.pokemon.infrastructure.persistence;

import com.ballastlane.pokemon.domain.model.PokemonLocal;
import com.ballastlane.pokemon.domain.port.out.PokemonRepositoryPort;
import com.ballastlane.pokemon.infrastructure.persistence.entity.PokemonEntity;
import com.ballastlane.pokemon.infrastructure.persistence.repository.JpaPokemonRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class PokemonRepositoryAdapter implements PokemonRepositoryPort {
    private final JpaPokemonRepository jpaPokemonRepository;

    public PokemonRepositoryAdapter(JpaPokemonRepository jpaPokemonRepository) {
        this.jpaPokemonRepository = jpaPokemonRepository;
    }

    @Override
    public PokemonLocal save(PokemonLocal pokemonLocal) {
        PokemonEntity savedEntity = jpaPokemonRepository.save(toEntity(pokemonLocal));
        return toDomain(savedEntity);
    }

    @Override
    public Optional<PokemonLocal> findById(Long id) {
        return jpaPokemonRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public List<PokemonLocal> findAllLocal() {
        return jpaPokemonRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public boolean existsById(Long id) {
        return jpaPokemonRepository.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        jpaPokemonRepository.deleteById(id);
    }

    private PokemonEntity toEntity(PokemonLocal pokemonLocal) {
        PokemonEntity entity = new PokemonEntity();
        entity.setId(pokemonLocal.getId());
        entity.setName(pokemonLocal.getName());
        entity.setHeight(pokemonLocal.getHeight());
        entity.setWeight(pokemonLocal.getWeight());
        entity.setCategory(pokemonLocal.getCategory());
        entity.setSpriteUrl(pokemonLocal.getSpriteUrl());
        entity.setLocalizedName(pokemonLocal.getLocalizedName());
        entity.setLocationMetadata(pokemonLocal.getLocationMetadata());
        entity.setTags(new HashSet<>(pokemonLocal.getTags()));
        return entity;
    }

    private PokemonLocal toDomain(PokemonEntity entity) {
        return PokemonLocal.builder()
                .id(entity.getId())
                .name(entity.getName())
                .height(entity.getHeight())
                .weight(entity.getWeight())
                .category(entity.getCategory())
                .spriteUrl(entity.getSpriteUrl())
                .localizedName(entity.getLocalizedName())
                .locationMetadata(entity.getLocationMetadata())
                .tags(new HashSet<>(entity.getTags()))
                .build();
    }
}
