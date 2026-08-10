package com.ballastlane.pokemon.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ballastlane.pokemon.domain.exception.PokemonNotFoundException;
import com.ballastlane.pokemon.domain.model.PokemonLocal;
import com.ballastlane.pokemon.domain.port.out.PokemonRepositoryPort;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PokemonCrudUseCaseTest {
    @Mock
    private PokemonRepositoryPort pokemonRepositoryPort;

    @InjectMocks
    private PokemonCrudUseCaseImpl useCase;

    @Test
    void shouldUpdateLocalPokemonSuccessfully() {
        PokemonLocal existingPokemon = PokemonLocal.builder()
                .id(1L)
                .name("bulbasaur")
                .height(7)
                .weight(69)
                .category("seed")
                .spriteUrl("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png")
                .localizedName("Bulbasaur ES")
                .locationMetadata("Kanto Forest")
                .tags(Set.of("starter", "grass"))
                .build();

        when(pokemonRepositoryPort.findById(1L)).thenReturn(Optional.of(existingPokemon));
        when(pokemonRepositoryPort.save(any(PokemonLocal.class)))
                .thenAnswer(invocation -> invocation.getArgument(0, PokemonLocal.class));

        PokemonLocal updatedPokemon = useCase.updateLocalPokemon(
                1L,
                "New Name",
                "New Region",
                Set.of("tag1")
        );

        ArgumentCaptor<PokemonLocal> pokemonLocalCaptor = ArgumentCaptor.forClass(PokemonLocal.class);
        verify(pokemonRepositoryPort).save(pokemonLocalCaptor.capture());

        PokemonLocal savedPokemon = pokemonLocalCaptor.getValue();
        assertThat(savedPokemon.getId()).isEqualTo(1L);
        assertThat(savedPokemon.getName()).isEqualTo("bulbasaur");
        assertThat(savedPokemon.getHeight()).isEqualTo(7);
        assertThat(savedPokemon.getWeight()).isEqualTo(69);
        assertThat(savedPokemon.getCategory()).isEqualTo("seed");
        assertThat(savedPokemon.getSpriteUrl())
                .isEqualTo("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png");
        assertThat(savedPokemon.getLocalizedName()).isEqualTo("New Name");
        assertThat(savedPokemon.getLocationMetadata()).isEqualTo("New Region");
        assertThat(savedPokemon.getTags()).containsExactly("tag1");
        assertThat(updatedPokemon).isEqualTo(savedPokemon);
    }

    @Test
    void shouldThrowPokemonNotFoundExceptionWhenIdDoesNotExist() {
        when(pokemonRepositoryPort.findById(999L)).thenReturn(Optional.empty());
        when(pokemonRepositoryPort.existsById(999L)).thenReturn(false);

        assertThrows(PokemonNotFoundException.class, () -> useCase.getLocalPokemonById(999L));
        assertThrows(
                PokemonNotFoundException.class,
                () -> useCase.updateLocalPokemon(999L, "New Name", "New Region", Set.of("tag1"))
        );
        assertThrows(PokemonNotFoundException.class, () -> useCase.deleteLocalPokemon(999L));
    }

    @Test
    void shouldDeleteLocalPokemonSuccessfully() {
        when(pokemonRepositoryPort.existsById(1L)).thenReturn(true);

        useCase.deleteLocalPokemon(1L);

        verify(pokemonRepositoryPort).deleteById(1L);
    }
}
