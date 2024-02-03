"use client";

import { useRewards } from "@/hooks";
import { DashboardShell } from "@/components/shell";
import { DashboardHeader } from "@/components/header";
import { DataTable } from "@/app/(app)/examples/tasks/components/data-table";
import * as React from "react";
import { columns } from "@/app/(app)/(app-protected)/rewards/components/columns";
import { RewardCreateForm } from "@/components/create-reward";

export default function RewardsPage() {
  const { data: rewards, isLoading } = useRewards();

  if (isLoading || !rewards) return "Loading...";

  return (
    <DashboardShell>
      <DashboardHeader heading="Rewards" text="Create and manage rewards.">
        <RewardCreateForm />
      </DashboardHeader>
      <DataTable columns={columns} data={rewards} />
    </DashboardShell>
  );
}
