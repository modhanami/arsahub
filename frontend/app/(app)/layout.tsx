"use client";

import { ContextProps, SidebarNavItem } from "../../types";
import BaseLayout from "@/components/base-layout";
import { resolveBasePath } from "@/lib/base-path";

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
      href: resolveBasePath("/overview"),
      icon: "general",
      type: "item",
    },
    {
      title: "Secrets",
      href: resolveBasePath(`/secrets`),
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
      href: resolveBasePath("/achievements"),
      icon: "achievement",
    },
    {
      type: "item",
      title: "Rules",
      href: resolveBasePath("/rules"),
      icon: "rule",
    },
    {
      type: "item",
      title: "Rewards",
      href: resolveBasePath("/rewards"),
      icon: "reward",
    },
    {
      type: "item",
      title: "Points Shop",
      href: resolveBasePath("/shop"),
      icon: "pointsShop",
    },
    {
      type: "item",
      title: "Leaderboard",
      href: resolveBasePath("/leaderboard"),
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
      href: resolveBasePath("/triggers"),
      icon: "trigger",
      appProtected: true,
      type: "item",
    },
    {
      type: "item",
      title: "Test Rules",
      href: resolveBasePath("/playground"),
      icon: "experiment",
      appProtected: true,
    },
    {
      title: "App Users",
      href: resolveBasePath("/users"),
      icon: "users",
      appProtected: true,
      type: "item",
    },
  ];
}
