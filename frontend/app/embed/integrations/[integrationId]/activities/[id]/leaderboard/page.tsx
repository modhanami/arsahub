import { ContextProps } from "../../../../../../../types";
import { LeaderboardResponse } from "../../../../../../../types/generated-types";

type Props = ContextProps;

export default async function LeaderboardEmbedPage({ params }: ContextProps) {
  const leaderboard = await getLeaderboard(Number(params.id), "total-points");
  return (
    <div>
      <h1>Leaderboard</h1>
      <div>ID: {params.id}</div>
      <div>{JSON.stringify(leaderboard, null, 2)}</div>
    </div>
  );
}

async function getLeaderboard(
  activityId: number,
  type: "total-points"
): Promise<LeaderboardResponse> {
  // TODO: type should be named board or something else, since type is more suited for time period, like "all-time" or "last-week"
  const res = await fetch(
    `http://localhost:8080/api/activities/${activityId}/leaderboard?type=${type}`
  );
  const data = await res.json();
  return data;
}
