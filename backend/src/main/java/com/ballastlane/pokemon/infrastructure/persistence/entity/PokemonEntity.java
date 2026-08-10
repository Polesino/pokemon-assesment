package com.ballastlane.pokemon.infrastructure.persistence.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "pokemons")
public class PokemonEntity {
    @Id
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private Integer height;
    private Integer weight;
    private String category;
    private String spriteUrl;
    private String localizedName;

    @Column(length = 2_000)
    private String locationMetadata;

    @ElementCollection
    @CollectionTable(name = "pokemon_tags", joinColumns = @JoinColumn(name = "pokemon_id"))
    @Column(name = "tag")
    private Set<String> tags = new HashSet<>();
}
