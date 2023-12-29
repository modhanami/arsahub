"use client";
import { useState } from "react";
import { Input } from "../../../../components/ui/input";
import { Button } from "../../../../components/ui/button";
import { DashboardHeader } from "../../../../components/header";
import { DashboardShell } from "../../../../components/shell";
import { toast } from "../../../../components/ui/use-toast";
import { useUserUuid } from "@/lib/current-user";
import { useAppByUserUUID } from "@/hooks";

export default function Page() {
  const { uuid } = useUserUuid();
  const { data, isLoading } = useAppByUserUUID(uuid);
  const [showSecret, setShowSecret] = useState(false);

  const copyToClipboard = async () => {
    if (!data?.apiKey) {
      return;
    }

    await navigator.clipboard.writeText(data.apiKey);
    toast({
      title: "Copied to clipboard",
      description: "Your API key was copied to clipboard.",
    });
  };

  if (isLoading) {
    return <div>Loading...</div>;
  }

  return (
    <DashboardShell>
      <DashboardHeader
        heading="Secrets"
        text="This is a secrets page. You can grab your API key here."
      ></DashboardHeader>
      {data?.apiKey && (
        <div>
          <p className="font-medium mt-4">Secret</p>
          <div className="flex items-center gap-2">
            <Input
              type={showSecret ? "text" : "password"}
              value={data?.apiKey}
              readOnly
              className="text-sm text-gray-500 max-w-sm"
            />
            <Button onClick={() => setShowSecret(!showSecret)}>
              {showSecret ? "Hide" : "Show"}
            </Button>
            {/*<Button onClick={copyToClipboard}>Copy</Button>*/}
          </div>
        </div>
      )}
    </DashboardShell>
  );
}
