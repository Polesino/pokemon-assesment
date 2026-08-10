package com.ballastlane.pokemon.infrastructure.web.controller;

import com.ballastlane.pokemon.domain.model.PokemonLocal;
import com.ballastlane.pokemon.domain.model.PokemonDetail;
import com.ballastlane.pokemon.domain.model.PokemonPage;
import com.ballastlane.pokemon.domain.port.in.PokemonCrudUseCase;
import com.ballastlane.pokemon.domain.port.in.PokemonDetailUseCase;
import com.ballastlane.pokemon.domain.port.in.PokemonEnumerationUseCase;
import com.ballastlane.pokemon.domain.port.in.PokemonSyncUseCase;
import com.ballastlane.pokemon.infrastructure.web.dto.PokemonDetailResponseDto;
import com.ballastlane.pokemon.infrastructure.web.dto.PokemonLocalResponseDto;
import com.ballastlane.pokemon.infrastructure.web.dto.PokemonPageResponseDto;
import com.ballastlane.pokemon.infrastructure.web.dto.SyncPokemonRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/v1/pokemon")
public class PokemonController {
    private final PokemonEnumerationUseCase pokemonEnumerationUseCase;
    private final PokemonDetailUseCase pokemonDetailUseCase;
    private final PokemonSyncUseCase pokemonSyncUseCase;
    private final PokemonCrudUseCase pokemonCrudUseCase;

    public PokemonController(
            PokemonEnumerationUseCase pokemonEnumerationUseCase,
            PokemonDetailUseCase pokemonDetailUseCase,
            PokemonSyncUseCase pokemonSyncUseCase,
            PokemonCrudUseCase pokemonCrudUseCase
    ) {
        this.pokemonEnumerationUseCase = pokemonEnumerationUseCase;
        this.pokemonDetailUseCase = pokemonDetailUseCase;
        this.pokemonSyncUseCase = pokemonSyncUseCase;
        this.pokemonCrudUseCase = pokemonCrudUseCase;
    }

    @GetMapping
    public ResponseEntity<PokemonPageResponseDto> getPokemon(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PokemonPage pokemonPage = pokemonEnumerationUseCase.getPaginatedPokemon(page, size);
        return ResponseEntity.ok(PokemonPageResponseDto.fromDomain(pokemonPage));
    }

    @GetMapping("/local")
    public ResponseEntity<List<PokemonLocalResponseDto>> getLocalPokemon() {
        List<PokemonLocalResponseDto> localPokemon = pokemonCrudUseCase.getAllLocalPokemon().stream()
                .map(PokemonLocalResponseDto::fromDomain)
                .toList();
        return ResponseEntity.ok(localPokemon);
    }

    @GetMapping("/local/{id}")
    public ResponseEntity<PokemonLocalResponseDto> getLocalPokemonById(@PathVariable Long id) {
        PokemonLocal pokemonLocal = pokemonCrudUseCase.getLocalPokemonById(id);
        return ResponseEntity.ok(PokemonLocalResponseDto.fromDomain(pokemonLocal));
    }

    @PutMapping("/local/{id}")
    public ResponseEntity<PokemonLocalResponseDto> updateLocalPokemon(
            @PathVariable Long id,
            @RequestBody SyncPokemonRequestDto request
    ) {
        PokemonLocal pokemonLocal = pokemonCrudUseCase.updateLocalPokemon(
                id,
                request.localizedName(),
                request.locationMetadata(),
                request.tags()
        );
        return ResponseEntity.ok(PokemonLocalResponseDto.fromDomain(pokemonLocal));
    }

    @DeleteMapping("/local/{id}")
    public ResponseEntity<Void> deleteLocalPokemon(@PathVariable Long id) {
        pokemonCrudUseCase.deleteLocalPokemon(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{idOrName}")
    public ResponseEntity<PokemonDetailResponseDto> getPokemonDetail(@PathVariable String idOrName) {
        PokemonDetail pokemonDetail = pokemonDetailUseCase.getPokemonDetail(idOrName);
        return ResponseEntity.ok(PokemonDetailResponseDto.fromDomain(pokemonDetail));
    }

    @PostMapping("/sync/{idOrName}")
    public ResponseEntity<PokemonLocalResponseDto> syncPokemon(
            @PathVariable String idOrName,
            @RequestBody SyncPokemonRequestDto request
    ) {
        PokemonLocal pokemonLocal = pokemonSyncUseCase.syncPokemon(
                idOrName,
                request.localizedName(),
                request.locationMetadata(),
                request.tags()
        );
        return ResponseEntity.ok(PokemonLocalResponseDto.fromDomain(pokemonLocal));
    }
}
