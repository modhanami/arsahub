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
import { useQueryClient } from "@tanstack/react-query";
import io from "socket.io-client";
import React, { useEffect } from "react";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { SOCKET_IO_URL } from "@/lib/socket";
import { useLeaderboard } from "@/hooks";

export default function LeaderboardEmbedPage({ params }: ContextProps) {
  const [animationLeaderboard] = useAutoAnimate();
  const appId = Number(params.appId);
  const type = "total-points";

  const queryClient = useQueryClient();
  const { data: leaderboard, error, isLoading } = useLeaderboard(appId, type);

  useEffect(() => {
    const socket = io(`${SOCKET_IO_URL}/default`, {
      forceNew: true,
      timestampRequests: true,
    });

    socket.on("connect", async () => {
      const response = await socket.emitWithAck("subscribe-activity", appId);
      console.log("subscribe-activity result", response);
    });

    socket.on("activity-update", (updatedData: any) => {
      if (updatedData.type === "leaderboard-update")
        console.log(`leaderboard-update: ${JSON.stringify(updatedData)}`);
      queryClient.setQueryData(
        [
          "leaderboard",
          {
            activityId: appId,
            type,
          },
        ],
        updatedData.data.leaderboard,
      );
    });

    return () => {
      if (socket.connected) {
        socket.disconnect();
      }
    }; // Disconnect when component is unmounted or leaderboard is updated
  }, [appId, queryClient, type]);

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
              {leaderboard?.entries.map((entry) => (
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
