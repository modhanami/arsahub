"use client";

import { UserProfile } from "../../../../../../components/ui/team-members";
import { ContextProps } from "../../../../../../types";
import { useAppUser } from "@/hooks";
import { useEffect, useState } from "react";
import { useSocket } from "@/lib/socket";
import { AchievementUnlock, PointsUpdate } from "@/types/generated-types";

export default function Page({ params }: ContextProps) {
  const appId = Number(params.appId);
  const userId = params.userId;
  const { data, error, isLoading } = useAppUser(appId, userId);
  const [points, setPoints] = useState(0);
  const [achievements, setAchievements] = useState(data?.achievements || []);
  const { socket, isConnected } = useSocket();

  useEffect(() => {
    if (!socket) return;

    if (isConnected) {
      console.log("connected");
      socket.emitWithAck("subscribe-user", userId).then((response: any) => {
        console.log("subscribe-user result", response);
      });
    }
  }, [socket, isConnected, userId]);

  useEffect(() => {
    function onUserUpdate(data: any) {
      console.log(`user-update: ${JSON.stringify(data)}`);
      if (data.type === "achievement-unlock") {
        const achievementsIds = new Set(
          achievements.map((a) => a.achievementId),
        );
        const { achievement } = data.data as AchievementUnlock;
        if (!achievementsIds.has(achievement.achievementId)) {
          achievementsIds.add(achievement.achievementId);
          setAchievements((prev) => [...prev, achievement]);
        }
      } else if (data.type === "points-update") {
        const { points } = data.data as PointsUpdate;
        setPoints(points);
      }
    }

    if (!socket) return;

    socket.on("user-update", onUserUpdate);

    return () => {
      socket.off("user-update", onUserUpdate);
    };
  }, [achievements, socket]);

  useEffect(() => {
    if (data) {
      setPoints(data.points);
      setAchievements(data.achievements);
    }
  }, [data]);

  if (isLoading) return "Loading...";
  if (error) return "An error has occurred: " + error.message;

  if (data === null) {
    return <div>User not found</div>;
  }

  return (
    <main>
      {isConnected ? "Connected" : "Disconnected"}
      {data && (
        <UserProfile
          points={points}
          achievements={achievements}
          userId={data.userId}
          name={data.displayName}
        />
      )}
    </main>
  );
}
