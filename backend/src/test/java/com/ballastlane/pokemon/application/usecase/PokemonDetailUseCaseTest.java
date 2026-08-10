package com.ballastlane.pokemon.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ballastlane.pokemon.domain.model.EvolutionNode;
import com.ballastlane.pokemon.domain.model.PokemonDetail;
import com.ballastlane.pokemon.domain.port.out.PokeApiPort;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PokemonDetailUseCaseTest {
    @Mock
    private PokeApiPort pokeApiPort;

    @InjectMocks
    private PokemonDetailUseCaseImpl useCase;

    @Test
    void shouldReturnPokemonDetailSuccessfully() {
        PokemonDetail expectedDetail = new PokemonDetail(
                1L,
                "bulbasaur",
                7,
                69,
                "seed",
                "A strange seed was planted on its back at birth.",
                Map.of(
                        "hp", 45,
                        "attack", 49,
                        "defense", 49,
                        "special-attack", 65,
                        "special-defense", 65,
                        "speed", 45
                ),
                Map.of(
                        "front_default", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png",
                        "back_default", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/back/1.png",
                        "front_shiny", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/shiny/1.png"
                ),
                List.of("overgrow", "chlorophyll"),
                List.of(new EvolutionNode(
                        "bulbasaur",
                        null,
                        null,
                        List.of(new EvolutionNode(
                                "ivysaur",
                                16,
                                "level-up",
                                List.of(new EvolutionNode(
                                        "venusaur",
                                        32,
                                        "level-up",
                                        List.of()
                                ))
                        ))
                ))
        );

        when(pokeApiPort.fetchPokemonDetail("bulbasaur")).thenReturn(expectedDetail);

        PokemonDetail actualDetail = useCase.getPokemonDetail("bulbasaur");

        assertThat(actualDetail.name()).isEqualTo("bulbasaur");
        assertThat(actualDetail.stats())
                .containsEntry("hp", 45)
                .containsEntry("attack", 49);
        assertThat(actualDetail.description()).isEqualTo("A strange seed was planted on its back at birth.");
        assertThat(actualDetail.evolutionChain()).isNotEmpty();
        verify(pokeApiPort).fetchPokemonDetail("bulbasaur");
    }

    @Test
    void shouldThrowExceptionWhenNameOrIdIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> useCase.getPokemonDetail(""));
        assertThrows(IllegalArgumentException.class, () -> useCase.getPokemonDetail("  "));
    }
}
