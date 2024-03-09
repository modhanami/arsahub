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
      title: "Triggers",
      href: resolveBasePath("/triggers"),
      icon: "trigger",
      appProtected: true,
      type: "item",
    },
    {
      type: "item",
      title: "Rules",
      href: resolveBasePath("/rules"),
      appProtected: true,
      icon: "rule",
    },
    {
      title: "App Users",
      href: resolveBasePath("/users"),
      icon: "users",
      appProtected: true,
      type: "item",
    },
    {
      type: "item",
      title: "Achievements",
      href: resolveBasePath("/achievements"),
      appProtected: true,
      icon: "achievement",
    },
    {
      type: "item",
      title: "Rewards",
      href: resolveBasePath("/rewards"),
      appProtected: true,
      icon: "reward",
    },
    {
      type: "item",
      title: "Points Shop",
      href: resolveBasePath("/shop"),
      appProtected: true,
      icon: "pointsShop",
    },
    {
      type: "item",
      title: "Leaderboard",
      href: resolveBasePath("/leaderboard"),
      appProtected: true,
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
      type: "item",
      title: "Test Rules",
      href: resolveBasePath("/playground"),
      icon: "experiment",
      appProtected: true,
    },
  ];
}
