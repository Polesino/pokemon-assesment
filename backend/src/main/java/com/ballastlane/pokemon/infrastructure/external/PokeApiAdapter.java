package com.ballastlane.pokemon.infrastructure.external;

import com.ballastlane.pokemon.domain.model.EvolutionNode;
import com.ballastlane.pokemon.domain.model.Pokemon;
import com.ballastlane.pokemon.domain.model.PokemonDetail;
import com.ballastlane.pokemon.domain.model.PokemonPage;
import com.ballastlane.pokemon.domain.port.out.PokeApiPort;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URI;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class PokeApiAdapter implements PokeApiPort {
    private final WebClient webClient;

    public PokeApiAdapter(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    @Cacheable(value = "pokemon_pages", key = "#page + '-' + #size")
    public PokemonPage fetchPaginatedPokemon(int page, int size) {
        int offset = page * size;
        PokeApiPageResponse response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/pokemon")
                        .queryParam("limit", size)
                        .queryParam("offset", offset)
                        .build())
                .retrieve()
                .bodyToMono(PokeApiPageResponse.class)
                .block();

        List<Pokemon> pokemon = Flux.fromIterable(response.results())
                .flatMap(item -> fetchPokemonSummary(item.url()))
                .collectList()
                .block();

        return new PokemonPage(pokemon, response.count(), page, size);
    }

    @Override
    @Cacheable(value = "pokemon_details", key = "#idOrName")
    public PokemonDetail fetchPokemonDetail(String idOrName) {
        Mono<PokeApiPokemonDetailResponse> pokemonMono = webClient.get()
                .uri("/pokemon/{idOrName}", idOrName)
                .retrieve()
                .bodyToMono(PokeApiPokemonDetailResponse.class)
                .cache();

        Mono<PokeApiSpeciesResponse> speciesMono = pokemonMono
                .flatMap(pokemon -> webClient.get()
                        .uri("/pokemon-species/{id}", pokemon.id())
                        .retrieve()
                        .bodyToMono(PokeApiSpeciesResponse.class))
                .cache();

        Mono<PokeApiEvolutionChainResponse> evolutionChainMono = speciesMono
                .map(PokeApiSpeciesResponse::evolutionChain)
                .map(NamedUrlResource::url)
                .map(this::resourceIdFromUrl)
                .flatMap(evolutionChainId -> webClient.get()
                        .uri("/evolution-chain/{id}", evolutionChainId)
                        .retrieve()
                        .bodyToMono(PokeApiEvolutionChainResponse.class));

        return Mono.zip(pokemonMono, speciesMono, evolutionChainMono)
                .map(tuple -> toDetailDomain(tuple.getT1(), tuple.getT2(), tuple.getT3()))
                .block();
    }

    private Mono<Pokemon> fetchPokemonSummary(String url) {
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(PokeApiPokemonDetailResponse.class)
                .map(this::toDomain);
    }

    private Pokemon toDomain(PokeApiPokemonDetailResponse response) {
        return new Pokemon(
                response.id(),
                response.name(),
                response.height(),
                response.weight(),
                categoryFrom(response),
                spriteUrlFrom(response),
                skillsFrom(response)
        );
    }

    private PokemonDetail toDetailDomain(
            PokeApiPokemonDetailResponse pokemon,
            PokeApiSpeciesResponse species,
            PokeApiEvolutionChainResponse evolutionChain
    ) {
        return new PokemonDetail(
                pokemon.id(),
                pokemon.name(),
                pokemon.height(),
                pokemon.weight(),
                englishGenusFrom(species),
                englishFlavorTextFrom(species),
                statsFrom(pokemon),
                spritesFrom(pokemon),
                skillsFrom(pokemon),
                List.of(toEvolutionNode(evolutionChain.chain(), null))
        );
    }

    private String categoryFrom(PokeApiPokemonDetailResponse response) {
        if (response.types() == null || response.types().isEmpty()) {
            return null;
        }
        return response.types().get(0).type().name();
    }

    private String spriteUrlFrom(PokeApiPokemonDetailResponse response) {
        if (response.sprites() == null) {
            return null;
        }
        return response.sprites().frontDefault();
    }

    private List<String> skillsFrom(PokeApiPokemonDetailResponse response) {
        if (response.abilities() == null) {
            return List.of();
        }
        return response.abilities().stream()
                .map(AbilitySlot::ability)
                .map(NamedApiResource::name)
                .toList();
    }

    private Map<String, Integer> statsFrom(PokeApiPokemonDetailResponse response) {
        if (response.stats() == null) {
            return Map.of();
        }
        return response.stats().stream()
                .filter(statSlot -> statSlot.stat() != null && statSlot.stat().name() != null)
                .collect(Collectors.toMap(
                        statSlot -> statSlot.stat().name(),
                        StatSlot::baseStat
                ));
    }

    private Map<String, String> spritesFrom(PokeApiPokemonDetailResponse response) {
        if (response.sprites() == null) {
            return Map.of();
        }

        return Stream.of(
                        new AbstractMap.SimpleEntry<>("front_default", response.sprites().frontDefault()),
                        new AbstractMap.SimpleEntry<>("back_default", response.sprites().backDefault()),
                        new AbstractMap.SimpleEntry<>("front_shiny", response.sprites().frontShiny()),
                        new AbstractMap.SimpleEntry<>("back_shiny", response.sprites().backShiny())
                )
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private String englishFlavorTextFrom(PokeApiSpeciesResponse response) {
        if (response.flavorTextEntries() == null) {
            return null;
        }
        return response.flavorTextEntries().stream()
                .filter(entry -> isEnglish(entry.language()))
                .map(FlavorTextEntry::flavorText)
                .filter(Objects::nonNull)
                .map(this::normalizeFlavorText)
                .findFirst()
                .orElse(null);
    }

    private String englishGenusFrom(PokeApiSpeciesResponse response) {
        if (response.genera() == null) {
            return null;
        }
        return response.genera().stream()
                .filter(genus -> isEnglish(genus.language()))
                .map(GenusEntry::genus)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    private boolean isEnglish(NamedApiResource language) {
        return language != null && "en".equals(language.name());
    }

    private String normalizeFlavorText(String flavorText) {
        return flavorText.replace('\n', ' ')
                .replace('\f', ' ')
                .trim();
    }

    private EvolutionNode toEvolutionNode(ChainLink chainLink, EvolutionDetail evolutionDetail) {
        return new EvolutionNode(
                chainLink.species().name(),
                Optional.ofNullable(evolutionDetail)
                        .map(EvolutionDetail::minLevel)
                        .orElse(null),
                Optional.ofNullable(evolutionDetail)
                        .map(EvolutionDetail::trigger)
                        .map(NamedApiResource::name)
                        .orElse(null),
                chainLink.evolvesTo().stream()
                        .map(nextLink -> toEvolutionNode(nextLink, firstEvolutionDetailFrom(nextLink)))
                        .toList()
        );
    }

    private EvolutionDetail firstEvolutionDetailFrom(ChainLink chainLink) {
        if (chainLink.evolutionDetails() == null || chainLink.evolutionDetails().isEmpty()) {
            return null;
        }
        return chainLink.evolutionDetails().get(0);
    }

    private String resourceIdFromUrl(String url) {
        String path = URI.create(url).getPath();
        String normalizedPath = path.endsWith("/")
                ? path.substring(0, path.length() - 1)
                : path;
        return normalizedPath.substring(normalizedPath.lastIndexOf('/') + 1);
    }

    private record PokeApiPageResponse(
            long count,
            List<PokeApiListItem> results
    ) {
    }

    private record PokeApiListItem(
            String name,
            String url
    ) {
    }

    private record PokeApiPokemonDetailResponse(
            Long id,
            String name,
            Integer height,
            Integer weight,
            Sprites sprites,
            List<AbilitySlot> abilities,
            List<TypeSlot> types,
            List<StatSlot> stats
    ) {
    }

    private record Sprites(
            @JsonProperty("front_default")
            String frontDefault,
            @JsonProperty("back_default")
            String backDefault,
            @JsonProperty("front_shiny")
            String frontShiny,
            @JsonProperty("back_shiny")
            String backShiny
    ) {
    }

    private record AbilitySlot(
            NamedApiResource ability
    ) {
    }

    private record TypeSlot(
            NamedApiResource type
    ) {
    }

    private record StatSlot(
            @JsonProperty("base_stat")
            Integer baseStat,
            NamedApiResource stat
    ) {
    }

    private record PokeApiSpeciesResponse(
            @JsonProperty("flavor_text_entries")
            List<FlavorTextEntry> flavorTextEntries,
            List<GenusEntry> genera,
            @JsonProperty("evolution_chain")
            NamedUrlResource evolutionChain
    ) {
    }

    private record FlavorTextEntry(
            @JsonProperty("flavor_text")
            String flavorText,
            NamedApiResource language
    ) {
    }

    private record GenusEntry(
            String genus,
            NamedApiResource language
    ) {
    }

    private record NamedUrlResource(
            String url
    ) {
    }

    private record PokeApiEvolutionChainResponse(
            ChainLink chain
    ) {
    }

    private record ChainLink(
            NamedApiResource species,
            @JsonProperty("evolution_details")
            List<EvolutionDetail> evolutionDetails,
            @JsonProperty("evolves_to")
            List<ChainLink> evolvesTo
    ) {
        private ChainLink {
            evolvesTo = evolvesTo == null ? List.of() : evolvesTo;
        }
    }

    private record EvolutionDetail(
            @JsonProperty("min_level")
            Integer minLevel,
            NamedApiResource trigger
    ) {
    }

    private record NamedApiResource(
            String name
    ) {
    }
}
