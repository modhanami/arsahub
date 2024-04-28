"use client";
import React from "react";
import { DashboardShell } from "@/components/shell";
import { DashboardHeader } from "@/components/header";
import { UserCreateForm } from "@/components/create-user-form";
import { useAppUserPointsHistory, useAppUsers } from "@/hooks";
import { DataTable } from "@/app/(app)/examples/tasks/components/data-table";
import { columns } from "@/app/(app)/(app-protected)/users/components/columns";
import { AppUserResponse } from "@/types/generated-types";
import { resolveBasePath } from "@/lib/base-path";
import { useCurrentApp } from "@/lib/current-app";
import { cn, datetimeFormatter, numberFormatter } from "@/lib/utils";
import { Badge } from "@/components/ui/badge";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Icons } from "@/components/icons";
import { toast } from "@/components/ui/use-toast";
import { Textarea } from "@/components/ui/textarea";

export default function Page() {
  const { currentApp, isLoading: isAppLoading } = useCurrentApp();
  const { data: users, isLoading } = useAppUsers();
  const [selectedAppUser, setSelectedAppUser] =
    React.useState<AppUserResponse | null>(null);
  const { data: pointsHistory } = useAppUserPointsHistory(
    selectedAppUser?.userId || "",
  );

  if (isLoading || !users || isAppLoading || !currentApp) return "Loading...";

  const iframeTemplate = resolveBasePath(
    `/embed/apps/${currentApp.id}/users/<userId>`,
  );

  const url = new URL(iframeTemplate, window.location.origin).toString();

  const iframeCode = `<iframe src="${decodeURIComponent(
    url,
  )}" width="100%" height="100%" frameBorder="0" />`;

  return (
    <DashboardShell>
      <DashboardHeader
        heading="App Users"
        text="Create and manage your app users."
      >
        <div className="flex gap-2">
          <UserCreateForm />
          <Dialog>
            <DialogTrigger>
              <Button variant="secondary">
                <Icons.code className="w-4 h-4 mr-2" />
                <span>Embed</span>
              </Button>
            </DialogTrigger>
            <DialogContent>
              <DialogHeader>
                <div className="flex gap-4 items-center">
                  <h2 className="text-lg font-bold">Embed Code</h2>
                  <Button
                    size="sm"
                    onClick={() =>
                      navigator.clipboard.writeText(iframeCode).then(() => {
                        toast({
                          title: "Copied to clipboard",
                        });
                      })
                    }
                  >
                    <Icons.copy className="w-4 h-4" />
                  </Button>
                </div>
                <p className="text-sm text-muted-foreground">
                  Copy, modify{" "}
                  <span className="font-semibold">{"<userId>"}</span>, and paste
                  the code below to embed this user profile
                </p>
              </DialogHeader>
              <Textarea value={iframeCode} readOnly />
            </DialogContent>
          </Dialog>
        </div>
      </DashboardHeader>
      {/*<div className="grid lg:grid-cols-[1fr_400px] gap-8">*/}
      <div
        className={cn("grid gap-8", {
          "lg:grid-cols-[1fr_400px]": selectedAppUser !== null,
          "lg:grid-cols-1": selectedAppUser === null,
        })}
      >
        <DataTable
          columns={columns}
          data={users}
          onRowClick={(row) => {
            setSelectedAppUser(row.original);
            row.toggleSelected(true);
          }}
          enableMultiSelect={false}
        />

        <Tabs defaultValue="profile" className="w-[400px]">
          <TabsList className="grid w-full grid-cols-2">
            <TabsTrigger value="profile">Profile</TabsTrigger>
            <TabsTrigger value="pointsHistory">Points History</TabsTrigger>
          </TabsList>
          <TabsContent value="profile">
            {selectedAppUser && (
              <iframe
                src={resolveBasePath(
                  `/embed/apps/${currentApp.id}/users/${selectedAppUser.userId}`,
                )}
                width="100%"
                height="100%"
                allowFullScreen={true}
                className="overflow-hidden border-none sticky top-0 h-[500px]"
              />
            )}
          </TabsContent>
          <TabsContent value="pointsHistory">
            {(pointsHistory && pointsHistory.length > 0 && (
              <div className="flex flex-col my-2 h-[600px] overflow-auto">
                {pointsHistory.map((history) => (
                  <div
                    key={history.id}
                    className="flex gap-4 hover:bg-muted/50 p-2 rounded"
                  >
                    {/*  delta */}
                    <div className="flex flex-col items-end">
                      <div
                        className={cn("font-bold text-lg", {
                          "text-green-500": history.pointsChange >= 0,
                          "text-red-500": history.pointsChange < 0,
                        })}
                      >
                        {history.points >= 0 ? "+" : ""}
                        {numberFormatter.format(history.pointsChange)}
                      </div>
                      {/*  total */}
                      <div className="text-sm text-muted-foreground">
                        = {numberFormatter.format(history.points)}
                      </div>
                    </div>
                    <div className="flex flex-col gap-1">
                      <div>
                        {history.fromRule ? (
                          <span>
                            {`${history.fromRule.title}`}
                            <Badge variant="outline">
                              ID: {history.fromRule.id}
                            </Badge>
                          </span>
                        ) : (
                          "Manual"
                        )}
                      </div>
                      <div className="text-xs text-muted-foreground">
                        {datetimeFormatter.format(new Date(history.createdAt))}
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )) || (
              <div className="text-center text-muted-foreground">
                No points history.
              </div>
            )}
          </TabsContent>
        </Tabs>
      </div>
    </DashboardShell>
  );
}
