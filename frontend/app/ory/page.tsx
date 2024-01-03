"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";

import { Configuration, FrontendApi, Identity, Session } from "@ory/client";
import { edgeConfig } from "@ory/integrations/next";
import { API_URL } from "@/api";

const ory = new FrontendApi(new Configuration(edgeConfig));

// Returns either the email or the username depending on the user's Identity Schema
const getUserName = (identity: Identity) =>
  identity.traits.email || identity.traits.username;

export default function Page() {
  const router = useRouter();
  const [session, setSession] = useState<Session | undefined>();
  const [logoutUrl, setLogoutUrl] = useState<string | undefined>();

  useEffect(() => {
    ory
      .toSession()
      .then(({ data }) => {
        // User has a session!
        setSession(data);
        // Create a logout url
        ory.createBrowserLogoutFlow().then(({ data }) => {
          setLogoutUrl(data.logout_url);
        });
      })
      .catch(() => {
        // Redirect to login page
        return router.push(edgeConfig.basePath + "/ui/login");
      });
  }, [router]);

  if (!session) {
    // Still loading
    return null;
  }

  async function callBackend() {
    const response = await fetch(`${API_URL}/apps/me`, {
      //   withCredentials: true,
      credentials: "include",
    });
    const data = await response.json();
    alert(data.message);
  }

  return (
    <div>
      <p>Hello, {getUserName(session?.identity)}</p>
      <div>
        <p>
          <a href={logoutUrl}>Log out</a>
        </p>
      </div>

      <div>
        <button onClick={callBackend}>Call backend</button>
      </div>
    </div>
  );
}
