"use client";
import { useCurrentApp } from "@/lib/current-app";
import { resolveBasePath } from "@/lib/base-path";
import { Button } from "@/components/ui/button";
import { Icons } from "@/components/icons";
import React from "react";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Textarea } from "@/components/ui/textarea";
import { toast } from "@/components/ui/use-toast";

export default function Page() {
  const { currentApp } = useCurrentApp();

  if (!currentApp) {
    return null;
  }

  const path = resolveBasePath(`/embed/apps/${currentApp?.id}/leaderboard`);
  const url = new URL(path, window.location.origin).toString();

  const iframeCode = `<iframe src="${url}" width="100%" height="100%" frameBorder="0" />`;
  return (
    <div className="h-full flex flex-col gap-4">
      <div className="flex gap-2 self-center mr-8">
        <div className="border border-muted-foreground/50 text-primary/80 px-4 py-2 text-sm rounded">
          {url}
        </div>
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
                Copy and paste the code below to embed this leaderboard
              </p>
            </DialogHeader>
            <Textarea value={iframeCode} readOnly />
          </DialogContent>
        </Dialog>
      </div>
      <iframe
        src={path}
        width="100%"
        height="100%"
        allowFullScreen={true}
        className="overflow-hidden border-none max-w-5xl mx-auto"
      />
    </div>
  );
}
