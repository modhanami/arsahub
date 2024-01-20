"use client";
import { DashboardShell } from "@/components/shell";
import { DashboardHeader } from "@/components/header";
import { useTriggers } from "@/hooks";
import { Button } from "@/components/ui/button";
import { Icons } from "@/components/icons";
import * as React from "react";
import Link from "next/link";
import { DataTable } from "@/app/(app)/examples/tasks/components/data-table";
import { columns } from "@/app/(app)/(app-protected)/triggers/components/columns";

export default function Page() {
  const { data: triggers, isLoading } = useTriggers();

  if (isLoading || !triggers) return "Loading...";

  return (
    <DashboardShell>
      <DashboardHeader heading="Triggers" text="Create and manage triggers.">
        <Link href={"/triggers/new"}>
          <Button>
            <Icons.add className="mr-2 h-4 w-4" />
            New Trigger
          </Button>
        </Link>
      </DashboardHeader>
      {/*<div>*/}
      {/*  {triggers?.length && (*/}
      {/*    <div className="divide-y divide-border rounded-md border">*/}
      {/*      {triggers.map((trigger) => (*/}
      {/*        <div*/}
      {/*          className="flex items-center justify-between p-4"*/}
      {/*          key={trigger.id}*/}
      {/*        >*/}
      {/*          <div className="grid gap-1">*/}
      {/*            {trigger.title}*/}
      {/*            <div>*/}
      {/*              <p className="text-sm text-muted-foreground">*/}
      {/*                {trigger.description}*/}
      {/*              </p>*/}
      {/*            </div>*/}
      {/*          </div>*/}
      {/*        </div>*/}
      {/*      ))}*/}
      {/*    </div>*/}
      {/*  )}*/}
      {/*</div>*/}

      <DataTable columns={columns} data={triggers} />
    </DashboardShell>
  );
}
