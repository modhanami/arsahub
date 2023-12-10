"use client";

import { MainNav } from "@/components/main-nav";
import { DashboardNav } from "@/components/nav";
import { SiteFooter } from "@/components/site-footer";
import { UserAccountNav } from "@/components/user-account-nav";
import { dashboardConfig } from "@/config/dashboard";
import { CurrentAppForm } from "../../components/current-app";
import { ContextProps, SidebarNavItem } from "../../types";

type DashboardLayoutProps = {
  children?: React.ReactNode;
} & ContextProps;

export default function DashboardLayout({ children }: DashboardLayoutProps) {
  const sideNavItems = createSideNavItems();

  return (
    <div className="flex min-h-screen flex-col space-y-6">
      <header className="sticky top-0 z-40 border-b bg-background">
        <div className="container flex h-16 items-center justify-between py-4">
          <MainNav items={dashboardConfig.mainNav} />
          <UserAccountNav />
        </div>
      </header>
      <div className="container grid flex-1 gap-12 md:grid-cols-[200px_1fr]">
        <aside className="hidden w-[200px] flex-col md:flex">
          <DashboardNav items={sideNavItems}>
            <CurrentAppForm />
          </DashboardNav>
        </aside>
        <main className="flex w-full flex-1 flex-col overflow-hidden">
          {children}
        </main>
      </div>
      <SiteFooter className="border-t" />
    </div>
  );
}

function createSideNavItems(): SidebarNavItem[] {
  return [
    {
      title: "Overview",
      href: `/overview`,
      icon: "general",
      type: "item",
    },
    {
      title: "Secrets",
      href: `/secrets`,
      icon: "lock",
      type: "item",
    },
    {
      type: "divider",
    },
    {
      type: "title",
      title: "Playground",
    },
    {
      title: "Triggers",
      href: `/triggers`,
      icon: "trigger",
      appProtected: true,
      type: "item",
    },
    {
      title: "Activities",
      href: `/activities`,
      icon: "activity",
      appProtected: true,
      type: "item",
    },
    {
      title: "Users",
      href: `/users`,
      icon: "users",
      appProtected: true,
      type: "item",
    },
  ];
}
