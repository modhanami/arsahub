"use client";
import { useCurrentApp } from "@/lib/current-app";

export default function Page() {
  const { currentApp } = useCurrentApp();

  if (!currentApp) {
    return null;
  }

  return (
    <iframe
      src={`/embed/apps/${currentApp?.id}/leaderboard`}
      width="100%"
      height="100%"
      allowFullScreen={true}
      className="overflow-hidden border-none"
    />
  );
}
