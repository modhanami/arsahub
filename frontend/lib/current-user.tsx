"use client";
import { Configuration, FrontendApi, Session } from "@ory/client";
import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { UserIdentity } from "@/types"; // Get your Ory url from .env

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

interface UseCurrentUserOptions {
  startLoginFlowIfUnauthenticated?: boolean;
}

export function useCurrentUser({
  startLoginFlowIfUnauthenticated = false,
}: UseCurrentUserOptions = {}): {
  currentUser: UserIdentity | undefined;
  isLoading: boolean;
  startLoginFlow: (args: { returnTo: string }) => void;
  startRegistrationFlow: (args: { returnTo: string }) => void;
  startLogoutFlow: (args: { returnTo: string }) => void;
} {
  const router = useRouter();
  const [session, setSession] = useState<Session | undefined>();
  const [isLoading, setIsLoading] = useState(true);
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
        try {
          const { data } = await ory.toSession();
          // User has a session!
          setSession(data);
        } catch (e) {
          console.error(e);
          if (startLoginFlowIfUnauthenticated) {
            const encodedCurrentUrl = encodeURIComponent(window.location.href);
            return router.push(
              `${basePath}/ui/login?return_to=${encodedCurrentUrl}`,
            );
          }
        }
      } finally {
        setIsLoading(false);
      }
    }

    init();
  }, [router]);

  function startLoginFlow({ returnTo }: { returnTo: string }) {
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

  return {
    // session,
    // identity,
    currentUser: user,
    isLoading,
    startLoginFlow,
    startRegistrationFlow,
    startLogoutFlow,
  };
}
