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
