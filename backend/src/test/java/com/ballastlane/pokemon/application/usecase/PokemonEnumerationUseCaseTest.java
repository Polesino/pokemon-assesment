package com.ballastlane.pokemon.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ballastlane.pokemon.domain.model.Pokemon;
import com.ballastlane.pokemon.domain.model.PokemonPage;
import com.ballastlane.pokemon.domain.port.out.PokeApiPort;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PokemonEnumerationUseCaseTest {
    @Mock
    private PokeApiPort pokeApiPort;

    @InjectMocks
    private PokemonEnumerationUseCaseImpl useCase;

    @Test
    void shouldReturnPaginatedPokemonSuccessfully() {
        Pokemon pikachu = new Pokemon(
                25L,
                "pikachu",
                4,
                60,
                "mouse",
                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/25.png",
                List.of("static", "lightning-rod")
        );
        Pokemon bulbasaur = new Pokemon(
                1L,
                "bulbasaur",
                7,
                69,
                "seed",
                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png",
                List.of("overgrow", "chlorophyll")
        );
        PokemonPage expectedPage = new PokemonPage(List.of(pikachu, bulbasaur), 2, 0, 20);

        when(pokeApiPort.fetchPaginatedPokemon(0, 20)).thenReturn(expectedPage);

        PokemonPage actualPage = useCase.getPaginatedPokemon(0, 20);

        assertThat(actualPage.totalElements()).isEqualTo(2);
        assertThat(actualPage.items()).containsExactly(pikachu, bulbasaur);
        verify(pokeApiPort).fetchPaginatedPokemon(0, 20);
    }

    @Test
    void shouldThrowExceptionWhenPageOrSizeIsInvalid() {
        assertThrows(IllegalArgumentException.class, () -> useCase.getPaginatedPokemon(-1, 20));
        assertThrows(IllegalArgumentException.class, () -> useCase.getPaginatedPokemon(0, 0));
    }
}
