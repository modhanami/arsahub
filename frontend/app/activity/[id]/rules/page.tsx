import { CreateRuleForm } from "../../../../components/create-rule";
import { EmptyPlaceholder } from "../../../../components/empty-placeholder";
import { DashboardHeader } from "../../../../components/header";
import { RuleCreateButton } from "../../../../components/rule-create-button";
import { RuleItem } from "../../../../components/rule-item";
import { DashboardShell } from "../../../../components/shell";
import { toast } from "../../../../components/ui/use-toast";

export interface Rule {
  id: number;
  title: string;
  description: string;
}
export default async function RulesPage({
  params,
}: {
  params: { id: string };
}) {
  const response = await fetch(
    `http://localhost:8080/api/activities/${params.id}/rules`,
    {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
      next: {
        tags: [`rules`],
      },
    }
  );

  if (!response?.ok) {
    return toast({
      title: "Something went wrong.",
      description: "Your rule was not created. Please try again.",
      variant: "destructive",
    });
  }

  const rules: Rule[] = await response.json();

  return (
    <DashboardShell>
      <DashboardHeader heading="Rules" text="Create and manage rules.">
        <CreateRuleForm />
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
