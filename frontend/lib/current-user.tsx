"use client";
import React, { createContext, useContext, useEffect, useState } from "react";
import { useCurrentApp } from "@/lib/current-app";
import { useUser } from "@/hooks";
import { isApiError, loginUser, logoutUser, refreshAccessToken } from "@/api";
import { UserResponseWithAccessToken } from "@/types";

export function useCurrentUser() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useCurrentUser must be used within an AuthProvider");
  }
  return context;
}

export interface AuthContextType {
  login: (email: string, password: string) => Promise<void>;
  logout: () => Promise<void>;
  isLoading: boolean;
  currentUser: UserResponseWithAccessToken | null;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [accessToken, setAccessToken] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const { clearCurrentApp } = useCurrentApp();
  const {
    data: currentUser,
    isLoading: isUserLoading,
    error: errorUser,
  } = useUser(accessToken);

  useEffect(() => {
    async function init() {
      console.log("[AuthProvider] init");
      try {
        const response = await refreshAccessToken();
        setAccessToken(response.accessToken);
        console.log("[AuthProvider] init success");
      } catch (error) {
        console.log("[AuthProvider] init error", error);
      } finally {
        setIsLoading(false);
      }
    }

    init().then(() => {});
  }, []);

  useEffect(() => {
    if (!errorUser) {
      return;
    }

    if (!isApiError(errorUser)) {
      console.log("[AuthProvider] errorUser is not ApiError", errorUser);
      return;
    } else {
      console.log("[AuthProvider] errorUser is ApiError", errorUser);
    }

    if (errorUser.response?.status === 401) {
      console.log("[AuthProvider] errorUser 401");
      refreshAccessToken().then((response) => {
        setAccessToken(response.accessToken);
      });
      return;
    }
  }, [errorUser]);

  async function login(email: string, password: string) {
    const { accessToken } = await loginUser(email, password);
    setAccessToken(accessToken);
    console.log("[AuthProvider] login success");
  }

  async function logout() {
    await logoutUser();
    setAccessToken(null);
    clearCurrentApp();
  }

  return (
    <AuthContext.Provider
      value={{
        currentUser: currentUser ?? null,
        login,
        logout,
        isLoading: isLoading || isUserLoading,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}
