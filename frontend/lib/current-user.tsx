"use client";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { toast } from "../components/ui/use-toast";
import { API_URL } from "../hooks/api";
import { UserResponse } from "../types/generated-types";
import { useCurrentApp } from "./current-app";
import { useEffect, useState } from "react";

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

let initiating = true;

export function useCurrentUser() {
  const [uuid, setUuid] = useState<string | undefined>();
  const queryClient = useQueryClient();
  const { clearCurrentApp } = useCurrentApp();

  const { data: currentUser, isLoading } = useQuery<
    UserResponseWithUUID,
    Error
  >({
    queryKey: ["currentUser"],
    queryFn: () => fetchUserByUUID(uuid!!),
    enabled: uuid !== undefined,
    initialData: () => {
      return queryClient.getQueryData<UserResponseWithUUID>(["currentUser"]);
    },
  });

  const { mutate: setCurrentUserWithUUID } = useMutation({
    mutationFn: (newUuid: string) => fetchUserByUUID(newUuid),
    onSuccess: (user) => {
      queryClient.setQueryData(["currentUser"], user);
      localStorage.setItem("user-uuid", user.uuid);
      setUuid(user.uuid);
    },
    onError: () => {
      toast({
        title: "Invalid user UUID",
        description: "The user UUID you entered is invalid.",
        variant: "destructive",
      });
    },
  });

  useEffect(() => {
    if (!initiating) {
      return;
    }

    const storedUuid = localStorage.getItem("user-uuid");
    if (storedUuid) {
      setCurrentUserWithUUID(storedUuid);
    }

    initiating = false;
  }, []);

  const logoutCurrentUser = () => {
    queryClient.removeQueries({ queryKey: ["currentUser"] });
    localStorage.removeItem("user-uuid");
    clearCurrentApp();
  };

  return {
    currentUser,
    setCurrentUserWithUUID,
    isLoading: isLoading || initiating,
    logoutCurrentUser,
  };
}
