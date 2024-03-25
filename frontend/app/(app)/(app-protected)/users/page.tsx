"use client";
import React from "react";
import { DashboardShell } from "@/components/shell";
import { DashboardHeader } from "@/components/header";
import { UserCreateForm } from "@/components/create-user-form";
import { useAppUsers } from "@/hooks";
import { DataTable } from "@/app/(app)/examples/tasks/components/data-table";
import { columns } from "@/app/(app)/(app-protected)/users/components/columns";
import { AppUserResponse } from "@/types/generated-types";
import { resolveBasePath } from "@/lib/base-path";
import { useCurrentApp } from "@/lib/current-app";

export default function Page() {
  const { currentApp, isLoading: isAppLoading } = useCurrentApp();
  const { data: users, isLoading } = useAppUsers();
  const [selectedAppUser, setSelectedAppUser] =
    React.useState<AppUserResponse>(null);

  if (isLoading || !users || isAppLoading || !currentApp) return "Loading...";

  return (
    <DashboardShell>
      <DashboardHeader
        heading="App Users"
        text="Create and manage your app users."
      >
        <UserCreateForm />
      </DashboardHeader>
      <div className="grid lg:grid-cols-[1fr_400px] gap-8">
        <DataTable
          columns={columns}
          data={users}
          onRowClick={(row) => {
            setSelectedAppUser(row);
          }}
        />
        {selectedAppUser && (
          <iframe
            src={resolveBasePath(
              `/embed/apps/${currentApp.id}/users/${selectedAppUser.userId}`,
            )}
            width="100%"
            height="100%"
            allowFullScreen={true}
            className="overflow-hidden border-none sticky top-0 h-[500px]"
          />
        )}
      </div>
    </DashboardShell>
  );
}
