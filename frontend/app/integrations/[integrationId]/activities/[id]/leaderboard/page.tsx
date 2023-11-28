"use client";
import { ContextProps } from "../../../../../../types";

type Props = ContextProps;

export default function Page({ params }: ContextProps) {
  return (
    <iframe
      src={`/embed/integrations/${params.integrationId}/activities/${params.id}/leaderboard`}
      width="100%"
      height="100%"
      allowFullScreen={true}
      className="overflow-hidden border-none"
    />
  );
}
