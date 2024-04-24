"use client";
import { usePathname, useRouter } from "next/navigation";
import { useCurrentApp } from "../lib/current-app";
import { useCurrentUser } from "../lib/current-user";
import { toast } from "./ui/use-toast";
import { useEffect, useRef } from "react";
import { resolveBasePath } from "@/lib/base-path";
import { getReturnTo } from "@/lib/utils";

export function UserProtectedPage({ children }: { children: React.ReactNode }) {
  const { session, isLoading } = useCurrentUser();
  const router = useRouter();
  const pathname = usePathname();

  useEffect(() => {
    if (isLoading) {
      console.log("[UserProtectedPage] isLoading");
      return;
    }

    if (!session) {
      console.log("[UserProtectedPage] No currentUser, redirecting to login");
      router.push(resolveBasePath(`/login?redirect=${getReturnTo(pathname)}`));
      return;
    }
  }, [isLoading, pathname, router, session]);

  if (isLoading) {
    return "Loading user...";
  }

  if (!session) {
    return null;
  }

  return children;
}

export function AppProtectedPage({ children }: { children: React.ReactNode }) {
  const router = useRouter();
  const { currentApp, isLoading } = useCurrentApp();
  const toastTimeoutRef = useRef<NodeJS.Timeout | null>(null);

  useEffect(() => {
    if (isLoading) {
      console.log("[AppProtectedPage] isLoading");
      return;
    }

    // TODO: This is a temporary fix to prevent a flash of the toast message
    if (!currentApp) {
      toastTimeoutRef.current = setTimeout(() => {
        toast({
          title: "No App Specified",
          description:
            "You must login or specify an app API key to access this page.",
          variant: "destructive",
        });
        router.push(resolveBasePath("/"));
      }, 3000);
      return;
    }

    if (toastTimeoutRef.current) {
      clearTimeout(toastTimeoutRef.current);
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
