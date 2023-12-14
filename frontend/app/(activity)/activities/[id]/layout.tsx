import { ContextProps, SidebarNavItem } from "../../../../types";
import BaseLayout from "@/components/base-layout";

type ActivityLayoutProps = {
  children?: React.ReactNode;
} & ContextProps;

export default function AcvitityLayout({
  children,
  params,
}: ActivityLayoutProps) {
  const sideNavItems = createSideNavItems(params.id);
  return <BaseLayout sideNavItems={sideNavItems}>{children}</BaseLayout>;
}

function createSideNavItems(id: string): SidebarNavItem[] {
  return [
    {
      type: "item",
      title: "Back to Activities",
      href: `/activities`,
      icon: "arrowLeft",
      hoverHighlight: false,
    },
    {
      type: "divider",
    },
    {
      type: "title",
      title: "Manage",
    },
    {
      type: "item",
      title: "Activity",
      href: `/activities/${id}`,
      icon: "activity",
    },
    {
      type: "item",
      title: "Achievements",
      href: `/activities/${id}/achievements`,
      icon: "achievement",
    },
    {
      type: "item",
      title: "Rules",
      href: `/activities/${id}/rules`,
      icon: "rule",
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
      title: "Leaderboard",
      href: `/activities/${id}/leaderboard`,
      icon: "leaderboard",
    },
    {
      type: "item",
      title: "Members",
      href: `/activities/${id}/members`,
      icon: "members",
    },
    {
      type: "item",
      title: "Test Rules",
      href: `/activities/${id}/playground`,
      icon: "experiment",
    },
  ];
}
