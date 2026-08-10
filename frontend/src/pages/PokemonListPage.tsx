import { Search } from 'lucide-react';
import { useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import { axiosClient } from '../api/axiosClient';
import type { PokemonPage, PokemonSummary } from '../types/pokemon';

const PAGE_SIZES = [12, 20, 40];

export function PokemonListPage() {
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(20);
  const [search, setSearch] = useState('');
  const [pokemonPage, setPokemonPage] = useState<PokemonPage | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let isCurrent = true;

    async function loadPokemon() {
      setIsLoading(true);
      setError(null);

      try {
        const response = await axiosClient.get<PokemonPage>('/pokemon', {
          params: { page, size: pageSize },
        });

        if (isCurrent) {
          setPokemonPage(response.data);
        }
      } catch {
        if (isCurrent) {
          setError('Unable to load Pokemon.');
        }
      } finally {
        if (isCurrent) {
          setIsLoading(false);
        }
      }
    }

    loadPokemon();

    return () => {
      isCurrent = false;
    };
  }, [page, pageSize]);

  const filteredItems = useMemo(() => {
    const items = pokemonPage?.items ?? [];
    const normalizedSearch = search.trim().toLowerCase();

    if (!normalizedSearch) {
      return items;
    }

    return items.filter((pokemon) => pokemon.name.toLowerCase().includes(normalizedSearch));
  }, [pokemonPage?.items, search]);

  const totalPages = pokemonPage ? Math.max(1, Math.ceil(pokemonPage.totalElements / pokemonPage.pageSize)) : 1;

  return (
    <section className="mx-auto w-full max-w-7xl px-6 py-8">
      <div className="flex flex-col gap-4 border-b border-zinc-200 pb-5 md:flex-row md:items-end md:justify-between">
        <div>
          <h1 className="text-2xl font-semibold">Pokemon</h1>
          <p className="mt-1 text-sm text-zinc-600">{pokemonPage?.totalElements ?? 0} discovered entries</p>
        </div>

        <div className="flex flex-col gap-3 sm:flex-row">
          <label className="relative block">
            <Search className="pointer-events-none absolute left-3 top-2.5 h-4 w-4 text-zinc-400" />
            <input
              className="w-full rounded border border-zinc-300 bg-white py-2 pl-9 pr-3 sm:w-64"
              placeholder="Filter by name"
              value={search}
              onChange={(event) => setSearch(event.target.value)}
            />
          </label>
          <select
            className="rounded border border-zinc-300 bg-white px-3 py-2"
            value={pageSize}
            onChange={(event) => {
              setPage(0);
              setPageSize(Number(event.target.value));
            }}
          >
            {PAGE_SIZES.map((size) => (
              <option key={size} value={size}>
                {size} per page
              </option>
            ))}
          </select>
        </div>
      </div>

      {error && <p className="mt-6 text-sm text-red-600">{error}</p>}
      {isLoading && <p className="mt-6 text-sm text-zinc-600">Loading Pokemon...</p>}

      {!isLoading && !error && (
        <div className="mt-6 grid gap-4 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
          {filteredItems.map((pokemon) => (
            <PokemonListCard key={pokemon.id} pokemon={pokemon} />
          ))}
        </div>
      )}

      <div className="mt-8 flex flex-col gap-3 border-t border-zinc-200 pt-5 sm:flex-row sm:items-center sm:justify-between">
        <p className="text-sm text-zinc-600">
          Page {page + 1} of {totalPages}
        </p>
        <div className="flex gap-2">
          <button
            className="rounded border border-zinc-300 bg-white px-4 py-2 disabled:cursor-not-allowed disabled:text-zinc-400"
            disabled={page === 0 || isLoading}
            onClick={() => setPage((currentPage) => Math.max(0, currentPage - 1))}
          >
            Previous
          </button>
          <button
            className="rounded border border-zinc-300 bg-white px-4 py-2 disabled:cursor-not-allowed disabled:text-zinc-400"
            disabled={page + 1 >= totalPages || isLoading}
            onClick={() => setPage((currentPage) => currentPage + 1)}
          >
            Next
          </button>
        </div>
      </div>
    </section>
  );
}

function PokemonListCard({ pokemon }: { pokemon: PokemonSummary }) {
  return (
    <Link className="rounded border border-zinc-200 bg-white p-4 transition hover:border-zinc-400" to={`/pokemon/${pokemon.name}`}>
      <img className="aspect-square w-full object-contain" src={pokemon.spriteUrl} alt={pokemon.name} />
      <div className="mt-3 flex items-start justify-between gap-3">
        <div>
          <h2 className="font-medium capitalize">{pokemon.name}</h2>
          <p className="text-sm text-zinc-600 capitalize">{pokemon.category}</p>
        </div>
        <span className="rounded bg-zinc-100 px-2 py-1 text-xs text-zinc-600">#{pokemon.id}</span>
      </div>
      <dl className="mt-3 grid grid-cols-2 gap-2 text-sm">
        <div>
          <dt className="text-zinc-500">Height</dt>
          <dd className="font-medium">{pokemon.height}</dd>
        </div>
        <div>
          <dt className="text-zinc-500">Weight</dt>
          <dd className="font-medium">{pokemon.weight}</dd>
        </div>
      </dl>
      <p className="mt-3 text-sm text-zinc-600">{pokemon.skills.join(', ')}</p>
    </Link>
  );
}
