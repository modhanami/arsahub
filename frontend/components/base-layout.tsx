"use client";
import React, { ReactNode } from "react";
import { SidebarNavItem } from "@/types";
import { UserAccountNav } from "@/components/user-account-nav";
import { DashboardNav } from "@/components/nav";
import { CurrentAppForm } from "@/components/current-app";
import { SiteFooter } from "@/components/site-footer";
import { MainNav } from "@/components/main-nav";

interface CommonLayoutProps {
  children: ReactNode;
  sideNavItems: SidebarNavItem[]; // Assuming SideNavItem is the type for side navigation items
}

export default function BaseLayout({
  children,
  sideNavItems,
}: CommonLayoutProps) {
  return (
    <div className="flex min-h-screen flex-col space-y-6">
      <header className="sticky top-0 z-40 border-b bg-background">
        <div className="container flex h-16 items-center justify-between py-4">
          <MainNav />
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
