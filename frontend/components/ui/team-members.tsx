"use client";

import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Card, CardContent } from "@/components/ui/card";
import { useEffect, useState } from "react";

// {
//     achievementId: 2,
//     title: 'Achievement 2',
//     description: null,
//     imageUrl: null
//   }

interface UserProfileProps {
  name: string;
  username: string;
  // role: string;
  avatar: string;
  points: number;
  achievements: AchievementResponse[];
}

export function UserProfile({
  name,
  username,
  avatar,
  points,
  achievements,
}: UserProfileProps) {
  console.log(achievements);
  const tempAvatar = `https://avatar.vercel.sh/${username}.jpeg`;

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
              <p className="text-sm text-muted-foreground">{username}</p>
            </div>

            <div className="flex items-center space-x-4">
              <p className=" font-medium text-xl leading-none text-amber-200">
                {points.toLocaleString()}
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

import Image from "next/image";
import { io } from "socket.io-client";
import { AchievementResponse } from "../../types/generated-types";
import { Badge } from "./badge";

export function UserProfileRealTime({
  userId,
  ...props
}: UserProfileRealTimeProps & UserProfileProps) {
  const [points, setPoints] = useState(props.points);

  useEffect(() => {
    const socket = io("http://localhost:9097/default");
    socket.on("connect", async () => {
      console.log("connected");
      const response = await socket.emitWithAck("subscribe-user", userId);
      console.log(response);
    });

    socket.on("user-update", (data) => {
      console.log(`user-update: ${JSON.stringify(data)}`);
      const { points } = data.data;
      setPoints(points);
    });
  }, []);

  return <UserProfile {...props} points={points} />;
}
