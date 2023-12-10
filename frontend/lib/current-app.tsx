"use client";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { toast } from "../components/ui/use-toast";
import { API_URL } from "../hooks/api";
import { AppResponse } from "../types/generated-types";
import { useEffect, useState } from "react";

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

let initiating = true;

export function useCurrentApp(): {
  currentApp: AppResponse | undefined;
  setCurrentAppWithApiKey: (apiKey: string) => void;
  isLoading: boolean;
  clearCurrentApp: () => void;
} {
  const [apiKey, setApiKey] = useState<string | undefined>();
  const queryClient = useQueryClient();

  const { data: currentApp, isLoading } = useQuery<AppResponse, Error>({
    queryKey: ["currentApp"],
    queryFn: () => fetchAppByApiKey(apiKey!!),
    enabled: apiKey !== undefined,
    initialData: () => {
      return queryClient.getQueryData<AppResponse>(["currentApp"]);
    },
  });

  const { mutate: setCurrentAppWithApiKey } = useMutation({
    mutationFn: (newApiKey: string) => fetchAppByApiKey(newApiKey),
    onSuccess: (app) => {
      queryClient.setQueryData(["currentApp"], app);
      localStorage.setItem("app-api-key", app.apiKey);
      setApiKey(app.apiKey);
    },
    onError: () => {
      toast({
        title: "Invalid API key",
        description: "The API key you entered is invalid.",
        variant: "destructive",
      });
    },
  });

  useEffect(() => {
    if (!initiating) {
      return;
    }

    const storedApiKey = localStorage.getItem("app-api-key");
    if (storedApiKey) {
      setCurrentAppWithApiKey(storedApiKey);
    }

    initiating = false;
  }, []);

  const clearCurrentApp = () => {
    queryClient.removeQueries({ queryKey: ["currentApp"] });
    localStorage.removeItem("app-api-key");
    setApiKey(undefined);
  };

  return {
    currentApp,
    setCurrentAppWithApiKey,
    isLoading: isLoading || initiating,
    clearCurrentApp,
  };
}
