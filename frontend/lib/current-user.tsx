"use client";
import { useMutation, useQuery, useQueryClient } from "react-query";
import { toast } from "../components/ui/use-toast";
import { API_URL } from "../hooks/api";
import { UserResponse } from "../types/generated-types";
import { useCurrentApp } from "./current-app";

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
  const uuid = localStorage.getItem("user-uuid");
  const queryClient = useQueryClient();
  const { clearCurrentApp } = useCurrentApp();

  const { data: currentUser, isLoading } = useQuery<
    UserResponseWithUUID,
    Error
  >("currentUser", () => fetchUserByUUID(uuid!!), {
    enabled: uuid !== null,
    initialData: () => {
      const userData =
        queryClient.getQueryData<UserResponseWithUUID>("currentUser");
      return userData;
    },
  });

  const { mutate: setCurrentUserWithUUID } = useMutation(
    (newUuid: string) => fetchUserByUUID(newUuid),
    {
      onSuccess: (user) => {
        queryClient.setQueryData("currentUser", user);
        localStorage.setItem("user-uuid", user.uuid);
      },
      onError: () => {
        toast({
          title: "Invalid user UUID",
          description: "The user UUID you entered is invalid.",
          variant: "destructive",
        });
      },
    }
  );

  const logoutCurrentUser = () => {
    queryClient.removeQueries("currentUser");
    localStorage.removeItem("user-uuid");
    clearCurrentApp();
  };

  return { currentUser, setCurrentUserWithUUID, logoutCurrentUser, isLoading };
}
