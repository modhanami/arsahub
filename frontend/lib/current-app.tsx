"use client";
import React, { useEffect, useState } from "react";
import { API_URL, makeAuthorizationHeader } from "../hooks/api";
import { AppResponse } from "../types/generated-types";
import { toast } from "../components/ui/use-toast";

let currentApp: AppResponse | null = null;
let _loading = true;

export function getCurrentApp(): AppResponse | null {
  return currentApp;
}

const appChangeListeners: ((
  newApp: AppResponse | null,
  loading: boolean
) => void)[] = [];

function loadFromLocalStorage() {
  const apiKey = localStorage.getItem("app-api-key");
  if (apiKey) {
    setCurrentAppWithApiKey(apiKey);
  }
}

loadFromLocalStorage();

export function waitForCurrentApp() {
  return new Promise<AppResponse | null>((resolve) => {
    if (currentApp) {
      resolve(currentApp);
    }

    setTimeout(() => {
      resolve(currentApp);
    }, 100);
  });
}

async function fetchAppByApiKey(apiKey: string): Promise<AppResponse> {
  const response = await fetch(`${API_URL}/apps/current`, {
    headers: {
      Authorization: `Bearer ${apiKey}`,
    },
  });

  if (!response.ok) {
    throw new Error("Invalid API key");
  }

  const json = await response.json();
  return json;
}

export function setCurrentAppWithApiKey(newApiKey: string) {
  console.log("Loading app with API key", newApiKey);
  localStorage.setItem("app-api-key", newApiKey);
  _loading = true;

  fetchAppByApiKey(newApiKey)
    .then((app) => {
      currentApp = app;
    })
    .catch((error) => {
      toast({
        title: "Invalid API key",
        description: "The API key you entered is invalid.",
        variant: "destructive",
      });
    })
    .finally(() => {
      _loading = false;
      appChangeListeners.forEach((listener) => listener(currentApp, _loading));
    });
}

export function onCurrentAppChange(
  callback: (newApp: AppResponse | null, loading: boolean) => void
) {
  appChangeListeners.push(callback);
}

const CurrentAppContext = React.createContext({
  currentApp: getCurrentApp(),
  setCurrentAppWithApiKey,
  loading: _loading,
});

export function CurrentAppProvider({
  children,
}: {
  children: React.ReactNode;
}) {
  const [currentApp, setCurrentApp] = useState(getCurrentApp());
  const [loading, setLoading] = useState(_loading);

  useEffect(() => {
    onCurrentAppChange((newApp, loading) => {
      console.log(
        `Updated current app to ${newApp?.name}, loading: ${loading}`
      );

      setCurrentApp(newApp);
      setLoading(loading);
    });
  }, []);

  useEffect(() => {
    if (currentApp?.apiKey) {
      setCurrentAppWithApiKey(currentApp.apiKey);
    }
  }, [currentApp?.apiKey]);

  return (
    <CurrentAppContext.Provider
      value={{ currentApp, setCurrentAppWithApiKey, loading }}
    >
      {children}
    </CurrentAppContext.Provider>
  );
}

export function useCurrentApp() {
  const context = React.useContext(CurrentAppContext);
  if (context === undefined) {
    throw new Error("useCurrentApp must be used within a CurrentAppProvider");
  }

  return context;
}
