"use client";

import { ContextProps, SidebarNavItem } from "../../types";
import BaseLayout from "@/components/base-layout";

type DashboardLayoutProps = {
  children?: React.ReactNode;
} & ContextProps;

export default function DashboardLayout({ children }: DashboardLayoutProps) {
  const sideNavItems = createSideNavItems();

  return <BaseLayout sideNavItems={sideNavItems}>{children}</BaseLayout>;
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
      title: "Gamification Elements",
    },
    {
      type: "item",
      title: "Achievements",
      href: `/achievements`,
      icon: "achievement",
    },
    {
      type: "item",
      title: "Rules",
      href: `/rules`,
      icon: "rule",
    },
    {
      type: "item",
      title: "Rewards",
      href: `/rewards`,
      icon: "reward",
    },
    {
      type: "item",
      title: "Leaderboard",
      href: `/leaderboard`,
      icon: "leaderboard",
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
      type: "item",
      title: "Test Rules",
      href: `/playground`,
      icon: "experiment",
      appProtected: true,
    },
    {
      title: "App Users",
      href: `/users`,
      icon: "users",
      appProtected: true,
      type: "item",
    },
  ];
}
