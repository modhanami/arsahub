"use client";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { toast } from "../components/ui/use-toast";
import { API_URL } from "../hooks/api";
import { UserResponse } from "../types/generated-types";
import { createContext, useContext, useEffect, useState } from "react";
import { useCurrentApp } from "@/lib/current-app";

type UserResponseWithUUID = UserResponse & {
  uuid: string;
};

async function fetchUserByUUID(uuid: string): Promise<UserResponseWithUUID> {
  const response = await fetch(`${API_URL}/apps/users/current`, {
    headers: {
      Authorization: `Bearer ${uuid}`,
    },
  });
  if (!response.ok) {
    throw new Error("Invalid user UUID");
  }
  const json = await response.json();
  return { ...json, uuid };
}

export function useCurrentUser() {
  const { uuid, updateUuid, clearUuid } = useUserUuid();
  const { clearCurrentApp } = useCurrentApp();
  const queryClient = useQueryClient();

  const {
    data: currentUser,
    isLoading,
    error,
  } = useQuery<UserResponseWithUUID, Error>({
    queryKey: ["currentUser"],
    queryFn: () => fetchUserByUUID(uuid!),
    enabled: !!uuid,
  });

  useEffect(() => {
    if (error) {
      toast({
        title: "Invalid user UUID",
        description: error.message,
        variant: "destructive",
      });
    }
  }, [error]);

  const logoutCurrentUser = () => {
    clearUuid();
    clearCurrentApp();
    queryClient.removeQueries({ queryKey: ["currentUser"] });
  };

  return {
    currentUser,
    isLoading,
    logoutCurrentUser,
  };
}

export interface UserUuidContextType {
  uuid: string | null;
  updateUuid: (newUuid: string) => void;
  clearUuid: () => void;
}

const UserUuidContext = createContext<UserUuidContextType | undefined>(
  undefined,
);

export function UserUuidProvider({ children }: { children: React.ReactNode }) {
  const [uuid, setUuid] = useState<string | null>(() =>
    localStorage.getItem("user-uuid"),
  );

  const updateUuid = (newUuid: string) => {
    localStorage.setItem("user-uuid", newUuid);
    setUuid(newUuid);
  };

  const clearUuid = () => {
    localStorage.removeItem("user-uuid");
    setUuid(null);
  };

  return (
    <UserUuidContext.Provider value={{ uuid, updateUuid, clearUuid }}>
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
