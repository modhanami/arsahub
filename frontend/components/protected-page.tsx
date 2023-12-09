"use client";
import { usePathname, useRouter } from "next/navigation";
import { useEffect } from "react";
import { useCurrentUser } from "../lib/current-user";

export function ProtectedPage({ children }: { children: React.ReactNode }) {
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
