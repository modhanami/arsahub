"use client";
import { usePathname, useRouter } from "next/navigation";
import { useCurrentApp } from "../lib/current-app";
import { useCurrentUser } from "../lib/current-user";
import { toast } from "./ui/use-toast";

export function UserProtectedPage({ children }: { children: React.ReactNode }) {
  const { currentUser, isLoading } = useCurrentUser();
  const router = useRouter();
  const pathname = usePathname();

  if (isLoading) {
    return null;
  }

  if (!currentUser) {
    router.push(`/login?redirect=${pathname}`);
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
