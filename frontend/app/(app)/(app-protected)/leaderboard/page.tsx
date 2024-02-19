"use client";
import { useCurrentApp } from "@/lib/current-app";
import { resolveBasePath } from "@/lib/base-path";

export default function Page() {
  const { currentApp } = useCurrentApp();

  if (!currentApp) {
    return null;
  }

  return (
    <iframe
      src={resolveBasePath(`/embed/apps/${currentApp?.id}/leaderboard`)}
      width="100%"
      height="100%"
      allowFullScreen={true}
      className="overflow-hidden border-none"
    />
  );
}
