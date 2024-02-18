"use client";
import { DashboardShell } from "@/components/shell";
import { DashboardHeader } from "@/components/header";
import { useAchievements } from "@/hooks";
import { AchievementCreateForm } from "@/components/create-achievement";
import { DataTable } from "@/app/(app)/examples/tasks/components/data-table";
import * as React from "react";
import { columns } from "@/app/(app)/(app-protected)/achievements/components/columns";

export default function Page() {
  const { data: achievements, isLoading, isError } = useAchievements();

  if (isLoading) return "Loading...";
  if (isError) return "An error occurred";

  return (
    <DashboardShell>
      <DashboardHeader
        heading="Achievements"
        text="Create and manage achievements."
      >
        <AchievementCreateForm />
      </DashboardHeader>
      <DataTable columns={columns} data={achievements || []} />
    </DashboardShell>

    // <>
    //   <div className="h-full flex-1 flex-col space-y-8 p-8">
    //     <div className="flex items-center justify-between space-y-2">
    //       <div>
    //         <h2 className="text-2xl font-bold tracking-tight">Welcome back!</h2>
    //         <p className="text-muted-foreground">
    //           Here&apos;s a list of your tasks for this month!
    //         </p>
    //       </div>
    //       <div className="flex items-center space-x-2">
    //         <UserNav />
    //       </div>
    //     </div>
    //     <DataTable columns={columns} data={achievements || []} />
    //   </div>
    // </>
  );
}
