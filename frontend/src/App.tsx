import { Navigate, NavLink, Route, BrowserRouter as Router, Routes } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import { LocalPokemonManagementPage } from './pages/LocalPokemonManagementPage';
import { LoginPage } from './pages/LoginPage';
import { PokemonDetailPage } from './pages/PokemonDetailPage';
import { PokemonListPage } from './pages/PokemonListPage';

export function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="min-h-screen bg-zinc-50 text-zinc-950">
          <AppNavigation />
          <main>
            <Routes>
              <Route path="/login" element={<LoginPage />} />
              <Route path="/" element={<PokemonListPage />} />
              <Route path="/pokemon/:idOrName" element={<PokemonDetailPage />} />
              <Route
                path="/local"
                element={
                  <ProtectedRoute>
                    <LocalPokemonManagementPage />
                  </ProtectedRoute>
                }
              />
            </Routes>
          </main>
        </div>
      </Router>
    </AuthProvider>
  );
}

function AppNavigation() {
  const { isAuthenticated, logout } = useAuth();

  return (
    <header className="border-b border-zinc-200 bg-white">
      <nav className="mx-auto flex max-w-7xl items-center justify-between px-6 py-4">
        <NavLink className="font-semibold" to="/">
          Pokemon
        </NavLink>
        <div className="flex items-center gap-3 text-sm">
          {isAuthenticated && (
            <NavLink className={({ isActive }) => (isActive ? 'font-medium text-zinc-950' : 'text-zinc-600')} to="/local">
              Local DB
            </NavLink>
          )}
          {isAuthenticated ? (
            <button className="rounded border border-zinc-300 px-3 py-2" onClick={logout}>
              Logout
            </button>
          ) : (
            <NavLink className="rounded bg-zinc-950 px-3 py-2 text-white" to="/login">
              Login
            </NavLink>
          )}
        </div>
      </nav>
    </header>
  );
}

function ProtectedRoute({ children }: { children: JSX.Element }) {
  const { isAuthenticated } = useAuth();

  if (!isAuthenticated) {
    return <Navigate replace to="/login" />;
  }

  return children;
}
