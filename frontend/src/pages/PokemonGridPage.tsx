import { PokemonCard } from '../components/PokemonCard';
import type { PokemonSummary } from '../types/pokemon';

const samplePokemon: PokemonSummary = {
  id: 25,
  name: 'pikachu',
  height: 4,
  weight: 60,
  category: 'electric',
  spriteUrl: 'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/25.png',
  skills: ['static', 'lightning-rod'],
};

export function PokemonGridPage() {
  return (
    <section className="border-b border-zinc-200 px-6 py-8">
      <h2 className="text-xl font-semibold">Pokemon</h2>
      <div className="mt-4 grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <PokemonCard pokemon={samplePokemon} />
      </div>
    </section>
  );
}
