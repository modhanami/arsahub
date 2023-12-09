import { DashboardHeader } from "../../../../../../../components/header";
import { PlaygroundTriggerForm } from "../../../../../../../components/playground-trigger";
import { DashboardShell } from "../../../../../../../components/shell";
import { ContextProps } from "../../../../../../../types";

export type Playground = {
  id: number;
  title: string;
  description: string;
} & ContextProps;

export default async function PlaygroundPage({ params }: ContextProps) {
  return (
    <DashboardShell>
      <DashboardHeader
        heading="Playground"
        text="Test your rules."
      ></DashboardHeader>
      <div>
        <PlaygroundTriggerForm
          activityId={Number(params.id)}
          appId={Number(params.appId)}
        />
      </div>
    </DashboardShell>
  );
}
