"use client";

import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Card, CardContent } from "@/components/ui/card";
import { AchievementResponse } from "../../types/generated-types";
import { Badge } from "./badge";
import { getImageUrlFromKey } from "@/lib/image";
import { Image } from "@nextui-org/react";

interface UserProfileProps {
  userId: string;
  name: string;
  points: number;
  achievements: AchievementResponse[];
}

export function UserProfile({
  userId,
  name,
  points,
  achievements,
}: UserProfileProps) {
  const tempAvatar = `https://avatar.vercel.sh/${userId}.jpeg`;

  return (
    <Card className="overflow-y-auto">
      <CardContent className="grid gap-6 mt-8">
        <div className="flex items-center justify-between space-x-4">
          <div className="flex flex-col gap-4 w-full items-center">
            <Avatar className="w-20 h-20">
              <AvatarImage src={tempAvatar} alt={name} />
              <AvatarFallback>{name[0]}</AvatarFallback>
            </Avatar>
            <div className="text-center grid gap-1">
              <p className="font-bold text-lg leading-none">{name}</p>
              <p className="text-sm text-muted-foreground">ID: {userId}</p>
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
          <ul className="my-4">
            {achievements?.length > 0
              ? achievements.map((achievement) => (
                  <li
                    className="font-medium flex gap-4 border-b border-foreground/10 py-4"
                    key={achievement.achievementId}
                  >
                    <Image
                      src={
                        (achievement.imageKey &&
                          getImageUrlFromKey(achievement.imageKey)) ||
                        ""
                      }
                      radius="none"
                      width={64}
                      classNames={{
                        wrapper: "mr-2 flex items-start min-w-[64px] ",
                      }}
                      alt={achievement.title}
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
