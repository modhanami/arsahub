import { DashboardConfig } from "types";
import { dashboardConfig } from "./dashboard";

export const activityConfig: DashboardConfig = {
  mainNav: dashboardConfig.mainNav,
  sidebarNav: [
    {
      type: "item",
      title: "Activity",
      href: "",
      icon: "activity",
    },
    {
      type: "item",
      title: "Rules",
      href: "rules",
      icon: "rule",
    },
    {
      type: "item",
      title: "Settings",
      href: "settings",
      icon: "settings",
    },
  ],
};
