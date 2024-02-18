"use client";

import { ContextProps } from "../../../../../types";
import { useAutoAnimate } from "@formkit/auto-animate/react";

import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import React, { useEffect } from "react";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { useSocket } from "@/lib/socket";
import { useLeaderboard } from "@/hooks";
import {
  LeaderboardResponse,
  LeaderboardUpdate,
} from "@/types/generated-types";
import { useDebounceCallback } from "usehooks-ts";

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

  return (
    <div className="py-8">
      <p className="text-2xl font-semibold mb-4 text-center">
        Total Points Leaderboard
      </p>
      <div className="my-8">
        <div className="inline-block min-w-full shadow-md rounded-lg overflow-hidden">
          <Table className="min-w-full leading-normal">
            <TableHeader>
              <TableRow>
                <TableHead className="px-5 py-3 border-b-2 border-gray-200 bg-gray-100 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  Rank
                </TableHead>
                <TableHead className="px-5 py-3 border-b-2 border-gray-200 bg-gray-100 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  Name
                </TableHead>
                <TableHead className="px-5 py-3 border-b-2 border-gray-200 bg-gray-100 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  Score
                </TableHead>
              </TableRow>
            </TableHeader>
            <TableBody ref={animationLeaderboard}>
              {leaderboard.entries.map((entry) => (
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
                  <TableCell className="px-6 py-4">{entry.score}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </div>
      </div>
    </div>
  );
}
