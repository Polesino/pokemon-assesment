package com.ballastlane.pokemon.infrastructure.persistence.repository;

import com.ballastlane.pokemon.infrastructure.persistence.entity.PokemonEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PokemonJpaRepository extends JpaRepository<PokemonEntity, Long> {
}
