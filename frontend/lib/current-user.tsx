"use strict";
import React, { createContext, useContext, useEffect, useState } from "react";
import { Configuration, FrontendApi, Session } from "@ory/client";
import { useRouter } from "next/navigation";
import { UserIdentity } from "@/types";
import { syncExternalUser } from "@/api"; // Get your Ory url from .env

// Get your Ory url from .env
// Or localhost for local development
const basePath = process.env.NEXT_PUBLIC_ORY_SDK_URL || "http://localhost:4000";
const ory = new FrontendApi(
  new Configuration({
    basePath: "http://localhost:4000",
    baseOptions: {
      withCredentials: true,
    },
  }),
);

interface CurrentUserContextProps {
  currentUser: UserIdentity | undefined;
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
  const [session, setSession] = useState<Session | undefined>();
  const identity = session?.identity;
  const user: UserIdentity | undefined = identity
    ? {
        id: identity.id,
        email: identity?.traits.email,
        firstName: identity.traits.first_name,
        lastName: identity.traits.last_name,
        get fullName() {
          return `${this.firstName} ${this.lastName}`;
        },
      }
    : undefined;

  useEffect(() => {
    async function init() {
      try {
        const { data } = await ory.toSession();
        // User has a session!
        setSession(data);

        // Sync the user with the external provider
        await syncExternalUser();
      } catch (e) {
        console.error(e);
        const encodedCurrentUrl = encodeURIComponent(window.location.href);
        return router.push(
          `${basePath}/ui/login?return_to=${encodedCurrentUrl}`,
        );
      } finally {
        setIsLoading(false);
      }
    }
    init();
  }, []);

  function startLoginFlow({ returnTo }: { returnTo: string }) {
    if (user) {
      console.warn("User is already logged in");
      return;
    }

    ory
      .createBrowserLoginFlow({
        returnTo,
      })
      .then(({ data }) => {
        router.push(data.request_url);
      })
      .catch((e) => {
        console.error(e);
      });
  }

  function startRegistrationFlow({ returnTo }: { returnTo: string }) {
    ory
      .createBrowserRegistrationFlow({
        returnTo,
      })
      .then(({ data }) => {
        router.push(data.request_url);
      })
      .catch((e) => {
        console.error(e);
      });
  }

  function startLogoutFlow({ returnTo }: { returnTo: string }) {
    ory
      .createBrowserLogoutFlow({
        returnTo: returnTo,
      })
      .then(({ data }) => {
        router.push(data.logout_url);
      })
      .catch((e) => {
        console.error(e);
      });
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
