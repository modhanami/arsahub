// import { authOptions } from "@/lib/auth"
// import { db } from "@/lib/db"
"use client";
import { ActivityCreateButton } from "@/components/activity-create-button";
import { ActivityItem } from "@/components/activity-item";
import { EmptyPlaceholder } from "@/components/empty-placeholder";
import { DashboardHeader } from "@/components/header";
import { DashboardShell } from "@/components/shell";
import { CardWithForm } from "../../../../../components/create-activity";
import { useActivities } from "../../../../../hooks/api";
import { ContextProps } from "../../../../../types";

type DashboardPageProps = ContextProps;

export default function DashboardPage({
  params: { id, appId },
}: DashboardPageProps) {
  const activities = useActivities();

  return (
    <DashboardShell>
      <DashboardHeader
        heading="Activities"
        text="Create and manage activities."
      >
        <CardWithForm />
      </DashboardHeader>
      <div>
        {activities?.length ? (
          <div className="divide-y divide-border rounded-md border">
            {activities.map((activity) => (
              <ActivityItem
                key={activity.id}
                activity={activity}
                appId={appId}
              />
            ))}
          </div>
        ) : (
          <EmptyPlaceholder>
            <EmptyPlaceholder.Icon name="activity" />
            <EmptyPlaceholder.Title>
              No activities created
            </EmptyPlaceholder.Title>
            <EmptyPlaceholder.Description>
              You don&apos;t have any activities yet. Start creating content.
            </EmptyPlaceholder.Description>
            <ActivityCreateButton variant="outline" />
          </EmptyPlaceholder>
        )}
      </div>
    </DashboardShell>
  );
}
