import { useState, type FormEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import { axiosClient } from '../api/axiosClient';
import { useAuth } from '../context/AuthContext';

type AuthMode = 'login' | 'register';

interface AuthResponse {
  token: string;
}

export function LoginPage() {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [mode, setMode] = useState<AuthMode>('login');
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError(null);
    setIsSubmitting(true);

    try {
      if (mode === 'register') {
        await axiosClient.post('/auth/register', { username, password });
      }

      const response = await axiosClient.post<AuthResponse>('/auth/login', { username, password });
      login(response.data.token);
      navigate('/');
    } catch {
      setError(mode === 'login' ? 'Invalid username or password.' : 'Unable to register this user.');
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <section className="mx-auto flex min-h-screen w-full max-w-sm flex-col justify-center px-6">
      <h1 className="text-2xl font-semibold">Pokemon Access</h1>

      <div className="mt-6 grid grid-cols-2 rounded border border-zinc-300 bg-white p-1">
        <button
          className={`rounded px-3 py-2 text-sm font-medium ${mode === 'login' ? 'bg-zinc-950 text-white' : 'text-zinc-600'}`}
          type="button"
          onClick={() => setMode('login')}
        >
          Login
        </button>
        <button
          className={`rounded px-3 py-2 text-sm font-medium ${mode === 'register' ? 'bg-zinc-950 text-white' : 'text-zinc-600'}`}
          type="button"
          onClick={() => setMode('register')}
        >
          Register
        </button>
      </div>

      <form className="mt-4 grid gap-3" onSubmit={handleSubmit}>
        <input
          className="rounded border border-zinc-300 bg-white px-3 py-2"
          placeholder="Username"
          value={username}
          onChange={(event) => setUsername(event.target.value)}
        />
        <input
          className="rounded border border-zinc-300 bg-white px-3 py-2"
          placeholder="Password"
          type="password"
          value={password}
          onChange={(event) => setPassword(event.target.value)}
        />
        {error && <p className="text-sm text-red-600">{error}</p>}
        <button
          className="rounded bg-zinc-950 px-4 py-2 font-medium text-white disabled:cursor-not-allowed disabled:bg-zinc-400"
          disabled={isSubmitting || !username || !password}
          type="submit"
        >
          {isSubmitting ? 'Working...' : mode === 'login' ? 'Login' : 'Register'}
        </button>
      </form>
    </section>
  );
}
