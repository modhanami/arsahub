import {DashboardConfig} from "types";

export const dashboardConfig: DashboardConfig = {
  mainNav: [],
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
