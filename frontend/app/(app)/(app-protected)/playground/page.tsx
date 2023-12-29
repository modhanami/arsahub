import { DashboardHeader } from "../../../../components/header";
import { PlaygroundTriggerForm } from "../../../../components/playground-trigger";
import { DashboardShell } from "../../../../components/shell";

export default async function PlaygroundPage() {
  return (
    <DashboardShell>
      <DashboardHeader
        heading="Playground"
        text="Test your rules."
      ></DashboardHeader>
      <div>
        <PlaygroundTriggerForm />
      </div>
    </DashboardShell>
  );
}
