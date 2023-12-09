"use client";
import { usePathname, useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { useCurrentUser } from "../lib/current-user";
import { useCurrentApp } from "../lib/current-app";
import { toast } from "./ui/use-toast";

export function UserProtectedPage({ children }: { children: React.ReactNode }) {
  const { currentUser, loading } = useCurrentUser();
  const router = useRouter();
  const pathname = usePathname();

  useEffect(() => {
    if (!loading && !currentUser) {
      router.push(`/login?redirect=${pathname}`);
    }
  }, [currentUser, loading, pathname, router]);

  if (loading) {
    return null;
  }

  return children;
}

export function AppProtectedPage({ children }: { children: React.ReactNode }) {
  const { currentApp, loading } = useCurrentApp();

  if (!loading && !currentApp) {
    toast({
      title: "App API key is required",
      description: "You need to provide an API key to access this page.",
      variant: "destructive",
    });
  }

  if (loading) {
    return null;
  }

  return children;
}
