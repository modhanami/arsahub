import { DashboardHeader } from "@/components/header";
import { ActivityCreateButton } from "@/components/activity-create-button";
import { ActivityItem } from "@/components/activity-item";
import { DashboardShell } from "@/components/shell";

export default function DashboardLoading() {
  return (
    <DashboardShell>
      <DashboardHeader
        heading="Activities"
        text="Create and manage acvitities."
      >
        <ActivityCreateButton />
      </DashboardHeader>
      <div className="divide-border-200 divide-y rounded-md border">
        <ActivityItem.Skeleton />
        <ActivityItem.Skeleton />
        <ActivityItem.Skeleton />
        <ActivityItem.Skeleton />
        <ActivityItem.Skeleton />
      </div>
    </DashboardShell>
  );
}
