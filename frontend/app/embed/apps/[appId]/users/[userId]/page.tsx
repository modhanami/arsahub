"use client";

import { UserProfileRealTime } from "../../../../../../components/ui/team-members";
import { ContextProps } from "../../../../../../types";
import { useCurrentApp } from "@/lib/current-app";
import { useAppUser } from "@/hooks";

export default function Page({ params }: ContextProps) {
  console.log("params", params);
  const { currentApp } = useCurrentApp();

  const userId = params.userId;
  const { data, error, isLoading } = useAppUser(userId);

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
