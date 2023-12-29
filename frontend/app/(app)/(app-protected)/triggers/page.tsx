"use client";
import { DashboardShell } from "@/components/shell";
import { DashboardHeader } from "@/components/header";
import { TriggerCreateForm } from "@/components/create-trigger";
import { useTriggers } from "@/hooks";

export default function Page() {
  const { data: triggers, isLoading } = useTriggers();

  if (isLoading) return "Loading...";

  return (
    <DashboardShell>
      <DashboardHeader heading="Triggers" text="Create and manage triggers.">
        <TriggerCreateForm />
      </DashboardHeader>
      <div>
        {triggers?.length && (
          <div className="divide-y divide-border rounded-md border">
            {triggers.map((trigger) => (
              <div
                className="flex items-center justify-between p-4"
                key={trigger.id}
              >
                <div className="grid gap-1">
                  {trigger.title}
                  <div>
                    <p className="text-sm text-muted-foreground">
                      {trigger.description}
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
