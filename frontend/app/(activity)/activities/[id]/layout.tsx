import { MainNav } from "@/components/main-nav";
import { DashboardNav } from "@/components/nav";
import { SiteFooter } from "@/components/site-footer";
import { activityConfig } from "../../../../config/activity";
import { ContextProps, SidebarNavItem } from "../../../../types";

type ActivityLayoutProps = {
  children?: React.ReactNode;
} & ContextProps;

export default function AcvitityLayout({
  children,
  params,
}: ActivityLayoutProps) {
  const sideNavItems = createSideNavItems(params.id);
  return (
    <div className="flex min-h-screen flex-col space-y-6">
      <header className="sticky top-0 z-40 border-b bg-background">
        <div className="container flex h-16 items-center justify-between py-4">
          <MainNav items={activityConfig.mainNav} />
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
