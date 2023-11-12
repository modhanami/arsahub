import { EmptyPlaceholder } from "../../../../components/empty-placeholder";
import { DashboardHeader } from "../../../../components/header";
import { PlaygroundTriggerForm } from "../../../../components/playground-trigger";
import { DashboardShell } from "../../../../components/shell";
import { toast } from "../../../../components/ui/use-toast";
import { fetchMembers, fetchRules, fetchTriggers } from "../../../../lib/api";

export interface Playground {
  id: number;
  title: string;
  description: string;
}
export default async function PlaygroundPage({
  params,
}: {
  params: { id: string };
}) {
  const responses = await Promise.all([
    fetchMembers(Number(params.id)),
    fetchTriggers(Number(params.id)),
    fetchRules(Number(params.id)),
  ]);

  // if any of the responses are not ok, return toast
  if (!responses.every((response) => response.ok)) {
    return toast({
      title: "Something went wrong.",
      description: "Your rule was not created. Please try again.",
      variant: "destructive",
    });
  }

  const [members, triggers, rules] = await Promise.all(
    responses.map((response) => response.json())
  );

  return (
    <DashboardShell>
      <DashboardHeader
        heading="Playground"
        text="Test your rules."
      ></DashboardHeader>
      <div>
        <PlaygroundTriggerForm activityId={Number(params.id)} />
      </div>
    </DashboardShell>
  );
}
