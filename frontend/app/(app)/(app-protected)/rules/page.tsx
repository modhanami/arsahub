"use client";
import { EmptyPlaceholder } from "../../../../components/empty-placeholder";
import { DashboardHeader } from "../../../../components/header";
import { RuleCreateButton } from "../../../../components/rule-create-button";
import { RuleItem } from "../../../../components/rule-item";
import { DashboardShell } from "../../../../components/shell";
import { useRules } from "@/hooks";
import Link from "next/link";

export default function RulesPage() {
  const { data: rules, isLoading } = useRules();

  if (isLoading) return "Loading...";

  return (
    <DashboardShell>
      <DashboardHeader heading="Rules" text="Create and manage rules.">
        <Link href={"/rules/new"}>
          <RuleCreateButton />
        </Link>
      </DashboardHeader>
      <div>
        {rules?.length ? (
          <div className="divide-y divide-border rounded-md border">
            {rules.map((rule) => (
              <RuleItem key={rule.id} rule={rule} />
            ))}
          </div>
        ) : (
          <EmptyPlaceholder>
            <EmptyPlaceholder.Icon name="rule" />
            <EmptyPlaceholder.Title>No rules created</EmptyPlaceholder.Title>
            <EmptyPlaceholder.Description>
              This rule don&apos;t have any rules yet.
            </EmptyPlaceholder.Description>
            <RuleCreateButton variant="outline" />
          </EmptyPlaceholder>
        )}
      </div>
    </DashboardShell>
  );
}
