"use client";
import React from "react";
import { DashboardShell } from "@/components/shell";
import { DashboardHeader } from "@/components/header";
import { fetchUsers } from "@/hooks/api";
import { useCurrentApp } from "@/lib/current-app";
import { useQuery } from "@tanstack/react-query";
import { UserCreateForm } from "@/components/create-user-form";

export default function Page() {
  const { currentApp } = useCurrentApp();

  const { data: users, isLoading } = useQuery({
    queryKey: ["users"],
    queryFn: () => currentApp && fetchUsers(currentApp),
    enabled: !!currentApp,
  });

  return (
    <DashboardShell>
      <DashboardHeader heading="Users" text="Create and manage your app users.">
        <UserCreateForm />
      </DashboardHeader>
      {isLoading ? (
        <div>Loading...</div>
      ) : (
        <div>
          <div className="divide-y divide-border rounded-md border">
            {users &&
              users.map((user) => (
                <div
                  className="flex items-center justify-between p-4"
                  key={user.userId}
                >
                  <div className="grid gap-1">
                    {user.displayName}
                    <div>
                      <p className="text-sm text-muted-foreground">
                        {user.userId}
                      </p>
                    </div>
                  </div>
                </div>
              ))}
          </div>
        </div>
      )}
    </DashboardShell>
  );
}
