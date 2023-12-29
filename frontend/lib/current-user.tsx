"use client";
import { useQueryClient } from "@tanstack/react-query";
import { toast } from "../components/ui/use-toast";
import React, { createContext, useContext, useEffect, useState } from "react";
import { useCurrentApp } from "@/lib/current-app";
import { useUser } from "@/hooks";

export function useCurrentUser() {
  const { uuid, clearUuid, isLoading: isUserUuidLoading } = useUserUuid();
  const { clearCurrentApp } = useCurrentApp();
  const queryClient = useQueryClient();

  const { data: currentUser, isLoading, error } = useUser(uuid);

  useEffect(() => {
    if (error) {
      toast({
        title: "Invalid user UUID",
        description: error.message,
        variant: "destructive",
      });
      clearUuid();
    }
  }, [clearUuid, error]);

  const logoutCurrentUser = () => {
    clearUuid();
    clearCurrentApp();
    queryClient.removeQueries({ queryKey: ["currentUser"] });
  };

  return {
    currentUser,
    isLoading: isLoading || isUserUuidLoading,
    logoutCurrentUser,
  };
}

export interface UserUuidContextType {
  uuid: string | null;
  updateUuid: (newUuid: string) => void;
  clearUuid: () => void;
  isLoading: boolean;
}

const UserUuidContext = createContext<UserUuidContextType | undefined>(
  undefined,
);

export function UserUuidProvider({ children }: { children: React.ReactNode }) {
  const [uuid, setUuid] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);

  useEffect(() => {
    setUuid(localStorage.getItem("user-uuid"));
    setIsLoading(false);
  }, []);

  const updateUuid = (newUuid: string) => {
    localStorage.setItem("user-uuid", newUuid);
    setUuid(newUuid);
  };

  const clearUuid = () => {
    localStorage.removeItem("user-uuid");
    setUuid(null);
  };

  return (
    <UserUuidContext.Provider
      value={{ uuid, updateUuid, clearUuid, isLoading }}
    >
      {children}
    </UserUuidContext.Provider>
  );
}

export const useUserUuid = (): UserUuidContextType => {
  const context = useContext(UserUuidContext);
  if (!context) {
    throw new Error("useUserUuid must be used within a UserUuidProvider");
  }
  return context;
};
