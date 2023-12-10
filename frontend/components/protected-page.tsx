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
  const router = useRouter();
  const { currentApp, isLoading } = useCurrentApp();

  if (isLoading) {
    return null;
  }

  if (!currentApp) {
    toast({
      title: "No App Specified",
      description: "You must specify an app API key to access this page.",
    });
    router.push("/");
    return null;
  }

  return children;
}
