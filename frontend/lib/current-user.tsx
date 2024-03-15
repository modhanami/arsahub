"use strict";
import React, { createContext, useContext, useEffect, useState } from "react";
import { supabase } from "@/lib/supabase";
import { Session } from "@supabase/supabase-js";
import { syncSupabaseIdentity } from "@/api";
import { UserIdentity } from "@/types/generated-types";
import { useCurrentApp } from "@/lib/current-app";

interface CurrentUserContextProps {
  currentUser: UserIdentity | null;
  session: Session | null;
  isLoading: boolean;
  startLoginFlow: (args: { returnTo: string }) => void;
  startRegistrationFlow: (args: { returnTo: string }) => void;
  startLogoutFlow: () => void;
}

const CurrentUserContext = createContext<CurrentUserContextProps | undefined>(
  undefined,
);

export function CurrentUserProvider({
  children,
}: {
  children: React.ReactNode;
}) {
  const [isLoading, setIsLoading] = useState(true);
  const [user, setUser] = useState<UserIdentity | null>(null);
  const [session, setSession] = useState<Session | null>(null);
  const { clearCurrentApp } = useCurrentApp();

  useEffect(() => {
    supabase.auth
      .getSession()
      .then(({ data: { session } }) => {
        setSession(session);
      })
      .finally(() => {
        setIsLoading(false);
      });

    const {
      data: { subscription },
    } = supabase.auth.onAuthStateChange(async (event, session) => {
      console.log(event, session);
      setSession(session);
      setIsLoading(false);
    });

    return () => {
      subscription.unsubscribe();
    };
  }, []);

  useEffect(() => {
    async function sessionCheck() {
      if (session !== null) {
        const internalUser = await syncSupabaseIdentity({ session });
        setUser({
          internalUserId: internalUser.internalUserId,
          externalUserId: internalUser.externalUserId,
          googleUserId: internalUser.googleUserId,
          email: internalUser.email,
          name: internalUser.name,
        });
      } else {
        setUser(null);
      }
    }

    sessionCheck();
  }, [session]);

  async function startLoginFlow({ returnTo }: { returnTo: string }) {
    console.log("startLoginFlow", user, session, returnTo);
    if (user) {
      console.warn("User is already logged in");
      return;
    }

    await supabase.auth.signInWithOAuth({
      provider: "google",
      options: {
        redirectTo: returnTo,
      },
    });
  }

  function startRegistrationFlow({ returnTo }: { returnTo: string }) {}

  async function startLogoutFlow() {
    console.log("startLogoutFlow", user, session);
    await supabase.auth.signOut();
    clearCurrentApp();
  }

  return (
    <CurrentUserContext.Provider
      value={{
        currentUser: user,
        session,
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
