import type { PokemonSummary } from '../types/pokemon';

interface PokemonCardProps {
  pokemon: PokemonSummary;
}

export function PokemonCard({ pokemon }: PokemonCardProps) {
  return (
    <article className="rounded border border-zinc-200 bg-white p-4">
      <img className="aspect-square w-full object-contain" src={pokemon.spriteUrl} alt={pokemon.name} />
      <h3 className="mt-3 font-medium capitalize">{pokemon.name}</h3>
      <p className="text-sm text-zinc-600">Mass: {pokemon.weight}</p>
      <p className="text-sm text-zinc-600">Skills: {pokemon.skills.join(', ')}</p>
    </article>
  );
}
