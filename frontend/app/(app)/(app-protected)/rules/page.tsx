"use client";
import { DashboardHeader } from "../../../../components/header";
import { RuleCreateButton } from "../../../../components/rule-create-button";
import { DashboardShell } from "../../../../components/shell";
import { useRules } from "@/hooks";
import Link from "next/link";
import { DataTable } from "@/app/(app)/examples/tasks/components/data-table";
import * as React from "react";
import { columns } from "@/app/(app)/(app-protected)/rules/components/columns";
import { resolveBasePath } from "@/lib/base-path";

export default function RulesPage() {
  const { data: rules, isLoading } = useRules();

  if (isLoading || !rules) return "Loading...";

  return (
    <DashboardShell>
      <DashboardHeader heading="Rules" text="Create and manage rules.">
        <Link href={resolveBasePath("/rules/new")}>
          <RuleCreateButton />
        </Link>
      </DashboardHeader>
      <DataTable columns={columns} data={rules} />
    </DashboardShell>
  );
}
