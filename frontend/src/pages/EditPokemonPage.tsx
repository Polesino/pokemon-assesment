export function EditPokemonPage() {
  return (
    <section className="px-6 py-8">
      <h2 className="text-xl font-semibold">Edit Local Fields</h2>
      <form className="mt-4 grid gap-3 sm:max-w-lg">
        <input className="rounded border border-zinc-300 px-3 py-2" placeholder="Localized name" />
        <textarea className="min-h-24 rounded border border-zinc-300 px-3 py-2" placeholder="Location metadata" />
        <input className="rounded border border-zinc-300 px-3 py-2" placeholder="Tags" />
        <button className="w-fit rounded bg-zinc-950 px-4 py-2 text-white">Save</button>
      </form>
    </section>
  );
}
