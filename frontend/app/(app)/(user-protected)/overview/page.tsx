"use client";
import { Configuration, FrontendApi, Identity, Session } from "@ory/client";
import { useEffect, useState } from "react";
import { usePathname, useRouter } from "next/navigation";

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

// Returns either the email or the username depending on the user's Identity Schema
const getUserName = (identity?: Identity) =>
  identity?.traits.email || identity?.traits.username;

export default function Page() {
  const router = useRouter();
  const [session, setSession] = useState<Session | undefined>();
  const [logoutUrl, setLogoutUrl] = useState<string | undefined>();
  const pathname = usePathname();

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
      .catch((e) => {
        console.error(e);
        // Redirect to login page
        // return router.push(basePath + "/ui/login");
        const currentUrl = encodeURIComponent(window.location.href);
        return router.push(`${basePath}/ui/login?return_to=${currentUrl}`);
      });
  }, [router]);

  if (!session) {
    // Still loading
    return null;
  }

  return (
    <div>
      NOTHING HERE
      {pathname}
      <p>Hello, {getUserName(session?.identity)}</p>
      <div>
        <p>
          <a href={logoutUrl}>Log out</a>
        </p>
      </div>
      {session && (
        <pre>
          <code>{JSON.stringify(session, null, 2)}</code>
        </pre>
      )}
    </div>
  );
}
