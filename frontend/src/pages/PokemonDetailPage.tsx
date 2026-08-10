import { GitBranch, Save } from 'lucide-react';
import { useEffect, useMemo, useState } from 'react';
import { useParams } from 'react-router-dom';
import { axiosClient } from '../api/axiosClient';
import { useAuth } from '../context/AuthContext';
import type { EvolutionNode, PokemonDetail } from '../types/pokemon';

const PRIMARY_STATS = ['hp', 'attack', 'defense'];

export function PokemonDetailPage() {
  const { idOrName } = useParams<{ idOrName: string }>();
  const { isAuthenticated } = useAuth();
  const [pokemon, setPokemon] = useState<PokemonDetail | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isSyncing, setIsSyncing] = useState(false);
  const [message, setMessage] = useState<string | null>(null);

  useEffect(() => {
    let isCurrent = true;

    async function loadPokemonDetail() {
      if (!idOrName) {
        return;
      }

      setIsLoading(true);
      setMessage(null);

      try {
        const response = await axiosClient.get<PokemonDetail>(`/pokemon/${idOrName}`);

        if (isCurrent) {
          setPokemon(response.data);
        }
      } catch {
        if (isCurrent) {
          setMessage('Unable to load Pokemon detail.');
        }
      } finally {
        if (isCurrent) {
          setIsLoading(false);
        }
      }
    }

    loadPokemonDetail();

    return () => {
      isCurrent = false;
    };
  }, [idOrName]);

  const spriteEntries = useMemo(() => Object.entries(pokemon?.sprites ?? {}).filter(([, url]) => Boolean(url)), [pokemon?.sprites]);

  async function syncToLocal() {
    if (!pokemon) {
      return;
    }

    setIsSyncing(true);
    setMessage(null);

    try {
      await axiosClient.post(`/pokemon/sync/${pokemon.name}`, {
        localizedName: pokemon.name,
        locationMetadata: '',
        tags: [pokemon.category],
      });
      setMessage('Pokemon synced to local DB.');
    } catch {
      setMessage('Unable to sync Pokemon.');
    } finally {
      setIsSyncing(false);
    }
  }

  if (isLoading) {
    return <p className="px-6 py-8 text-sm text-zinc-600">Loading Pokemon detail...</p>;
  }

  if (!pokemon) {
    return <p className="px-6 py-8 text-sm text-red-600">{message ?? 'Pokemon not found.'}</p>;
  }

  return (
    <section className="mx-auto w-full max-w-6xl px-6 py-8">
      <div className="grid gap-8 lg:grid-cols-[320px_1fr]">
        <div>
          <img className="aspect-square w-full object-contain" src={pokemon.spriteUrl} alt={pokemon.name} />
          {isAuthenticated && (
            <button
              className="mt-4 inline-flex w-full items-center justify-center gap-2 rounded bg-zinc-950 px-4 py-2 font-medium text-white disabled:bg-zinc-400"
              disabled={isSyncing}
              onClick={syncToLocal}
            >
              <Save className="h-4 w-4" />
              {isSyncing ? 'Syncing...' : 'Sync to Local DB'}
            </button>
          )}
          {message && <p className="mt-3 text-sm text-zinc-600">{message}</p>}
        </div>

        <div>
          <div className="flex flex-wrap items-center gap-3">
            <h1 className="text-3xl font-semibold capitalize">{pokemon.name}</h1>
            <span className="rounded bg-zinc-100 px-2 py-1 text-sm text-zinc-600">#{pokemon.id}</span>
            <span className="rounded bg-emerald-100 px-2 py-1 text-sm capitalize text-emerald-800">{pokemon.category}</span>
          </div>
          <p className="mt-4 max-w-2xl leading-7 text-zinc-700">{pokemon.description}</p>

          <div className="mt-8 grid gap-6 lg:grid-cols-2">
            <section>
              <h2 className="text-lg font-semibold">Stats</h2>
              <div className="mt-4 grid gap-3">
                {PRIMARY_STATS.map((stat) => (
                  <StatBar key={stat} label={stat} value={pokemon.stats[stat] ?? 0} />
                ))}
              </div>
            </section>

            <section>
              <h2 className="text-lg font-semibold">Sprites</h2>
              <div className="mt-4 grid grid-cols-2 gap-3 sm:grid-cols-4">
                {spriteEntries.map(([name, url]) => (
                  <div className="rounded border border-zinc-200 bg-white p-3" key={name}>
                    <img className="aspect-square w-full object-contain" src={url} alt={name} />
                    <p className="mt-2 text-center text-xs text-zinc-500">{name}</p>
                  </div>
                ))}
              </div>
            </section>
          </div>

          <section className="mt-8">
            <div className="flex items-center gap-2">
              <GitBranch className="h-5 w-5 text-zinc-500" />
              <h2 className="text-lg font-semibold">Evolutionary Lineage</h2>
            </div>
            <div className="mt-4 grid gap-3">
              {pokemon.evolutionChain.map((node) => (
                <EvolutionTree key={node.speciesName} node={node} />
              ))}
            </div>
          </section>
        </div>
      </div>
    </section>
  );
}

function StatBar({ label, value }: { label: string; value: number }) {
  const width = Math.min(100, Math.round((value / 160) * 100));

  return (
    <div>
      <div className="flex justify-between text-sm">
        <span className="capitalize text-zinc-600">{label.replace('-', ' ')}</span>
        <span className="font-medium">{value}</span>
      </div>
      <div className="mt-1 h-2 rounded bg-zinc-100">
        <div className="h-2 rounded bg-emerald-500" style={{ width: `${width}%` }} />
      </div>
    </div>
  );
}

function EvolutionTree({ node }: { node: EvolutionNode }) {
  return (
    <div className="rounded border border-zinc-200 bg-white p-3">
      <div className="flex flex-wrap items-center gap-2">
        <span className="font-medium capitalize">{node.speciesName}</span>
        {node.trigger && <span className="rounded bg-zinc-100 px-2 py-1 text-xs text-zinc-600">{node.trigger}</span>}
        {node.minLevel && <span className="rounded bg-zinc-100 px-2 py-1 text-xs text-zinc-600">Lv. {node.minLevel}</span>}
      </div>
      {node.evolvesTo.length > 0 && (
        <div className="ml-4 mt-3 grid gap-3 border-l border-zinc-200 pl-4">
          {node.evolvesTo.map((child) => (
            <EvolutionTree key={child.speciesName} node={child} />
          ))}
        </div>
      )}
    </div>
  );
}
