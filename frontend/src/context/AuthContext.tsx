import { createContext, useCallback, useContext, useMemo, useState, type ReactNode } from 'react';
import { AUTH_TOKEN_STORAGE_KEY } from '../api/axiosClient';

interface AuthUser {
  username: string;
  role?: string;
}

interface AuthContextValue {
  user: AuthUser | null;
  token: string | null;
  login: (token: string) => void;
  logout: () => void;
  isAuthenticated: boolean;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

interface AuthProviderProps {
  children: ReactNode;
}

export function AuthProvider({ children }: AuthProviderProps) {
  const [token, setToken] = useState<string | null>(() => localStorage.getItem(AUTH_TOKEN_STORAGE_KEY));

  const login = useCallback((nextToken: string) => {
    localStorage.setItem(AUTH_TOKEN_STORAGE_KEY, nextToken);
    setToken(nextToken);
  }, []);

  const logout = useCallback(() => {
    localStorage.removeItem(AUTH_TOKEN_STORAGE_KEY);
    setToken(null);
  }, []);

  const value = useMemo<AuthContextValue>(() => {
    const user = token ? userFromToken(token) : null;

    return {
      user,
      token,
      login,
      logout,
      isAuthenticated: Boolean(token),
    };
  }, [login, logout, token]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);

  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }

  return context;
}

function userFromToken(token: string): AuthUser | null {
  try {
    const [, payload] = token.split('.');
    const decodedPayload = JSON.parse(atob(payload)) as { sub?: string; role?: string };

    if (!decodedPayload.sub) {
      return null;
    }

    return {
      username: decodedPayload.sub,
      role: decodedPayload.role,
    };
  } catch {
    return null;
  }
}
