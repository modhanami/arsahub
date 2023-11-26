import ActivityForm from "@/components/activity-form";
import { DashboardHeader } from "@/components/header";
import { DashboardShell } from "@/components/shell";

export default async function ActivityPage() {
  return (
    <DashboardShell>
      <DashboardHeader
        heading="Activity"
        text="Manage activity and website settings."
      />
      <div className="grid gap-10">
        <ActivityForm />
      </div>
    </DashboardShell>
  );
}
