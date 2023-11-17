import { DashboardConfig } from "types";

export const dashboardConfig: DashboardConfig = {
  mainNav: [
    {
      title: "Home 👽",
      href: "/overview",
    },
  ],
  sidebarNav: [
    {
      title: "Overview",
      href: "/overview",
      icon: "activity",
    },
    // {
    //   title: "Billing",
    //   href: "/dashboard/billing",
    //   icon: "billing",
    // },
    {
      title: "Settings",
      href: "/overview/settings",
      icon: "settings",
    },
  ],
};
