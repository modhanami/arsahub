"use client";
import { useCurrentUser } from "@/lib/current-user";
import { Auth } from "@supabase/auth-ui-react";
import { supabase } from "@/lib/supabase";
import { ThemeSupa } from "@supabase/auth-ui-shared";
import { useRouter, useSearchParams } from "next/navigation";
import { useEffect } from "react";
import { siteConfig } from "@/config/site";

export default function LoginPage() {
  const { session, isLoading } = useCurrentUser();
  const router = useRouter();
  console.log("[LoginPage] Session", session);
  const redirect = useSearchParams().get("redirect") || "/";

  useEffect(() => {
    if (session) {
      router.push(redirect);
    }
  }, [redirect, router, session]);

  if (session) {
    return null;
  }

  return (
    <div className="flex items-center justify-center h-screen">
      <div className="border p-4 rounded-md bg-white shadow-lg">
        <div className="flex flex-col items-center">
          <span className="hidden text-2xl font-bold sm:inline-block mb-4 mt-4">
            Welcome to {siteConfig.name}
          </span>
          <Auth
            supabaseClient={supabase}
            appearance={{
              theme: ThemeSupa,
              extend: true,
              className: {
                container: "sm:w-[400px] w-[80vw]",
              },
            }}
            providers={["google"]}
            theme="default"
            redirectTo={redirect}
          />
        </div>
      </div>
    </div>
  );
}
