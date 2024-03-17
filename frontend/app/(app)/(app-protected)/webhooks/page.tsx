"use client";
import { useWebhooks } from "@/hooks";
import { DashboardShell } from "@/components/shell";
import { DashboardHeader } from "@/components/header";
import { DataTable } from "@/app/(app)/examples/tasks/components/data-table";
import { columns } from "@/app/(app)/(app-protected)/webhooks/components/columns";
import React from "react";
import { WebhookCreateForm } from "@/app/(app)/(app-protected)/webhooks/create-webhook-form";

export default function Page() {
  const { data: webhooks, isLoading } = useWebhooks();

  if (isLoading || !webhooks) return "Loading...";

  return (
    <DashboardShell>
      <DashboardHeader
        heading="Webhooks"
        text="Create and manage your webhooks"
      >
        <WebhookCreateForm />
      </DashboardHeader>
      <DataTable columns={columns} data={webhooks} />
    </DashboardShell>
  );
}
