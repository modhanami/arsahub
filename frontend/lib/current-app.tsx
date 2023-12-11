"use client";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { toast } from "../components/ui/use-toast";
import { API_URL } from "../hooks/api";
import { AppResponse } from "../types/generated-types";
import React, { createContext, useContext, useState } from "react";

async function fetchAppByApiKey(apiKey: string): Promise<AppResponse> {
  const response = await fetch(`${API_URL}/apps/current`, {
    headers: {
      Authorization: `Bearer ${apiKey}`,
    },
  });
  if (!response.ok) {
    throw new Error("Invalid API key");
  }
  return response.json();
}

export function useCurrentApp(): {
  currentApp: AppResponse | undefined;
  setCurrentAppWithApiKey: (apiKey: string) => void;
  isLoading: boolean;
  clearCurrentApp: () => void;
} {
  const { apiKey, updateApiKey, clearApiKey } = useApiKey();
  const queryClient = useQueryClient();

  const { data: currentApp, isLoading } = useQuery<AppResponse, Error>({
    queryKey: ["currentApp"],
    queryFn: () => fetchAppByApiKey(apiKey!!),
    enabled: !!apiKey,
  });

  const { mutate: setCurrentAppWithApiKey } = useMutation({
    mutationFn: (newApiKey: string) => fetchAppByApiKey(newApiKey),
    onSuccess: (app) => {
      queryClient.setQueryData(["currentApp"], app);
    },
    onError: (error: Error) => {
      toast({
        title: "Invalid API key",
        description: error.message,
        variant: "destructive",
      });
    },
  });

  const clearCurrentApp = () => {
    clearApiKey();
    queryClient.removeQueries({ queryKey: ["currentApp"] });
  };

  return {
    currentApp,
    setCurrentAppWithApiKey,
    isLoading,
    clearCurrentApp,
  };
}

export interface AppApiKeyContextType {
  apiKey: string | null;
  updateApiKey: (newApiKey: string) => void;
  clearApiKey: () => void;
}

const AppApiKeyContext = createContext<AppApiKeyContextType | undefined>(
  undefined,
);

export function AppApiKeyProvider({ children }: { children: React.ReactNode }) {
  const [apiKey, setApiKey] = useState<string | null>(() =>
    localStorage.getItem("app-api-key"),
  );

  const updateApiKey = (newApiKey: string) => {
    localStorage.setItem("app-api-key", newApiKey);
    setApiKey(newApiKey);
  };

  const clearApiKey = () => {
    localStorage.removeItem("app-api-key");
    setApiKey(null);
  };

  return (
    <AppApiKeyContext.Provider value={{ apiKey, updateApiKey, clearApiKey }}>
      {children}
    </AppApiKeyContext.Provider>
  );
}

export const useApiKey = (): AppApiKeyContextType => {
  const context = useContext(AppApiKeyContext);
  if (!context) {
    throw new Error("useApiKey must be used within a ApiKeyProvider");
  }
  return context;
};
