import { DashboardConfig } from "types";
import { dashboardConfig } from "./dashboard";

export const activityConfig: DashboardConfig = {
  mainNav: dashboardConfig.mainNav,
  sidebarNav: [
    {
      title: "Activity",
      href: "",
      icon: "activity",
    },
    {
      title: "Rules",
      href: "rules",
      icon: "rule",
    },
    {
      title: "Settings",
      href: "settings",
      icon: "settings",
    },
  ],
};
