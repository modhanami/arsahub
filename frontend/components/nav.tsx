"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";

import { SidebarNavItem } from "types";
import { cn } from "@/lib/utils";
import { Icons } from "@/components/icons";
import { useCurrentApp } from "@/lib/current-app";
import { CurrentAppForm } from "@/components/current-app";
import React from "react";

interface DashboardNavProps {
  items: SidebarNavItem[];
  children?: React.ReactNode;
}

export function DashboardNav({ items, children }: DashboardNavProps) {
  const path = usePathname();
  const { currentApp, isLoading } = useCurrentApp();

  if (!items?.length) {
    return null;
  }

  return (
    <nav className="grid content-between bg-primary/5 px-4 py-6 h-full">
      <div className="grid items-start gap-2">
        {items.map((item, index) => {
          if (item.type === "item") {
            const Icon = Icons[item.icon || "arrowRight"];
            const isDisabled =
              item.disabled || (item.appProtected && !isLoading && !currentApp);
            return (
              item.href && (
                <Link
                  key={item.title}
                  href={isDisabled || !item.href ? "#" : item.href}
                >
                  <span
                    className={cn(
                      "group flex items-center rounded-md px-3 py-2 text-sm font-medium",
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
                className="text-xs font-semibold text-accent-foreground uppercase tracking-wide"
              >
                {item.title}
              </span>
            );
          }
        })}
      </div>
      <CurrentAppForm />
    </nav>
  );
}
