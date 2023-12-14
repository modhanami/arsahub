"use client";

import { UserProfileRealTime } from "../../../../../components/ui/team-members";
import { API_URL } from "../../../../../hooks/api";
import { ContextProps } from "../../../../../types";
import { UserActivityProfileResponse } from "../../../../../types/generated-types";
import { useQuery } from "@tanstack/react-query";

async function fetchUserProfile(
  activityId: string,
  userId: string,
): Promise<UserActivityProfileResponse> {
  const res = await fetch(
    `${API_URL}/activities/${activityId}/profile?userId=${userId}`,
  );

  const data = await res.json();
  if (!res.ok) {
    throw new Error(data);
  }

  return data;
}

type Props = ContextProps & {
  searchParams: {
    userId: string;
  };
};

export default function Page({ params, searchParams: { userId } }: Props) {
  const activityId = params.id;
  const { data, error, isLoading } = useQuery({
    queryKey: ["userProfile", activityId, userId],
    queryFn: () => fetchUserProfile(activityId, userId),
  });

  if (isLoading) return "Loading...";
  if (error) return "An error has occurred: " + error.message;

  if (data?.user === null) {
    return <div>User not found</div>;
  }

  return (
    <main>
      <UserProfileRealTime
        userId={userId}
        name={data?.user.displayName || ""}
        avatar="X"
        points={data?.points || 0}
        achievements={data?.achievements || []}
      />
    </main>
  );
}
