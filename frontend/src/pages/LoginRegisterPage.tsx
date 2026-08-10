export function LoginRegisterPage() {
  return (
    <section className="border-b border-zinc-200 px-6 py-8">
      <h1 className="text-2xl font-semibold">Pokemon Access</h1>
      <div className="mt-4 grid gap-3 sm:max-w-sm">
        <input className="rounded border border-zinc-300 px-3 py-2" placeholder="Username" />
        <input className="rounded border border-zinc-300 px-3 py-2" placeholder="Password" type="password" />
        <div className="flex gap-2">
          <button className="rounded bg-zinc-950 px-4 py-2 text-white">Login</button>
          <button className="rounded border border-zinc-300 px-4 py-2">Register</button>
        </div>
      </div>
    </section>
  );
}
