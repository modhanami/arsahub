"use client";
import { usePathname, useRouter } from "next/navigation";
import { useCurrentApp } from "../lib/current-app";
import { useCurrentUser } from "../lib/current-user";
import { toast } from "./ui/use-toast";
import { useEffect } from "react";

export function UserProtectedPage({ children }: { children: React.ReactNode }) {
  const { currentUser, isLoading } = useCurrentUser();
  const router = useRouter();
  const pathname = usePathname();

  useEffect(() => {
    if (isLoading) {
      return;
    }

    if (!currentUser) {
      router.push(`/login?redirect=${pathname}`);
      return;
    }
  }, [currentUser, isLoading, pathname, router]);

  if (isLoading) {
    return "Loading user...";
  }

  if (!currentUser) {
    return null;
  }

  return children;
}

export function AppProtectedPage({ children }: { children: React.ReactNode }) {
  const router = useRouter();
  const { currentApp, isLoading } = useCurrentApp();

  useEffect(() => {
    if (isLoading) {
      return;
    }

    if (!currentApp) {
      toast({
        title: "No App Specified",
        description: "You must specify an app API key to access this page.",
      });
      router.push("/");
      return;
    }
  }, [currentApp, isLoading, router]);

  if (isLoading) {
    return "Loading app...";
  }

  if (!currentApp) {
    return null;
  }

  return children;
}
