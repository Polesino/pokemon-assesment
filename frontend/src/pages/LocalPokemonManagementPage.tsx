import { Edit2, Trash2 } from 'lucide-react';
import { useEffect, useState, type FormEvent } from 'react';
import { axiosClient } from '../api/axiosClient';
import type { PokemonLocal } from '../types/pokemon';

interface LocalPokemonFormState {
  localizedName: string;
  locationMetadata: string;
  tags: string;
}

export function LocalPokemonManagementPage() {
  const [pokemon, setPokemon] = useState<PokemonLocal[]>([]);
  const [selectedPokemon, setSelectedPokemon] = useState<PokemonLocal | null>(null);
  const [formState, setFormState] = useState<LocalPokemonFormState>({
    localizedName: '',
    locationMetadata: '',
    tags: '',
  });
  const [message, setMessage] = useState<string | null>(null);

  useEffect(() => {
    loadLocalPokemon();
  }, []);

  async function loadLocalPokemon() {
    try {
      const response = await axiosClient.get<PokemonLocal[]>('/pokemon/local');
      setPokemon(response.data);
    } catch {
      setMessage('Unable to load local Pokemon.');
    }
  }

  function openEditor(nextPokemon: PokemonLocal) {
    setSelectedPokemon(nextPokemon);
    setFormState({
      localizedName: nextPokemon.localizedName ?? '',
      locationMetadata: nextPokemon.locationMetadata ?? '',
      tags: nextPokemon.tags.join(', '),
    });
  }

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    if (!selectedPokemon) {
      return;
    }

    try {
      const response = await axiosClient.put<PokemonLocal>(`/pokemon/local/${selectedPokemon.id}`, {
        localizedName: formState.localizedName || null,
        locationMetadata: formState.locationMetadata || null,
        tags: tagsFromInput(formState.tags),
      });
      setPokemon((items) => items.map((item) => (item.id === response.data.id ? response.data : item)));
      setSelectedPokemon(null);
      setMessage('Local Pokemon updated.');
    } catch {
      setMessage('Unable to update local Pokemon.');
    }
  }

  async function deletePokemon(id: number) {
    try {
      await axiosClient.delete(`/pokemon/local/${id}`);
      setPokemon((items) => items.filter((item) => item.id !== id));
      setMessage('Local Pokemon deleted.');
    } catch {
      setMessage('Unable to delete local Pokemon.');
    }
  }

  return (
    <section className="mx-auto w-full max-w-6xl px-6 py-8">
      <div className="border-b border-zinc-200 pb-5">
        <h1 className="text-2xl font-semibold">Local Pokemon</h1>
        <p className="mt-1 text-sm text-zinc-600">Manage synchronized records and proprietary metadata.</p>
      </div>

      {message && <p className="mt-4 text-sm text-zinc-600">{message}</p>}

      <div className="mt-6 overflow-hidden rounded border border-zinc-200 bg-white">
        <table className="w-full min-w-[760px] text-left text-sm">
          <thead className="bg-zinc-100 text-zinc-600">
            <tr>
              <th className="px-4 py-3">Pokemon</th>
              <th className="px-4 py-3">Localized Name</th>
              <th className="px-4 py-3">Location</th>
              <th className="px-4 py-3">Tags</th>
              <th className="px-4 py-3 text-right">Actions</th>
            </tr>
          </thead>
          <tbody>
            {pokemon.map((item) => (
              <tr className="border-t border-zinc-200" key={item.id}>
                <td className="px-4 py-3">
                  <div className="flex items-center gap-3">
                    <img className="h-12 w-12 object-contain" src={item.spriteUrl} alt={item.name} />
                    <div>
                      <p className="font-medium capitalize">{item.name}</p>
                      <p className="text-xs text-zinc-500">#{item.id}</p>
                    </div>
                  </div>
                </td>
                <td className="px-4 py-3">{item.localizedName || '-'}</td>
                <td className="px-4 py-3">{item.locationMetadata || '-'}</td>
                <td className="px-4 py-3">{item.tags.join(', ') || '-'}</td>
                <td className="px-4 py-3">
                  <div className="flex justify-end gap-2">
                    <button className="rounded border border-zinc-300 p-2" onClick={() => openEditor(item)} title="Edit">
                      <Edit2 className="h-4 w-4" />
                    </button>
                    <button className="rounded border border-zinc-300 p-2 text-red-600" onClick={() => deletePokemon(item.id)} title="Delete">
                      <Trash2 className="h-4 w-4" />
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {selectedPokemon && (
        <div className="fixed inset-0 z-10 flex items-center justify-center bg-black/30 px-4">
          <form className="w-full max-w-lg rounded bg-white p-5 shadow-xl" onSubmit={handleSubmit}>
            <h2 className="text-lg font-semibold capitalize">Edit {selectedPokemon.name}</h2>
            <div className="mt-4 grid gap-3">
              <input
                className="rounded border border-zinc-300 px-3 py-2"
                placeholder="Localized name"
                value={formState.localizedName}
                onChange={(event) => setFormState((current) => ({ ...current, localizedName: event.target.value }))}
              />
              <textarea
                className="min-h-28 rounded border border-zinc-300 px-3 py-2"
                placeholder="Location metadata"
                value={formState.locationMetadata}
                onChange={(event) => setFormState((current) => ({ ...current, locationMetadata: event.target.value }))}
              />
              <input
                className="rounded border border-zinc-300 px-3 py-2"
                placeholder="Tags"
                value={formState.tags}
                onChange={(event) => setFormState((current) => ({ ...current, tags: event.target.value }))}
              />
            </div>
            <div className="mt-5 flex justify-end gap-2">
              <button className="rounded border border-zinc-300 px-4 py-2" type="button" onClick={() => setSelectedPokemon(null)}>
                Cancel
              </button>
              <button className="rounded bg-zinc-950 px-4 py-2 text-white" type="submit">
                Save
              </button>
            </div>
          </form>
        </div>
      )}
    </section>
  );
}

function tagsFromInput(input: string) {
  return input
    .split(',')
    .map((tag) => tag.trim())
    .filter(Boolean);
}
