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
  );
}
