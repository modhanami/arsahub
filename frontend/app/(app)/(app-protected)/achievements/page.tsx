"use client";
import { DashboardShell } from "@/components/shell";
import { DashboardHeader } from "@/components/header";
import { useQuery } from "@tanstack/react-query";
import { useCurrentApp } from "@/lib/current-app";
import { API_URL, makeAppAuthHeader } from "@/api";
import { AchievementResponse } from "@/types/generated-types";

export default function Page() {
  const { currentApp } = useCurrentApp();

  async function fetchAchievements(): Promise<AchievementResponse[]> {
    const response = await fetch(`${API_URL}/apps/achievements`, {
      headers: {
        "Content-Type": "application/json",
        ...makeAppAuthHeader(currentApp),
      },
    });
    return response.json();
  }

  const {
    data: achievements,
    isLoading,
    isError,
  } = useQuery({
    queryKey: ["achievements"],
    queryFn: () => fetchAchievements(),
  });

  if (isLoading) return "Loading...";
  if (isError) return "An error occurred";

  return (
    <DashboardShell>
      <DashboardHeader
        heading="Achievements"
        text="Create and manage achievements."
      >
        {/*<AchievementCreateForm activityId={activityId} />*/}
      </DashboardHeader>
      <div>
        {achievements?.length && (
          <div className="divide-y divide-border rounded-md border">
            {achievements.map((achievement) => (
              <div
                className="flex items-center justify-between p-4"
                key={achievement.achievementId}
              >
                <div className="grid gap-1">
                  {achievement.title}
                  <div>
                    <p className="text-sm text-muted-foreground">
                      {achievement.description}
                    </p>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </DashboardShell>
  );
}
