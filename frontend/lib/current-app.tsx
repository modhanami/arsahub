"use client";
import { useQueryClient } from "@tanstack/react-query";
import { toast } from "../components/ui/use-toast";
import { AppResponse } from "../types/generated-types";
import React, { createContext, useContext, useEffect, useState } from "react";
import { useAppByAPIKey } from "@/hooks";

export function useCurrentApp(): {
  currentApp: AppResponse | undefined;
  isLoading: boolean;
  clearCurrentApp: () => void;
} {
  const { apiKey, clearApiKey, isLoading: isAppApiKeyLoading } = useAppApiKey();
  const queryClient = useQueryClient();
  const { data: currentApp, isLoading, error } = useAppByAPIKey(apiKey);

  useEffect(() => {
    if (error) {
      toast({
        title: "Invalid API key",
        description: error.message,
        variant: "destructive",
      });
      clearApiKey();
    }
  }, [clearApiKey, error]);

  const clearCurrentApp = () => {
    // TODO: clear from queryClient?
    queryClient.removeQueries({ queryKey: ["app"] });
    clearApiKey();
  };

  return {
    currentApp,
    isLoading: isLoading || isAppApiKeyLoading,
    clearCurrentApp,
  };
}

export interface AppApiKeyContextType {
  apiKey: string | null;
  updateApiKey: (newApiKey: string) => void;
  clearApiKey: () => void;
  isLoading: boolean;
}

const AppApiKeyContext = createContext<AppApiKeyContextType | undefined>(
  undefined,
);

export function AppApiKeyProvider({ children }: { children: React.ReactNode }) {
  const [apiKey, setApiKey] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);

  useEffect(() => {
    setApiKey(localStorage.getItem("app-api-key"));
    setIsLoading(false);
  }, []);

  const updateApiKey = (newApiKey: string) => {
    localStorage.setItem("app-api-key", newApiKey);
    setApiKey(newApiKey);
  };

  const clearApiKey = () => {
    localStorage.removeItem("app-api-key");
    setApiKey(null);
  };

  return (
    <AppApiKeyContext.Provider
      value={{ apiKey, updateApiKey, clearApiKey, isLoading }}
    >
      {children}
    </AppApiKeyContext.Provider>
  );
}

export const useAppApiKey = (): AppApiKeyContextType => {
  const context = useContext(AppApiKeyContext);
  if (!context) {
    throw new Error("useApiKey must be used within a ApiKeyProvider");
  }
  return context;
};
