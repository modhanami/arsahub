import { DashboardConfig } from "types";

export const dashboardConfig: DashboardConfig = {
  mainNav: [
    {
      title: "Home 👽",
      href: "/dashboard",
    },
  ],
  sidebarNav: [
    {
      title: "Activities",
      href: "/dashboard",
      icon: "activity",
    },
    // {
    //   title: "Billing",
    //   href: "/dashboard/billing",
    //   icon: "billing",
    // },
    {
      title: "Settings",
      href: "/dashboard/settings",
      icon: "settings",
    },
  ],
};
