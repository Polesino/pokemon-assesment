package com.ballastlane.pokemon.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ballastlane.pokemon.domain.model.Pokemon;
import com.ballastlane.pokemon.domain.model.PokemonPage;
import com.ballastlane.pokemon.domain.model.PokemonPageRequest;
import com.ballastlane.pokemon.domain.port.out.PokemonCatalogPort;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PokemonEnumerationUseCaseTest {
    @Mock
    private PokemonCatalogPort pokemonCatalogPort;

    private PokemonEnumerationUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new PokemonEnumerationUseCase(pokemonCatalogPort);
    }

    @Test
    @DisplayName("RED: enumerates a paginated Pokemon list with sprite, mass, and skills")
    void enumerateReturnsPaginatedPokemonSummaries() {
        PokemonPageRequest request = new PokemonPageRequest(0, 20);
        Pokemon pikachu = new Pokemon(
                25L,
                "pikachu",
                4,
                60,
                "mouse",
                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/25.png",
                null,
                null,
                List.of(),
                List.of("static", "lightning-rod")
        );
        PokemonPage expectedPage = new PokemonPage(List.of(pikachu), 0, 20, 1);

        when(pokemonCatalogPort.findPage(request)).thenReturn(expectedPage);

        PokemonPage actualPage = useCase.enumerate(request);

        assertThat(actualPage).isEqualTo(expectedPage);
        assertThat(actualPage.items())
                .singleElement()
                .satisfies(pokemon -> {
                    assertThat(pokemon.spriteUrl()).contains("/25.png");
                    assertThat(pokemon.weight()).isEqualTo(60);
                    assertThat(pokemon.skills()).containsExactly("static", "lightning-rod");
                });
        verify(pokemonCatalogPort).findPage(request);
    }
}
