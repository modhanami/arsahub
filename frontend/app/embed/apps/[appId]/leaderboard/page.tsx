"use client";

import { ContextProps } from "../../../../../types";
import { useAutoAnimate } from "@formkit/auto-animate/react";

import { Table, TableBody, TableCell, TableRow } from "@/components/ui/table";
import React, { useEffect } from "react";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { useSocket } from "@/lib/socket";
import { useLeaderboard } from "@/hooks";
import {
  Entry,
  LeaderboardResponse,
  LeaderboardUpdate,
} from "@/types/generated-types";
import { useDebounceCallback } from "usehooks-ts";
import { cn, numberFormatter } from "@/lib/utils";

function NewComponent({
  entry,
  className,
  classNameScore,
}: {
  entry: Entry;
  className: string;
  classNameScore?: string;
}) {
  return (
    <div
      className={cn(
        "flex items-center justify-between text-white px-6 py-4",
        className,
      )}
    >
      <div className="flex gap-4">
        <Avatar className="h-10 w-10 shadow-md">
          <AvatarImage
            src={`https://avatar.vercel.sh/${entry.memberName}.png`}
            width={64}
            alt={entry.memberName}
          />
          <AvatarFallback>AS</AvatarFallback>
        </Avatar>
        <div className="flex flex-col gap-1 drop-shadow-md">
          <span>{entry.memberName}</span>
          <span className="text-xs text-muted-foreground">{entry.userId}</span>
        </div>
      </div>
      <p className={cn("text-xl font-semibold drop-shadow-lg", classNameScore)}>
        {numberFormatter.format(entry.score)}
      </p>
    </div>
  );
}

export default function LeaderboardEmbedPage({ params }: ContextProps) {
  const [animationLeaderboard] = useAutoAnimate();
  const appId = Number(params.appId);
  const type = "total-points";

  const { data, error, isLoading } = useLeaderboard(appId, type);
  const [leaderboard, setLeaderboard] =
    React.useState<LeaderboardResponse | null>();
  const { socket, isConnected } = useSocket();

  function onLeaderboardUpdate(updatedData: any) {
    if (updatedData.type === "leaderboard-update")
      console.log("leaderboard-update", updatedData);
    const { leaderboard: newLeaderboard } =
      updatedData.data as LeaderboardUpdate;
    // TODO: check why newLeaderboard sometimes is undefined
    console.log("newLeaderboard", newLeaderboard);
    if (newLeaderboard) {
      setLeaderboard(newLeaderboard);
    }
  }

  const debouncedOnLeaderboardUpdate = useDebounceCallback(
    onLeaderboardUpdate,
    50,
  );

  useEffect(() => {
    if (data) {
      setLeaderboard(data);
    }
  }, [data]);

  useEffect(() => {
    if (!socket) return;

    if (isConnected) {
      console.log("connected");
      socket.emitWithAck("subscribe-activity", appId).then((response: any) => {
        console.log("subscribe-activity result", response);
      });
    }
  }, [socket, isConnected, appId]);

  useEffect(() => {
    if (!socket) return;

    socket.on("activity-update", debouncedOnLeaderboardUpdate);

    return () => {
      socket.off("activity-update", debouncedOnLeaderboardUpdate);
    };
  }, [debouncedOnLeaderboardUpdate, setLeaderboard, socket]);

  if (!leaderboard) return "Loading...";

  const entries = leaderboard.entries;
  const topThree = entries.slice(0, 3);
  const rest = entries.slice(3);

  return (
    <div className="py-6">
      <p className="text-xl font-semibold mb-4 text-center">
        Total Points Leaderboard
      </p>
      <div className="my-8">
        <div className="inline-block min-w-full shadow-md rounded-lg overflow-hidden">
          {/* Top 3 horizontal: platinum, gold, silver */}
          {topThree[0] && (
            <NewComponent
              entry={topThree[0]}
              className="bg-royal"
              classNameScore="text-amber-200 text-3xl"
            />
          )}
          {topThree[1] && (
            <NewComponent
              entry={topThree[1]}
              className="bg-deep-space"
              classNameScore="text-2xl"
            />
          )}
          {topThree[2] && (
            <NewComponent entry={topThree[2]} className="bg-hersheys" />
          )}
          <Table className="min-w-full leading-normal mt-4">
            <TableBody ref={animationLeaderboard}>
              {rest.map((entry) => (
                <TableRow key={entry.memberName} className="border-t">
                  <TableCell className="px-6 py-4">{entry.rank}</TableCell>
                  <TableCell className="px-6 py-4">
                    <div className="flex gap-4">
                      <Avatar className="h-5 w-5">
                        <AvatarImage
                          src={`https://avatar.vercel.sh/${entry.memberName}.png`}
                          alt={entry.memberName}
                        />
                        <AvatarFallback>AS</AvatarFallback>
                      </Avatar>
                      {entry.memberName}
                    </div>
                  </TableCell>
                  <TableCell className="px-6 py-4">
                    {numberFormatter.format(entry.score)}
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </div>
      </div>
    </div>
  );
}
