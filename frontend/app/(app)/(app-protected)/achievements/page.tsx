"use client";
import { DashboardShell } from "@/components/shell";
import { DashboardHeader } from "@/components/header";
import { useAchievements } from "@/hooks";
import { AchievementCreateForm } from "@/components/create-achievement";
import { Image } from "@nextui-org/react";
import { getImageUrlFromKey } from "@/lib/image";

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
                className="flex items-center gap-4 p-4"
                key={achievement.achievementId}
              >
                {achievement.imageKey && (
                  <Image
                    src={getImageUrlFromKey(achievement.imageKey)}
                    width={64}
                    height={64}
                    alt={`achievement image ${achievement.title}`}
                  />
                )}

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
