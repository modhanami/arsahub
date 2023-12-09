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
  const { currentApp, isLoading } = useCurrentApp();

  if (isLoading) {
    return null;
  }

  if (!currentApp) {
    toast({
      title: "No app selected",
      description: "Please select an app to continue.",
    });
    return null;
  }

  return children;
}
