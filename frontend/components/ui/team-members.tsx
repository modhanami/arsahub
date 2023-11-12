"use client";

import { ChevronDownIcon } from "lucide-react";

import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import {
  Command,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
  CommandList,
} from "@/components/ui/command";
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover";
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

  return (
    <Card className="max-w-md">
      <CardHeader>
        <CardTitle>User Profile</CardTitle>
        <CardDescription></CardDescription>
      </CardHeader>
      <CardContent className="grid gap-6">
        <div className="flex items-center justify-between space-x-4">
          <div className="flex items-center space-x-4">
            <Avatar>
              <AvatarImage src={avatar} alt={name} />
              <AvatarFallback>{name[0]}</AvatarFallback>
            </Avatar>
            <div>
              <p className="text-sm font-medium leading-none">{name}</p>
              <p className="text-sm text-muted-foreground">{username}</p>
            </div>
          </div>
          {/* <Popover>
            <PopoverTrigger asChild>
              <Button variant="outline" className="ml-auto">
                Owner{" "}
                <ChevronDownIcon className="ml-2 h-4 w-4 text-muted-foreground" />
              </Button>
            </PopoverTrigger>
            <PopoverContent className="p-0" align="end">
              <Command>
                <CommandInput placeholder="Select new role..." />
                <CommandList>
                  <CommandEmpty>No roles found.</CommandEmpty>
                  <CommandGroup>
                    <CommandItem className="teamaspace-y-1 flex flex-col items-start px-4 py-2">
                      <p>Viewer</p>
                      <p className="text-sm text-muted-foreground">
                        Can view and comment.
                      </p>
                    </CommandItem>
                    <CommandItem className="teamaspace-y-1 flex flex-col items-start px-4 py-2">
                      <p>Developer</p>
                      <p className="text-sm text-muted-foreground">
                        Can view, comment and edit.
                      </p>
                    </CommandItem>
                    <CommandItem className="teamaspace-y-1 flex flex-col items-start px-4 py-2">
                      <p>Billing</p>
                      <p className="text-sm text-muted-foreground">
                        Can view, comment and manage billing.
                      </p>
                    </CommandItem>
                    <CommandItem className="teamaspace-y-1 flex flex-col items-start px-4 py-2">
                      <p>Owner</p>
                      <p className="text-sm text-muted-foreground">
                        Admin-level access to all resources.
                      </p>
                    </CommandItem>
                  </CommandGroup>
                </CommandList>
              </Command>
            </PopoverContent>
          </Popover> */}
          {/* show points */}
          <div className="flex items-center space-x-4">
            <p className="text-sm font-medium leading-none">{`Points: ${points}`}</p>
          </div>
        </div>

        <div>
          <p className="font-semibold mb-2">All Achievements</p>
          <ul className="space-y-1 list-disc list-inside text-muted-foreground">
            {achievements?.length > 0
              ? achievements.map((achievement) => (
                  <li
                    className="text-sm font-medium "
                    key={achievement.achievementId}
                  >
                    {achievement.title}
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

import { io } from "socket.io-client";
import { AchievementResponse } from "../../hooks/api";

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
