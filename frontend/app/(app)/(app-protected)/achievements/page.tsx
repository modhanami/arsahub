"use client";
import { DashboardShell } from "@/components/shell";
import { DashboardHeader } from "@/components/header";
import { useAchievements } from "@/hooks";
import { AchievementCreateForm } from "@/components/create-achievement";

export default function Page() {
  const { data: achievements, isLoading, isError } = useAchievements();

  if (isLoading) return "Loading...";
  if (isError) return "An error occurred";

  return (
    <DashboardShell>
      <DashboardHeader
        heading="Achievements"
        text="Create and manage achievements."
      >
        <AchievementCreateForm />
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
