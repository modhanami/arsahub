"use client";
import React, { useEffect, useState } from "react";
import { API_URL, makeAuthorizationHeader } from "../hooks/api";
import { AppResponse } from "../types/generated-types";
import { toast } from "../components/ui/use-toast";

let currentApp: Partial<AppResponse> = {
  // apiKey: "15cfada4-eadd-4087-9e85-1aaf92a8d476",
};

export function getCurrentApp(): Partial<AppResponse> {
  return currentApp;
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

const appChangeListeners: ((newApp: AppResponse) => void)[] = [];

export function setCurrentAppWithApiKey(newApiKey: string) {
  console.log("Loading app with API key", newApiKey);

  fetchAppByApiKey(newApiKey)
    .then((app) => {
      currentApp = app;
      appChangeListeners.forEach((listener) => listener(app));
    })
    .catch((error) => {
      toast({
        title: "Invalid API key",
        description: "The API key you entered is invalid.",
        variant: "destructive",
      });
    });
}

export function onCurrentAppChange(callback: (newApp: AppResponse) => void) {
  appChangeListeners.push(callback);
}

const CurrentAppContext = React.createContext({
  currentApp: getCurrentApp(),
  setCurrentAppWithApiKey,
});

export function CurrentAppProvider({
  children,
}: {
  children: React.ReactNode;
}) {
  const [currentApp, setCurrentApp] = useState(getCurrentApp());

  useEffect(() => {
    onCurrentAppChange((newApp) => {
      console.log("Received new app", newApp);

      setCurrentApp(newApp);
    });
  }, []);

  useEffect(() => {
    if (currentApp.apiKey) {
      setCurrentAppWithApiKey(currentApp.apiKey);
    }
  }, [currentApp.apiKey]);

  return (
    <CurrentAppContext.Provider value={{ currentApp, setCurrentAppWithApiKey }}>
      {children}
    </CurrentAppContext.Provider>
  );
}

export function useCurrentApp() {
  const context = React.useContext(CurrentAppContext);
  if (context === undefined) {
    throw new Error("useCurrentApp must be used within a CurrentAppProvider");
  }

  const { currentApp, setCurrentAppWithApiKey } = context;

  return { currentApp, setCurrentAppWithApiKey };
}
