import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Card, CardContent } from "@/components/ui/card";
import { useEffect, useRef, useState } from "react";
import Image from "next/image";
import { io, Socket } from "socket.io-client";
import {
  AchievementResponse,
  AchievementUnlock,
  PointsUpdate,
} from "../../types/generated-types";
import { Badge } from "./badge";

interface UserProfileProps {
  userId: string;
  name: string;
  avatar: string;
  points: number;
  achievements: AchievementResponse[];
}

export function UserProfile({
  userId,
  name,
  avatar,
  points,
  achievements,
}: UserProfileProps) {
  console.log(achievements);
  const tempAvatar = `https://avatar.vercel.sh/${userId}.jpeg`;

  return (
    <Card className="max-w-sm max-h-[500px] overflow-y-auto">
      <CardContent className="grid gap-6 mt-8">
        <div className="flex items-center justify-between space-x-4">
          <div className="flex flex-col gap-4 w-full items-center">
            <Avatar className="w-20 h-20">
              <AvatarImage src={tempAvatar} alt={name} />
              <AvatarFallback>{name[0]}</AvatarFallback>
            </Avatar>
            <div className="text-center grid gap-1">
              <p className="font-bold text-lg leading-none">{name}</p>
              {/*<p className="text-sm text-muted-foreground">{userId}</p>*/}
            </div>

            <div className="flex items-center space-x-4">
              <p className=" font-medium text-xl leading-none text-amber-200">
                {(points || 0).toLocaleString()}
              </p>
              <p className="text-sm font-medium leading-none">Points</p>
            </div>
          </div>
        </div>

        <div>
          <div className="font-semibold mb-2">
            Achievements
            <Badge className="ml-2" variant="outline">
              {achievements?.length}
            </Badge>
          </div>
          <ul className="space-y-4 my-4">
            {achievements?.length > 0
              ? achievements.map((achievement) => (
                  <li
                    className="font-medium flex gap-4"
                    key={achievement.achievementId}
                  >
                    <Image
                      src={achievement.imageUrl || ""}
                      width={52}
                      height={52}
                      alt={achievement.title}
                      className="rounded-full"
                    />
                    <div>
                      <p>{achievement.title}</p>
                      <p className="text-muted-foreground text-sm">
                        {achievement.description}
                      </p>
                    </div>
                  </li>
                ))
              : "No achievements :("}
          </ul>
        </div>
      </CardContent>
    </Card>
  );
}

// user profile real-time update with Socket.io
interface UserProfileRealTimeProps {
  userId: string;
}

export function UserProfileRealTime({
  ...props
}: UserProfileRealTimeProps & UserProfileProps) {
  const [points, setPoints] = useState(props.points);
  const [achievements, setAchievements] = useState(props.achievements);

  const socketRef = useRef<Socket | undefined>(undefined);
  useEffect(() => {
    const socket = io("http://localhost:9097/default");
    socketRef.current = socket; // Store the connection in the ref

    socket.on("connect", async () => {
      console.log("connected");
      const response = await socket.emitWithAck("subscribe-user", props.userId);
      console.log(response);
    });

    // Return a clean-up function
    return () => {
      if (socket.connected) {
        socket.disconnect();
      }
    };
  }, [props.userId]); // Dependencies for the effect

  // Set up the WebSocket message handler
  useEffect(() => {
    const achievementsIds = new Set(achievements.map((a) => a.achievementId)); // Separate state for the IDs is too noisy

    const handler = (data: any) => {
      console.log(`user-update: ${JSON.stringify(data)}`);
      if (data.type === "achievement-unlock") {
        const { achievement } = data.data as AchievementUnlock;
        if (!achievementsIds.has(achievement.achievementId)) {
          achievementsIds.add(achievement.achievementId);
          setAchievements((prev) => [...prev, achievement]);
        }
      } else if (data.type === "points-update") {
        const { points } = data.data as PointsUpdate;
        setPoints(points);
      }
    };

    // Register the handler
    socketRef.current?.on("user-update", handler);

    // Return a clean-up function
    return () => {
      // Remove the handler when the effect is cleaned up
      socketRef.current?.off("user-update", handler);
    };
  }, [achievements]); // Dependencies for the effect

  return <UserProfile {...props} points={points} achievements={achievements} />;
}
