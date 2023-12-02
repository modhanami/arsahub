import { UserProfileRealTime } from "../../../../../../../components/ui/team-members";
import { API_URL } from "../../../../../../../hooks/api";
import { ContextProps } from "../../../../../../../types";
import { UserActivityProfileResponse } from "../../../../../../../types/generated-types";

async function getUserProfile(
  activityId: string,
  userId: string
): Promise<UserActivityProfileResponse> {
  const res = await fetch(`${API_URL}/activities/1/profile?userId=${userId}`);

  if (!res.ok) {
    throw new Error("Failed to fetch data");
  }

  return res.json();
}

type Props = ContextProps & {
  searchParams: {
    userId: string;
  };
};

export default async function Page({
  params,
  searchParams: { userId },
}: Props) {
  const data = await getUserProfile(params.id, userId);
  console.log(data);

  if (data.user === null) {
    return <div>User not found</div>;
  }

  return (
    <main>
      <UserProfileRealTime
        userId={userId}
        name={data.user.name}
        username={data.user.username || ""}
        avatar="X"
        points={data.points}
        achievements={data.achievements || []}
      />
    </main>
  );
}
