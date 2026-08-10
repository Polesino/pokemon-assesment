package com.ballastlane.pokemon.application;

import com.ballastlane.pokemon.domain.model.PokemonPage;
import com.ballastlane.pokemon.domain.model.PokemonPageRequest;
import com.ballastlane.pokemon.domain.port.in.EnumeratePokemonUseCase;
import com.ballastlane.pokemon.domain.port.out.PokemonCatalogPort;

public class PokemonEnumerationUseCase implements EnumeratePokemonUseCase {
    private final PokemonCatalogPort pokemonCatalogPort;

    public PokemonEnumerationUseCase(PokemonCatalogPort pokemonCatalogPort) {
        this.pokemonCatalogPort = pokemonCatalogPort;
    }

    @Override
    public PokemonPage enumerate(PokemonPageRequest request) {
        return pokemonCatalogPort.findPage(request);
    }
}
