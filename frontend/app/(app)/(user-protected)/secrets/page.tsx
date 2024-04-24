"use client";
import React, { useState } from "react";
import { Input } from "../../../../components/ui/input";
import { Button } from "../../../../components/ui/button";
import { DashboardHeader } from "../../../../components/header";
import { DashboardShell } from "../../../../components/shell";
import { toast } from "../../../../components/ui/use-toast";
import { useOwnedApp } from "@/hooks";

export default function Page() {
  const { data, isLoading } = useOwnedApp();
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

  if (!data) {
    return null;
  }

  return (
    <DashboardShell>
      <DashboardHeader
        heading="Secrets"
        text="You can grab your app's API key here."
      ></DashboardHeader>
      <div className="flex flex-col gap-2">
        <div className="sm:flex flex-row gap-4 my-4">
          {/*  App General Info: Name and ID */}
          <div className="flex flex-col gap-2">
            <h3 className="text-tremor-default text-tremor-content dark:text-dark-tremor-content">
              App Name
            </h3>
            <p className="text-tremor-content-strong dark:text-dark-tremor-content-strong">
              {data?.name}
            </p>
          </div>
          <div className="flex flex-col gap-2">
            <h3 className="text-tremor-default text-tremor-content dark:text-dark-tremor-content">
              App ID
            </h3>
            <p className="text-tremor-content-strong dark:text-dark-tremor-content-strong">
              {data?.id}
            </p>
          </div>
        </div>
        {data.apiKey && (
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
      </div>
    </DashboardShell>
  );
}
