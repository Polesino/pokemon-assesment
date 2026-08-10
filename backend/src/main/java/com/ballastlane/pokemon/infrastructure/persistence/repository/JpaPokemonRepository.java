package com.ballastlane.pokemon.infrastructure.persistence.repository;

import com.ballastlane.pokemon.infrastructure.persistence.entity.PokemonEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaPokemonRepository extends JpaRepository<PokemonEntity, Long> {
    @Override
    @EntityGraph(attributePaths = "tags")
    Optional<PokemonEntity> findById(Long id);

    @Override
    @EntityGraph(attributePaths = "tags")
    List<PokemonEntity> findAll();
}
