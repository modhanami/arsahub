"use client";
import { useCurrentUser } from "@/lib/current-user";
import { Auth } from "@supabase/auth-ui-react";
import { supabase } from "@/lib/supabase";
import { ThemeSupa } from "@supabase/auth-ui-shared";
import { useRouter, useSearchParams } from "next/navigation";
import { useEffect } from "react";

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
    <Auth
      supabaseClient={supabase}
      appearance={{ theme: ThemeSupa }}
      providers={["google"]}
      theme="dark"
      redirectTo={redirect}
    />
  );
}
