import { DashboardConfig } from "types";

export const dashboardConfig: DashboardConfig = {
  mainNav: [],
  sidebarNav: [
    {
      type: "item",
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
      type: "item",
      title: "Settings",
      href: "/dashboard/settings",
      icon: "settings",
    },
  ],
};
