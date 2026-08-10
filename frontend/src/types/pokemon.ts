export interface PokemonSummary {
  id: number;
  name: string;
  height: number;
  weight: number;
  category: string;
  spriteUrl: string;
  skills: string[];
}

export interface PokemonPage {
  items: PokemonSummary[];
  totalElements: number;
  pageNumber: number;
  pageSize: number;
}

export interface EvolutionNode {
  speciesName: string;
  minLevel: number | null;
  trigger: string | null;
  evolvesTo: EvolutionNode[];
}

export interface PokemonDetail extends PokemonSummary {
  description: string;
  stats: Record<string, number>;
  sprites: Record<string, string>;
  evolutionChain: EvolutionNode[];
}

export interface PokemonLocal {
  id: number;
  name: string;
  height: number;
  weight: number;
  category: string;
  spriteUrl: string;
  localizedName: string | null;
  locationMetadata: string | null;
  tags: string[];
}
