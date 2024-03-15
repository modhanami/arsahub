"use client";
import { ReactQueryDevtools } from "@tanstack/react-query-devtools";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { NextUIProvider } from "@nextui-org/react";
import { CurrentUserProvider } from "@/lib/current-user";
import { useRouter } from "next/navigation";
import { AppApiKeyProvider } from "@/lib/current-app";

const queryClient = new QueryClient();

export function Providers({ children }: { children: React.ReactNode }) {
  const router = useRouter();
  return (
    <AppApiKeyProvider>
      <NextUIProvider navigate={router.push}>
        <QueryClientProvider client={queryClient}>
          <CurrentUserProvider>{children}</CurrentUserProvider>
          <ReactQueryDevtools initialIsOpen={false} />
        </QueryClientProvider>
      </NextUIProvider>
    </AppApiKeyProvider>
  );
}
