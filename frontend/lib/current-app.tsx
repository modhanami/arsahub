"use client";
import {useMutation, useQuery, useQueryClient} from "@tanstack/react-query";
import {toast} from "../components/ui/use-toast";
import {API_URL} from "../hooks/api";
import {AppResponse} from "../types/generated-types";

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
  const apiKey = localStorage.getItem("app-api-key");
  const queryClient = useQueryClient();

  const {data: currentApp, isLoading} = useQuery<AppResponse, Error>(
    {
      queryKey: ["currentApp"],
      queryFn: () => fetchAppByApiKey(apiKey || ""),
      enabled: !!apiKey,
    },
  );

  const {mutate: setCurrentAppWithApiKey} = useMutation({
    mutationFn: (newApiKey: string) => fetchAppByApiKey(newApiKey),
    onSuccess: (app) => {
      queryClient.setQueryData(["currentApp"], app);
      localStorage.setItem("app-api-key", app.apiKey);
    },
    onError: () => {
      toast({
        title: "Invalid API key",
        description: "The API key you entered is invalid.",
        variant: "destructive",
      });
    },
  });

  const clearCurrentApp = () => {
    queryClient.removeQueries({queryKey: ["currentApp"]});
    localStorage.removeItem("app-api-key");
  };

  return {currentApp, setCurrentAppWithApiKey, isLoading, clearCurrentApp};
}
