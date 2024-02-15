"use strict";
import React, { createContext, useContext, useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { supabase } from "@/lib/supabase";
import { Session } from "@supabase/supabase-js";
import { syncSupabaseIdentity } from "@/api";
import { UserIdentity } from "@/types/generated-types";

interface CurrentUserContextProps {
  currentUser: UserIdentity | null;
  isLoading: boolean;
  startLoginFlow: (args: { returnTo: string }) => void;
  startRegistrationFlow: (args: { returnTo: string }) => void;
  startLogoutFlow: (args: { returnTo: string }) => void;
}

const CurrentUserContext = createContext<CurrentUserContextProps | undefined>(
  undefined,
);

export function CurrentUserProvider({
  children,
}: {
  children: React.ReactNode;
}) {
  const router = useRouter();
  const [isLoading, setIsLoading] = useState(true);
  const [user, setUser] = useState<UserIdentity | null>(null);
  const [session, setSession] = useState<Session | null>(null);

  useEffect(() => {
    async function init() {
      supabase.auth.onAuthStateChange(async (event, session) => {
        console.log(event, session);
        setSession(session);
        if (
          event === "INITIAL_SESSION" ||
          event === "SIGNED_IN" ||
          event === "USER_UPDATED"
        ) {
          const internalUser = await syncSupabaseIdentity();
          setUser({
            internalUserId: internalUser.internalUserId,
            externalUserId: internalUser.externalUserId,
            googleUserId: internalUser.googleUserId,
            email: internalUser.email,
            name: internalUser.name,
          });
        }
      });
    }

    init().then(() => {
      setIsLoading(false);
    });
  }, []);

  async function startLoginFlow({ returnTo }: { returnTo: string }) {
    if (user) {
      console.warn("User is already logged in");
      return;
    }

    supabase.auth.signInWithOAuth({
      provider: "google",
      options: {
        redirectTo: returnTo,
      },
    });
  }

  function startRegistrationFlow({ returnTo }: { returnTo: string }) {}

  async function startLogoutFlow({ returnTo }: { returnTo: string }) {
    await supabase.auth.signOut();
  }

  return (
    <CurrentUserContext.Provider
      value={{
        currentUser: user,
        isLoading,
        startLoginFlow,
        startRegistrationFlow,
        startLogoutFlow,
      }}
    >
      {children}
    </CurrentUserContext.Provider>
  );
}

export function useCurrentUser() {
  const context = useContext(CurrentUserContext);
  if (!context) {
    throw new Error("useCurrentUser must be used within a CurrentUserProvider");
  }
  return context;
}
