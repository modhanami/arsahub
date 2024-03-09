import * as React from "react";

import { cn } from "@/lib/utils";

interface DashboardShellProps extends React.HTMLAttributes<HTMLDivElement> {
  compact?: boolean;
}

export function DashboardShell({
  children,
  className,
  ...props
}: DashboardShellProps) {
  return (
    <div
      className={cn(
        "flex flex-col gap-8 mx-16 max-w-screen-2xl",
        { "flex-1 lg:max-w-2xl": props.compact },
        className,
      )}
      {...props}
    >
      {children}
    </div>
  );
}
