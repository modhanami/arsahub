"use client";
import React, { useEffect, useState } from "react";
import { toast } from "../components/ui/use-toast";
import { API_URL } from "../hooks/api";
import { UserResponse } from "../types/generated-types";

type UserResponseWithUUID = UserResponse & {
  uuid: string;
};

let currentUser: UserResponseWithUUID | null = null;
let loading = true;

function loadFromLocalStorage() {
  const uuid = localStorage.getItem("user-uuid");
  if (uuid) {
    setCurrentUserWithUUID(uuid);
  }

  loading = false;
}

loadFromLocalStorage();

export function getCurrentUser(): UserResponseWithUUID | null {
  return currentUser;
}

async function fetchUserByUUID(uuid: string): Promise<UserResponse> {
  const response = await fetch(`${API_URL}/apps/users/current`, {
    headers: {
      Authorization: `Bearer ${uuid}`,
    },
  });

  if (!response.ok) {
    throw new Error("Invalid user UUID");
  }

  const json = await response.json();
  return json;
}

const userChangeListeners: ((newUser: UserResponseWithUUID | null) => void)[] =
  [];

export async function setCurrentUserWithUUID(uuid: string) {
  console.log("Loading user with UUID", uuid);
  localStorage.setItem("user-uuid", uuid);
  loading = true;

  return fetchUserByUUID(uuid).then((user) => {
    currentUser = {
      ...user,
      uuid,
    };
    userChangeListeners.forEach((listener) => listener({ ...user, uuid }));
    loading = false;
  });
}

export function logoutCurrentUser() {
  currentUser = null;
  userChangeListeners.forEach((listener) => listener(null));
}

export function onCurrentUserChange(
  callback: (newUser: UserResponseWithUUID | null) => void
) {
  userChangeListeners.push(callback);
}

const CurrentUserContext = React.createContext({
  currentUser: getCurrentUser(),
  setCurrentUserWithUUID,
  logoutCurrentUser,
  loading,
});

export function CurrentUserProvider({
  children,
}: {
  children: React.ReactNode;
}) {
  const [currentUser, setCurrentUser] = useState(getCurrentUser());

  useEffect(() => {
    onCurrentUserChange((newUser) => {
      console.log("Received new user", newUser);
      setCurrentUser(newUser);
    });
  }, []);

  useEffect(() => {
    if (currentUser?.uuid) {
      setCurrentUserWithUUID(currentUser.uuid);
    }
  }, [currentUser?.uuid]);

  return (
    <CurrentUserContext.Provider
      value={{
        currentUser,
        setCurrentUserWithUUID,
        logoutCurrentUser,
        loading,
      }}
    >
      {children}
    </CurrentUserContext.Provider>
  );
}

export function useCurrentUser() {
  const context = React.useContext(CurrentUserContext);
  if (context === undefined) {
    throw new Error("useCurrentUser must be used within a CurrentUserProvider");
  }

  return context;
}
