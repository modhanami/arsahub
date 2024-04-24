"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";

import { SidebarNavItem } from "types";
import { cn } from "@/lib/utils";
import { Icons } from "@/components/icons";
import { useCurrentApp } from "@/lib/current-app";
import { CurrentAppForm } from "@/components/current-app";
import React from "react";
import { useCurrentUser } from "@/lib/current-user";

interface DashboardNavProps {
  items: SidebarNavItem[];
  children?: React.ReactNode;
}

export function DashboardNav({ items, children }: DashboardNavProps) {
  const path = usePathname();
  const { currentApp, isLoading } = useCurrentApp();
  const { currentUser, isLoading: userLoading } = useCurrentUser();

  if (!items?.length) {
    return null;
  }

  return (
    <nav className="bg-primary/5 relative overflow-hidden">
      <div className="grid content-between px-4 py-6 h-[calc(100vh-65px)] sticky top-16">
        <div className="grid items-start gap-2">
          {items.map((item, index) => {
            if (item.type === "item") {
              const Icon = Icons[item.icon || "arrowRight"];
              const isDisabled =
                item.disabled ||
                (item.appProtected && !currentApp) ||
                (item.userProtected && !currentUser);
              return (
                item.href && (
                  <Link
                    key={item.title}
                    href={isDisabled || !item.href ? "#" : item.href}
                    title={
                      item.tooltip
                        ? item.tooltip
                        : item.appProtected && !currentApp
                          ? "You must specify an app to access this page."
                          : item.userProtected && !currentUser
                            ? "You must login to access this page."
                            : ""
                    }
                  >
                    <span
                      className={cn(
                        "group flex items-center rounded-sm px-3 py-2 text-sm font-medium",
                        path === item.href ? "bg-accent" : "transparent",
                        isDisabled
                          ? "cursor-not-allowed opacity-70"
                          : "hover:text-accent-foreground hover:bg-accent",
                      )}
                    >
                      <Icon className="mr-2 h-4 w-4" />
                      <span>{item.title}</span>
                    </span>
                  </Link>
                )
              );
            }

            if (item.type === "divider") {
              return <hr key={index} className="border-transparent m-1" />;
            }

            if (item.type === "title") {
              return (
                <span
                  key={index}
                  className="px-3 py-1 text-sm text-muted-foreground tracking-wide"
                >
                  {item.title}
                </span>
              );
            }
          })}
        </div>
        <CurrentAppForm />
      </div>
    </nav>
  );
}
