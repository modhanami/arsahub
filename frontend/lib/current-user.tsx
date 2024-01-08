"use client";
import { toast } from "../components/ui/use-toast";
import React, { createContext, useContext, useEffect, useState } from "react";
import { useCurrentApp } from "@/lib/current-app";
import { useUser } from "@/hooks";
import { isApiError, loginUser } from "@/api";

export function useCurrentUser() {
  const { isLoading: isAuthLoading, logout, accessToken } = useAuth();

  const { data: currentUser, isLoading, error } = useUser(accessToken);

  useEffect(() => {
    if (isApiError(error)) {
      toast({
        title: "Invalid credentials",
        description: error.message,
        variant: "destructive",
      });
      logout();
    }
  }, [logout, error]);

  return {
    currentUser,
    isLoading: isLoading || isAuthLoading,
  };
}

export interface AuthContextType {
  accessToken: string | null;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
  isLoading: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [accessToken, setAccessToken] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const { clearCurrentApp } = useCurrentApp();

  useEffect(() => {
    setAccessToken(localStorage.getItem("access-token"));
    setIsLoading(false);
  }, []);

  async function login(email: string, password: string) {
    const { accessToken } = await loginUser(email, password);
    setAccessToken(accessToken);
    localStorage.setItem("access-token", accessToken);
  }

  function logout() {
    setAccessToken(null);
    localStorage.removeItem("access-token");
    clearCurrentApp();
  }

  return (
    <AuthContext.Provider
      value={{
        accessToken,
        login,
        logout,
        isLoading,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
};
