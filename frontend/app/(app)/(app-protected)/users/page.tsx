"use client";
import { DashboardShell } from "@/components/shell";
import { DashboardHeader } from "@/components/header";
import { API_URL, makeAppAuthHeader } from "@/hooks/api";
import { useCurrentApp } from "@/lib/current-app";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { AppUserResponse } from "@/types/generated-types";
import { UserCreateForm } from "@/components/create-user-form";

export default function Page() {
  const { currentApp } = useCurrentApp();
  const queryClient = useQueryClient();

  async function fetchUsers(): Promise<AppUserResponse[]> {
    const res = await fetch(`${API_URL}/apps/users`, {
      headers: makeAppAuthHeader(currentApp),
    });
    return await res.json();
  }

  const { data: users } = useQuery({
    queryKey: ["users"],
    queryFn: () => fetchUsers(),
  });

  return (
    <DashboardShell>
      <DashboardHeader heading="Users" text="Create and manage your app users.">
        <UserCreateForm />
      </DashboardHeader>
      <div>
        <div className="divide-y divide-border rounded-md border">
          {users?.map((user) => (
            <div
              className="flex items-center justify-between p-4"
              key={user.userId}
            >
              <div className="grid gap-1">
                {user.displayName}
                <div>
                  <p className="text-sm text-muted-foreground">{user.userId}</p>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </DashboardShell>
  );
}
