package com.ballastlane.pokemon.domain.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class PokemonLocal {
    private final Long id;
    private final String name;
    private final Integer height;
    private final Integer weight;
    private final String category;
    private final String spriteUrl;
    private final String localizedName;
    private final String locationMetadata;
    private final Set<String> tags;

    public PokemonLocal(
            Long id,
            String name,
            Integer height,
            Integer weight,
            String category,
            String spriteUrl,
            String localizedName,
            String locationMetadata,
            Set<String> tags
    ) {
        this.id = id;
        this.name = name;
        this.height = height;
        this.weight = weight;
        this.category = category;
        this.spriteUrl = spriteUrl;
        this.localizedName = localizedName;
        this.locationMetadata = locationMetadata;
        this.tags = tags == null ? Set.of() : Collections.unmodifiableSet(new HashSet<>(tags));
    }

    private PokemonLocal(Builder builder) {
        this(
                builder.id,
                builder.name,
                builder.height,
                builder.weight,
                builder.category,
                builder.spriteUrl,
                builder.localizedName,
                builder.locationMetadata,
                builder.tags
        );
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getHeight() {
        return height;
    }

    public Integer getWeight() {
        return weight;
    }

    public String getCategory() {
        return category;
    }

    public String getSpriteUrl() {
        return spriteUrl;
    }

    public String getLocalizedName() {
        return localizedName;
    }

    public String getLocationMetadata() {
        return locationMetadata;
    }

    public Set<String> getTags() {
        return tags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PokemonLocal that)) {
            return false;
        }
        return Objects.equals(id, that.id)
                && Objects.equals(name, that.name)
                && Objects.equals(height, that.height)
                && Objects.equals(weight, that.weight)
                && Objects.equals(category, that.category)
                && Objects.equals(spriteUrl, that.spriteUrl)
                && Objects.equals(localizedName, that.localizedName)
                && Objects.equals(locationMetadata, that.locationMetadata)
                && Objects.equals(tags, that.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, height, weight, category, spriteUrl, localizedName, locationMetadata, tags);
    }

    public static class Builder {
        private Long id;
        private String name;
        private Integer height;
        private Integer weight;
        private String category;
        private String spriteUrl;
        private String localizedName;
        private String locationMetadata;
        private Set<String> tags = Set.of();

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder height(Integer height) {
            this.height = height;
            return this;
        }

        public Builder weight(Integer weight) {
            this.weight = weight;
            return this;
        }

        public Builder category(String category) {
            this.category = category;
            return this;
        }

        public Builder spriteUrl(String spriteUrl) {
            this.spriteUrl = spriteUrl;
            return this;
        }

        public Builder localizedName(String localizedName) {
            this.localizedName = localizedName;
            return this;
        }

        public Builder locationMetadata(String locationMetadata) {
            this.locationMetadata = locationMetadata;
            return this;
        }

        public Builder tags(Set<String> tags) {
            this.tags = tags;
            return this;
        }

        public PokemonLocal build() {
            return new PokemonLocal(this);
        }
    }
}
