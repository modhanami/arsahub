"use client";
import React, { ReactNode } from "react";
import { SidebarNavItem } from "@/types";
import { UserAccountNav } from "@/components/user-account-nav";
import { DashboardNav } from "@/components/nav";
import { MainNav } from "@/components/main-nav";
import { ModeToggle } from "@/components/mode-toggle";

interface CommonLayoutProps {
  children: ReactNode;
  sideNavItems: SidebarNavItem[]; // Assuming SideNavItem is the type for side navigation items
}

export default function BaseLayout({
  children,
  sideNavItems,
}: CommonLayoutProps) {
  return (
    <div className="flex min-h-screen flex-col">
      <header className="sticky top-0 z-40 border-b border-primary/10 bg-background">
        <div className="flex h-16 items-center justify-between py-4 px-6">
          <MainNav />
          <div className="flex items-center gap-4">
            <UserAccountNav />
            <ModeToggle />
          </div>
        </div>
      </header>
      <div className="grid flex-1 gap-4 md:grid-cols-[240px_1fr]">
        <aside className="hidden w-[240px] md:flex sticky top-[65px] left-0 max-h-[calc(100vh-65px)]">
          {/*<aside className="hidden w-[240px] flex-col md:flex max-h-[calc(100vh-64px)]">*/}
          <DashboardNav items={sideNavItems}></DashboardNav>
        </aside>
        <main className="flex w-full flex-1 flex-col overflow-hidden py-12">
          {children}
        </main>
      </div>
      {/*<SiteFooter className="border-t" />*/}
    </div>
  );
}
