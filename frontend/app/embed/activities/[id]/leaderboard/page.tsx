"use client";

import {ContextProps} from "../../../../../types";
import {LeaderboardResponse} from "../../../../../types/generated-types";
import {useAutoAnimate} from "@formkit/auto-animate/react";

import {Table, TableBody, TableCell, TableHead, TableHeader, TableRow,} from "@/components/ui/table";
import {useQuery, useQueryClient} from "@tanstack/react-query";
import io from "socket.io-client";
import {API_URL} from "@/hooks/api";
import React, {useEffect} from "react";
import {Avatar, AvatarFallback, AvatarImage} from "@/components/ui/avatar";

type Props = ContextProps;

export default function LeaderboardEmbedPage({params}: ContextProps) {
  const [animationLeaderboard] = useAutoAnimate();
  const leaderboard = useLeaderboard(Number(params.id), "total-points");

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
                <TableHead
                  className="px-5 py-3 border-b-2 border-gray-200 bg-gray-100 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  Rank
                </TableHead>
                <TableHead
                  className="px-5 py-3 border-b-2 border-gray-200 bg-gray-100 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  Name
                </TableHead>
                <TableHead
                  className="px-5 py-3 border-b-2 border-gray-200 bg-gray-100 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  Score
                </TableHead>
              </TableRow>
            </TableHeader>
            <TableBody ref={animationLeaderboard}>
              {leaderboard.leaderboard?.entries.map((entry) => (
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
  )
}

async function fetchLeaderboard(activityId: number, type: string): Promise<LeaderboardResponse> {
  const res = await fetch(`${API_URL}/activities/${activityId}/leaderboard?type=${type}`);
  return res.json();
}

const useLeaderboard = (activityId: number, type: string) => {
  const queryClient = useQueryClient();
  const {data: leaderboard, error, isLoading} = useQuery({
    queryKey: ['leaderboard', {
      activityId,
      type
    }],
    queryFn: () => fetchLeaderboard(activityId, type)
  });

  useEffect(() => {
    const socket = io("http://localhost:9097/default");

    socket.on("connect", async () => {
      const result = await socket.emitWithAck("subscribe-activity", activityId);
      console.log(result);
    });

    socket.on("activity-update", (updatedData) => {
      if (updatedData.type === 'leaderboard-update')
        console.log(`leaderboard-update: ${JSON.stringify(updatedData)}`);
      queryClient.setQueryData(['leaderboard', {
        activityId,
        type
      }], updatedData.data.leaderboard);
    });

    return () => {
      socket.disconnect();
    } // Disconnect when component is unmounted or leaderboard is updated

  }, [activityId, queryClient, type]);

  return {leaderboard, error, isLoading};
}
