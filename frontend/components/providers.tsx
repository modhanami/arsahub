"use client";
import { ReactQueryDevtools } from "@tanstack/react-query-devtools";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { NextUIProvider } from "@nextui-org/react";
import { CurrentUserProvider } from "@/lib/current-user";
import { useRouter } from "next/navigation";
import { AppApiKeyProvider } from "@/lib/current-app";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { LocalizationProvider } from "@mui/x-date-pickers";
import {
  createTheme,
  StyledEngineProvider,
  ThemeProvider as MUIThemeProvider,
} from "@mui/material/styles";
import { useEffect, useMemo, useState } from "react";
import { useTheme } from "next-themes";

const queryClient = new QueryClient();

export function Providers({ children }: { children: React.ReactNode }) {
  const router = useRouter();
  const { resolvedTheme: nextTheme } = useTheme();

  const theme = useMemo(
    () =>
      createTheme({
        palette: {
          mode: nextTheme === "dark" ? "dark" : "light",
        },
      }),
    [nextTheme],
  );

  const [mounted, setMounted] = useState(false);

  useEffect(() => {
    setMounted(true);
  }, []);

  if (!mounted) {
    return null;
  }

  return (
    <MUIThemeProvider theme={theme}>
      <StyledEngineProvider>
        <LocalizationProvider dateAdapter={AdapterDayjs}>
          <AppApiKeyProvider>
            <NextUIProvider navigate={router.push}>
              <QueryClientProvider client={queryClient}>
                <CurrentUserProvider>{children}</CurrentUserProvider>
                <ReactQueryDevtools initialIsOpen={false} />
              </QueryClientProvider>
            </NextUIProvider>
          </AppApiKeyProvider>
        </LocalizationProvider>
      </StyledEngineProvider>
    </MUIThemeProvider>
  );
}
