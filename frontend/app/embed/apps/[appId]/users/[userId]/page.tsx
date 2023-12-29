"use client";

import { UserProfileRealTime } from "../../../../../../components/ui/team-members";
import { API_URL } from "../../../../../../api";
import { ContextProps } from "../../../../../../types";
import { useQuery } from "@tanstack/react-query";
import { AppUserResponse } from "@/types/generated-types";
import { useCurrentApp } from "@/lib/current-app";

async function fetchUser(
  appId: number,
  userId: string,
): Promise<AppUserResponse> {
  const res = await fetch(`${API_URL}/apps/${appId}/users/${userId}`);

  const data = await res.json();
  if (!res.ok) {
    throw new Error(data);
  }

  return data;
}

export default function Page({ params }: ContextProps) {
  console.log("params", params);
  const { currentApp } = useCurrentApp();

  const userId = params.userId;
  const { data, error, isLoading } = useQuery({
    queryKey: ["user", currentApp && currentApp.id, userId],
    queryFn: () => currentApp && fetchUser(currentApp.id, userId),
    enabled: !!currentApp,
  });

  if (isLoading) return "Loading...";
  if (error) return "An error has occurred: " + error.message;

  if (!currentApp) {
    return <div>Loading...</div>;
  }

  if (data === null) {
    return <div>User not found</div>;
  }

  return (
    <main>
      <UserProfileRealTime
        userId={userId}
        name={data?.displayName || ""}
        avatar="X"
        points={data?.points || 0}
        achievements={data?.achievements || []}
      />
    </main>
  );
}
