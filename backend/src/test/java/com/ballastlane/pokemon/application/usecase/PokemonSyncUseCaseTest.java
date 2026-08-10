package com.ballastlane.pokemon.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ballastlane.pokemon.domain.model.PokemonDetail;
import com.ballastlane.pokemon.domain.model.PokemonLocal;
import com.ballastlane.pokemon.domain.port.out.PokeApiPort;
import com.ballastlane.pokemon.domain.port.out.PokemonRepositoryPort;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PokemonSyncUseCaseTest {
    @Mock
    private PokeApiPort pokeApiPort;

    @Mock
    private PokemonRepositoryPort pokemonRepositoryPort;

    @InjectMocks
    private PokemonSyncUseCaseImpl useCase;

    @Test
    void shouldSyncPokemonFromExternalApiAndSaveLocally() {
        PokemonDetail bulbasaur = new PokemonDetail(
                1L,
                "bulbasaur",
                7,
                69,
                "seed",
                "A strange seed was planted on its back at birth.",
                Map.of("hp", 45, "attack", 49),
                Map.of("front_default", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png"),
                List.of("overgrow", "chlorophyll"),
                List.of()
        );

        when(pokeApiPort.fetchPokemonDetail("bulbasaur")).thenReturn(bulbasaur);
        when(pokemonRepositoryPort.save(any(PokemonLocal.class)))
                .thenAnswer(invocation -> invocation.getArgument(0, PokemonLocal.class));

        PokemonLocal syncedPokemon = useCase.syncPokemon(
                "bulbasaur",
                "Bulbasaur ES",
                "Kanto Forest",
                Set.of("starter", "grass")
        );

        ArgumentCaptor<PokemonLocal> pokemonLocalCaptor = ArgumentCaptor.forClass(PokemonLocal.class);
        verify(pokeApiPort).fetchPokemonDetail("bulbasaur");
        verify(pokemonRepositoryPort).save(pokemonLocalCaptor.capture());

        PokemonLocal savedPokemon = pokemonLocalCaptor.getValue();
        assertThat(savedPokemon.getId()).isEqualTo(1L);
        assertThat(savedPokemon.getName()).isEqualTo("bulbasaur");
        assertThat(savedPokemon.getHeight()).isEqualTo(7);
        assertThat(savedPokemon.getWeight()).isEqualTo(69);
        assertThat(savedPokemon.getCategory()).isEqualTo("seed");
        assertThat(savedPokemon.getSpriteUrl())
                .isEqualTo("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png");
        assertThat(savedPokemon.getLocalizedName()).isEqualTo("Bulbasaur ES");
        assertThat(savedPokemon.getLocationMetadata()).isEqualTo("Kanto Forest");
        assertThat(savedPokemon.getTags()).containsExactlyInAnyOrder("starter", "grass");
        assertThat(syncedPokemon).isEqualTo(savedPokemon);
    }

    @Test
    void shouldThrowExceptionWhenIdOrNameIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> useCase.syncPokemon("", null, null, null));
    }
}
