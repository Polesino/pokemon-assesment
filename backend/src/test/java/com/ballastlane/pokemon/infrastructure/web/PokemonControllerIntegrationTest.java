package com.ballastlane.pokemon.infrastructure.web;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ballastlane.pokemon.domain.model.Pokemon;
import com.ballastlane.pokemon.domain.model.PokemonPage;
import com.ballastlane.pokemon.domain.port.out.PokeApiPort;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class PokemonControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PokeApiPort pokeApiPort;

    @Test
    void shouldReturnPaginatedPokemonSuccessfully() throws Exception {
        Pokemon pikachu = new Pokemon(
                25L,
                "pikachu",
                4,
                60,
                "electric",
                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/25.png",
                List.of("static", "lightning-rod")
        );
        Pokemon bulbasaur = new Pokemon(
                1L,
                "bulbasaur",
                7,
                69,
                "grass",
                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png",
                List.of("overgrow", "chlorophyll")
        );

        when(pokeApiPort.fetchPaginatedPokemon(0, 5))
                .thenReturn(new PokemonPage(List.of(pikachu, bulbasaur), 2, 0, 5));

        mockMvc.perform(get("/api/v1/pokemon")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").exists())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.pageSize").value(5));
    }

    @Test
    void shouldReturnBadRequestWhenPageIsInvalid() throws Exception {
        mockMvc.perform(get("/api/v1/pokemon")
                        .param("page", "-1")
                        .param("size", "5"))
                .andExpect(status().isBadRequest());
    }
}
