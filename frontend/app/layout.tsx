import { ThemeProvider } from "@/components/theme-provider";
import type { Metadata } from "next";
import { Inter } from "next/font/google";
import { WrappedQueryClientProvider } from "../components/query-client-provider";
import { Toaster } from "../components/ui/toaster";
import "./globals.css";
import { AppApiKeyProvider } from "@/lib/current-app";
import { UserUuidProvider } from "@/lib/current-user";

const inter = Inter({ subsets: ["latin"] });

export const metadata: Metadata = {
  title: "Create Next App",
  description: "Generated by create next app",
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en">
      <body className={inter.className}>
        <ThemeProvider
          attribute="class"
          defaultTheme="system"
          enableSystem
          disableTransitionOnChange
        >
          <AppApiKeyProvider>
            <UserUuidProvider>
              <WrappedQueryClientProvider>
                {/* <ModeToggle /> */}
                {children}
                <Toaster />
              </WrappedQueryClientProvider>
            </UserUuidProvider>
          </AppApiKeyProvider>
        </ThemeProvider>
      </body>
    </html>
  );
}
