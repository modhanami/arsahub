"use client";

import { notFound } from "next/navigation";

import { MainNav } from "@/components/main-nav";
import { DashboardNav } from "@/components/nav";
import { SiteFooter } from "@/components/site-footer";
import { UserAccountNav } from "@/components/user-account-nav";
import { dashboardConfig } from "@/config/dashboard";
import { CurrentAppForm } from "../../../../components/current-app";
import { useCurrentUser } from "../../../../lib/current-user";
import { ContextProps, SidebarNavItem } from "../../../../types";

type DashboardLayoutProps = {
  children?: React.ReactNode;
} & ContextProps;

export default function DashboardLayout({
  children,
  params: { appId },
}: DashboardLayoutProps) {
  const { currentUser } = useCurrentUser();

  const sideNavItems = createSideNavItems(appId);

  return (
    <div className="flex min-h-screen flex-col space-y-6">
      <header className="sticky top-0 z-40 border-b bg-background">
        <div className="container flex h-16 items-center justify-between py-4">
          <MainNav items={dashboardConfig.mainNav} />
          <CurrentAppForm />
          <UserAccountNav />
        </div>
      </header>
      <div className="container grid flex-1 gap-12 md:grid-cols-[200px_1fr]">
        <aside className="hidden w-[200px] flex-col md:flex">
          <DashboardNav items={sideNavItems} />
        </aside>
        <main className="flex w-full flex-1 flex-col overflow-hidden">
          {children}
        </main>
      </div>
      <SiteFooter className="border-t" />
    </div>
  );
}

function createSideNavItems(appId: string): SidebarNavItem[] {
  return [
    {
      title: "General",
      href: `/apps/${appId}`,
      icon: "general",
    },
    {
      title: "Secrets",
      href: `/apps/${appId}/secrets`,
      icon: "lock",
    },
    {
      title: "Triggers",
      href: `/apps/${appId}/triggers`,
      icon: "trigger",
    },
    {
      title: "Activities",
      href: `/apps/${appId}/activities`,
      icon: "activity",
    },
    {
      title: "Users",
      href: `/apps/${appId}/users`,
      icon: "users",
    },
  ];
}
