"use client";
import React from "react";
import { DashboardShell } from "@/components/shell";
import { DashboardHeader } from "@/components/header";
import { UserCreateForm } from "@/components/create-user-form";
import { useAppUsers } from "@/hooks";
import { DataTable } from "@/app/(app)/examples/tasks/components/data-table";
import { columns } from "@/app/(app)/(app-protected)/users/components/columns";

export default function Page() {
  const { data: users, isLoading } = useAppUsers();

  if (isLoading || !users) return "Loading...";

  return (
    <DashboardShell>
      <DashboardHeader
        heading="App Users"
        text="Create and manage your app users."
      >
        <UserCreateForm />
      </DashboardHeader>
      <DataTable columns={columns} data={users} />
    </DashboardShell>
  );
}
