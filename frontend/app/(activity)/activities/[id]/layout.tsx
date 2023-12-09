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
      title: "Back to Activities",
      href: `/activities`,
      icon: "arrowLeft",
    },
    {
      title: "Activity",
      href: `/activities/${id}`,
      icon: "activity",
    },
    {
      title: "Rules",
      href: `/activities/${id}/rules`,
      icon: "rule",
    },
    {
      title: "Leaderboard",
      href: `/activities/${id}/leaderboard`,
      icon: "leaderboard",
    },
    {
      title: "Members",
      href: `/activities/${id}/members`,
      icon: "members",
    },
    {
      title: "Playground",
      href: `/activities/${id}/playground`,
      icon: "playground",
    },
    {
      title: "Settings",
      href: `/activities/${id}/settings`,
      icon: "settings",
    },
  ];
}
